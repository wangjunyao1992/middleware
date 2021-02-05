package com.wangjunyao.middleware.server.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * 在Spring Boot项目中使用Redis进行相关业务操作之前，
 * 强烈建议将自定义注入RedisTemplate与StringRedisTemplate组件的代码加入项目中，
 * 避免出现“非业务性”的令人头疼的问题
 */
@Configuration
public class CommonConfig {

    /**
     * redis连接工厂
     */
    @Autowired
    private RedisConnectionFactory redisConnectionFactory;

    /**
     * 缓存操作组件RedisTemplate的自定义配置
     * @return
     */
    @Bean
    public RedisTemplate<String, Object> redisTemplate(){
        //定义RedisTemplate实例
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        //设置redis连接工厂
        redisTemplate.setConnectionFactory(redisConnectionFactory);
        //指定key序列化策略为String序列化，value为jdk自带的序列化策略
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new JdkSerializationRedisSerializer());
        //指定hashKey序列化策略为String序列化 - 针对hash散列存储
        redisTemplate.setHashKeySerializer(new StringRedisSerializer());
        return redisTemplate;
    }

    /**
     * 缓存操作组件StringRedisTemplate
     * @return
     */
    @Bean
    public StringRedisTemplate stringRedisTemplate(){
        //采用默认配置即可 - 后续有自定义配置时则在此次添加即可
        StringRedisTemplate stringRedisTemplate = new StringRedisTemplate();
        stringRedisTemplate.setConnectionFactory(redisConnectionFactory);
        return stringRedisTemplate;
    }

}
