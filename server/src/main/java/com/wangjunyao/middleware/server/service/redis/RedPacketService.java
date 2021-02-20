package com.wangjunyao.middleware.server.service.redis;

import com.wangjunyao.middleware.server.dto.RedPacketDto;
import com.wangjunyao.middleware.server.service.IRedPacketService;
import com.wangjunyao.middleware.server.service.IRedService;
import com.wangjunyao.middleware.server.utils.RedPackedUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
@Transactional(rollbackFor = Exception.class)
public class RedPacketService implements IRedPacketService {

    private Logger log = LoggerFactory.getLogger(RedPacketService.class);

    /**
     * 存储至缓存系统redis时定义的key前缀
     */
    private static final String keyPrefix = "redis:red:packet:";

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private IRedService redService;

    /**
     * 发红包
     * @param dto
     * @return
     */
    @Override
    public String handOut(RedPacketDto dto) throws Exception {
        //红包个数
        Integer total = dto.getTotal();
        //总金额 - 单位：分
        Integer amount = dto.getAmount();

        //判断参数的合法性
        if (total > 0 && amount > 0){
            //采用二倍均值法生成随机金额列表
            List<Integer> list = RedPackedUtil.divideRedPackage(amount, total);
            //生成红包全局唯一标识串
            String timestamp = String.valueOf(System.nanoTime());
            //根据缓存key的前缀与其他信息拼接成一个新的用于存储随机金额列表的key
            String redId = new StringBuilder(keyPrefix).append(dto.getUserId())
                    .append(":").append(timestamp).toString();
            //将随机金额列表存入缓存List中
            redisTemplate.opsForList().leftPushAll(redId, list);
            //根据缓存key的前缀与其他信息拼接成一个新的用于存储红包总数的key
            String redTotalKey = redId + ":total";
            //将红包总数存入缓存中
            redisTemplate.opsForValue().set(redTotalKey, total);

            //异步记录红包的全局唯一标识串、红包个数和随机金额列表信息至数据库中
            redService.recordRedPacket(dto, redId, list);
            return redId;
        }else {
            throw new Exception("系统异常-分发红包-参数不合法");
        }
    }

    @Override
    public BigDecimal rob(Integer userId, String redId) throws Exception {
        //定义redis操作组件的值操作方法
        ValueOperations valueOperations = redisTemplate.opsForValue();
        //在处理用户抢红包之前，需要先判断一下当前用户是否已经抢过红包了
        //如果已经抢过了，则直接返回红包金额，并在前端显示出来
        Object obj = valueOperations.get(redId + ":" + userId + ":rob");
        if (obj != null){
            return new BigDecimal(obj.toString());
        }
        //”点红包“业务逻辑 - 主要用于判断缓存系统中是否仍然有红包，即红包剩余个数是否大于0
        boolean res = click(redId);
        if (res){
            //这里需要分布式锁
            /******** 分布式锁 ***********/
            //不加锁，会出现  同一用户抢到多个红包金额的现象
            final String lockKey = redId + ":" + userId + ":lock";
            //调用setIfAbsent()方法，其实就是间接实现了分布式锁
            Boolean lock = valueOperations.setIfAbsent(lockKey, redId);
            //表示当前线程获取到了分布式锁
            try{
                if (lock){
                    //res为true，则可以进入“拆红包”业务逻辑的处理
                    //从小红包随机金额列表中弹出一个随机金额
                    Object value = redisTemplate.opsForList().rightPop(redId);
                    if (value != null){
                        //value != null，表示当前弹出的红包金额不为null，即有钱
                        //当前用户抢到一个红包了，则可以进入后续的更新缓存，并将信息记入数据库
                        String redTotalKey = redId + ":total";
                        //更新缓存系统中剩余的红包个数，即红包个数减一
                        Integer currTotal = valueOperations.get(redTotalKey) != null ? (Integer)valueOperations.get(redTotalKey) : 0;
                        valueOperations.set(redTotalKey, currTotal - 1);
                        //将红包金额返回给用户前，在这里金额的单位设置为 “元”
                        BigDecimal result = new BigDecimal(value.toString()).divide(new BigDecimal(100));
                        //将抢到红包时用户的账号信息及抢到的金额等信息记入数据库
                        redService.recordRobRedPacket(userId, redId, new BigDecimal(value.toString()));
                        //将当前抢到红包的用户设置进缓存系统中，表示当前用户已经抢过红包了
                        valueOperations.set(redId + ":" + userId + ":rob", result, 24L, TimeUnit.HOURS);
                        //打印当前用户抢到红包的记录信息
                        log.info("当前用户抢到红包了：userId={} key={} 金额={}", userId, redId, result);
                        //将结果返回
                        return result;
                    }
                }
            }catch (Exception e){
                throw new Exception("系统异常，抢红包，加分布式锁失败");
            }
        }
        return null;
    }

    /**
     * 点红包的业务处理逻辑 - 如果返回true，则代表缓存系统redis还有红包，即剩余个数 > 0
     * @param redId
     * @return
     */
    private boolean click(String redId){
        //定义redis的bean操作组件 - 值操作组件
        ValueOperations valueOperations = redisTemplate.opsForValue();
        //定义用于查询缓存系统中红包剩余个数的key
        String redTotalKey = redId + ":total";
        Object total = valueOperations.get(redTotalKey);
        //判断红包剩余个数total是否大于0，如果大于0，则返回true，代表还有红包
        if (total != null && Integer.valueOf(total.toString()) > 0){
            return true;
        }
        return false;
    }
}
