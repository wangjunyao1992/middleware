package com.wangjunyao.middleware.server.rabbitmq.publisher;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wangjunyao.middleware.server.rabbitmq.entity.KnowledgeInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.core.MessageDeliveryMode;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

/**
 * 确认消费模式 - 生产者
 */
@Component
public class KnowledgePublisher {

    private static final Logger logger = LoggerFactory.getLogger(KnowledgePublisher.class);

    //定义json序列化和反序列化组件
    @Autowired
    private ObjectMapper objectMapper;

    /**
     * 读取环境变量的实例
     */
    @Autowired
    private Environment environment;

    /**
     * rabbitMQ操作组件
     */
    @Autowired
    private RabbitTemplate rabbitTemplate;

    public void sendAutoMsg(KnowledgeInfo info){
        try{
            if (info != null){
                //设置消息的传输格式
                rabbitTemplate.setMessageConverter(new Jackson2JsonMessageConverter());;
                //设置交换机
                rabbitTemplate.setExchange(environment.getProperty("mq.auto.knowledge.exchange.name"));
                //设置路由
                rabbitTemplate.setRoutingKey(environment.getProperty("mq.auto.knowledge.routing.key.name"));
                //创建消息。其中，对消息设置持久化策略
                Message message = MessageBuilder.withBody(objectMapper.writeValueAsBytes(info))
                        .setDeliveryMode(MessageDeliveryMode.PERSISTENT).build();
                //发送消息
                rabbitTemplate.convertAndSend(message);
                logger.info("基于AUTO机制 - 生产者发送消息 - 内容为：{}", info);
            }
        }catch (Exception e){
            logger.error("基于AUTO机制 - 生产者发送消息 - 发生异常：{}", info, e.fillInStackTrace());
        }
    }

}
