package com.wangjunyao.middleware.server;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wangjunyao.middleware.server.entity.User;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * RedisTemplate与StringRedisTemplate操作组件都可以用于操作存储字符串类型的数据信息。
 * RedisTemplate，还适用于其他的数据类型的存储，如列表List、集合Set、有序集合SortedSet和哈希Hash等。
 */
@SpringBootTest(classes = MainApplication.class)
@RunWith(SpringJUnit4ClassRunner.class)
public class RedisTest {

    private static final Logger log = LoggerFactory.getLogger(RedisTest.class);

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * RedisTemplate的特例，专门用于处理缓存中Value的数据类型为字符串String的数据，
     * 包括String类型的数据，和序列化后为String类型的字符串数据
     */
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 采用RedisTemplate将字符串信息写入缓存中，并读取出来
     */
    @Test
    public void redisTest1(){
        log.info("开始RedisTemplate操作组件实战... ...");
        //定义字符串内容以及存入缓存的key
        final String content = "RedisTemplate字符串信息";
        final String key = "redis:template:one:string";
        //redis通用的操作组件
        ValueOperations valueOperations = redisTemplate.opsForValue();
        //将字符串信息写入缓存
        log.info("写入缓存中的内容：{}", content);
        valueOperations.set(key, content);
        //从缓存中读取内容
        Object result = valueOperations.get(key);
        log.info("读取出来的内容：{}", result);
    }

    /**
     * json序列化与反序列化
     */
    @Autowired
    private ObjectMapper objectMapper;

    /**
     * 采用RedisTemplate将对象信息序列化为JSON格式的字符串后写入缓存中，
     * 然后将其读取出来，最后反序列化解析其中的内容
     */
    @Test
    public void redisTest2() throws Exception {
        log.info("开始RedisTemplate操作组件实战... ...");
        User user = new User(1, "debug", "阿修罗");
        //redis通用的操作组件
        ValueOperations valueOperations = redisTemplate.opsForValue();

        //将序列化后的信息写入缓存中
        final String key = "redis:template:two:object";
        final String content = objectMapper.writeValueAsString(user);

        valueOperations.set(key, content);

        //从缓存中读取内容
        Object result = valueOperations.get(key);
        if (result != null){
            User resultUser = objectMapper.readValue(result.toString(), User.class);
            log.info("读取缓存内容并反序列化后的结果：{}", resultUser);
        }
    }

    /**
     * 采用StringRedisTemplate将字符串信息写入缓存中，并读取出来
     */
    @Test
    public void redisTest3(){
        log.info("开始RedisTemplate操作组件实战... ...");
        //定义字符串内容及存入缓存的key
        final String content = "StringRedisTemp字符串信息";
        final String key = "redis:three";
        //redis通用操作组件
        ValueOperations<String, String> stringStringValueOperations = stringRedisTemplate.opsForValue();
        //将字符串信息写入缓存中
        log.info("写入缓存中的内容：{}", content);
        stringStringValueOperations.set(key, content);
        //从缓存中读取内容
        String result = stringStringValueOperations.get(key);
        log.info("读取出来的内容：{}", result);
    }


    /**
     * 采用StringRedisTemplate将对象信息序列化为JSON格式字符串后写入缓存中，
     * 然后将其读取出来，最后反序列化解析其中的内容
     */
    @Test
    public void redisTest4() throws Exception {
        log.info("开始RedisTemplate操作组件实战... ...");
        User user = new User(2, "SteadyJack", "阿修罗");
        //redis通用操作组件
        ValueOperations<String, String> stringStringValueOperations = stringRedisTemplate.opsForValue();
        //将序列化后的信息写入缓存中
        final String key = "redis:four";
        final String content = objectMapper.writeValueAsString(user);
        stringStringValueOperations.set(key, content);
        log.info("写入缓存对象的信息：{}", user);
        //从缓存中读取内容
        String result = stringStringValueOperations.get(key);
        if (result != null){
            User resultUser = objectMapper.readValue(result, User.class);
            log.info("读取缓存内容并反序列化后的结果：{}", resultUser);
        }
    }

}
