package com.wangjunyao.middleware.server.rabbitmq.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wangjunyao.middleware.server.entity.Person;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

/**
 * 基本消息模型 - 消费者
 */
@Component
public class BasicConsumer {

    private static final Logger logger = LoggerFactory.getLogger(BasicConsumer.class);

    //定义json序列化和反序列化
    @Autowired
    private ObjectMapper objectMapper;

    //监听并接收消费队列中的消息 - 这里采用单一容器工厂实例
    //由于消息本质上是一串二进制数据流，因而监听接收的消息采用字节数组接收
    @RabbitListener(queues = "${mq.basic.info.queue.name}", containerFactory = "singleListenerContainer")
    public void consumeMsg(@Payload byte[] msg){
        try {
            String message = new String(msg, "utf-8");
            logger.info("基本消息模型 - 消费者 - 监听消费到消息 - 1：{}", message);
        }catch (Exception e){
            logger.error("基本消息模型 - 消费者 - 发生异常：", e.fillInStackTrace());
        }
    }

    @RabbitListener(queues = "${mq.object.info.queue.name}", containerFactory = "singleListenerContainer")
    public void consumeObjectMsg(@Payload Person person){
        try {
            logger.info("基本消息模型 - 监听消费处理对象信息 - 消费者 - 监听消费消息 - 1：{}", person);
        }catch (Exception e){
            logger.error("基本消息模型 - 监听消费处理对象信息 - 消费者 - 发生异常：", e.fillInStackTrace());
        }
    }

}
