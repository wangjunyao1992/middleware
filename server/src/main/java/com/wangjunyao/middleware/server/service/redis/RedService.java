package com.wangjunyao.middleware.server.service.redis;

import com.wangjunyao.middleware.model.entity.RedDetail;
import com.wangjunyao.middleware.model.entity.RedRecord;
import com.wangjunyao.middleware.model.entity.RedRobRecord;
import com.wangjunyao.middleware.model.mapper.RedDetailMapper;
import com.wangjunyao.middleware.model.mapper.RedRecordMapper;
import com.wangjunyao.middleware.model.mapper.RedRobRecordMapper;
import com.wangjunyao.middleware.server.dto.RedPacketDto;
import com.wangjunyao.middleware.server.service.IRedService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Service
@EnableAsync
public class RedService implements IRedService {

    private Logger log = LoggerFactory.getLogger(RedService.class);

    /**
     * 发红包时红包全局唯一标识串等信息操作接口
     */
    @Autowired
    private RedRecordMapper redRecordMapper;

    /**
     * 发红包时随机数算法生成的随机金额列表等信息操作接口
     */
    @Autowired
    private RedDetailMapper redDetailMapper;

    /**
     * 抢红包时
     */
    @Autowired
    private RedRobRecordMapper redRobRecordMapper;

    /**
     * 发红包记录 - 异步方式
     * @param dto 红包总金额 + 个数
     * @param redId 红包全局唯一标识串
     * @param list 红包随机金额列表
     */
    @Async
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void recordRedPacket(RedPacketDto dto, String redId, List<Integer> list) {
        RedRecord redRecord = new RedRecord();
        //设置字段的取值信息
        redRecord.setUserId(dto.getUserId());
        redRecord.setRedPacked(redId);
        redRecord.setTotal(dto.getTotal());
        redRecord.setAmount(BigDecimal.valueOf(dto.getAmount()));
        redRecord.setCreateTime(new Date());
        //插入数据库
        redRecordMapper.insertSelective(redRecord);
        //定义红包随机金额明细实体类对象
        RedDetail detail = null;
        for (Integer i : list) {
            detail = new RedDetail();
            detail.setRecordId(redRecord.getId());
            detail.setAmount(BigDecimal.valueOf(i));
            detail.setCreateTime(new Date());
            //插入数据库
            redDetailMapper.insertSelective(detail);
        }

    }

    @Async
    @Override
    public void recordRobRedPacket(Integer userId, String redId, BigDecimal amount) {
        RedRobRecord redRobRecord = new RedRobRecord();
        //设置用户账号id
        redRobRecord.setUserId(userId);
        //设置红包全局唯一标识
        redRobRecord.setRedPacket(redId);
        //设置抢到的金额
        redRobRecord.setAmount(amount);
        //设置抢到的时间
        redRobRecord.setRobTime(new Date());
        //将实体对象信息插入数据库中
        redRobRecordMapper.insertSelective(redRobRecord);
    }
}
