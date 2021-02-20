package com.wangjunyao.middleware.server.service.redis;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Strings;
import com.wangjunyao.middleware.model.entity.Item;
import com.wangjunyao.middleware.model.mapper.ItemMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class CachePassService {

    private static final Logger log = LoggerFactory.getLogger(CachePassService.class);

    /**
     * 定义mapper
     */
    @Autowired
    private ItemMapper itemMapper;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    private static final String keyPrefix = "item:";

    /**
     * 获取商品详情，如果缓存有，则从缓存中获取；如果没有，则从数据库查询，并将查询结果加入缓存中
     * @param itemCode
     * @return
     */
    public Object getItemInfo(String itemCode) throws Exception {
        //定义商品对象
        Item item = null;
        //定义缓存中真正的key：由前缀和商品编码组成
        final String key = keyPrefix + itemCode;
        //定义redis的操作组件ValueOperations
        ValueOperations valueOperations = redisTemplate.opsForValue();
        if (redisTemplate.hasKey(key)){
            log.info("获取商品详情 - 缓存中存在该商品 - 商品编号为：{}", itemCode);
            //从缓存中查询该商品详情
            Object res = valueOperations.get(key);
            if (res != null && !Strings.isNullOrEmpty(res.toString())){
                item = objectMapper.readValue(res.toString(), Item.class);
            }
        }else {
            log.info("获取商品详情 - 缓存中不存在该商品 - 商品编号为：{}", itemCode);
            item = itemMapper.selectByCode(itemCode);
            if (item != null){
                valueOperations.set(key, objectMapper.writeValueAsString(item));
            }else {
                log.info("数据库中也不存在该商品信息... ...");
                //过期时效设置为30分钟，根据实际业务定
                valueOperations.set(key, "", 30L, TimeUnit.MINUTES);
            }
        }
        return item;
    }
}
