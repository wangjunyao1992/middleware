package com.wangjunyao.middleware.server.rabbitmq.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wangjunyao.middleware.server.rabbitmq.entity.KnowledgeInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

/**
 * 确认消费模式 - 消费者
 */
@Component
public class KnowledgeConsumer {

    private static final Logger logger = LoggerFactory.getLogger(KnowledgeConsumer.class);

    //定义json序列化和反序列化组件
    @Autowired
    private ObjectMapper objectMapper;

    /**
     * 基于AUTO的确认消费模式 - 消费者
     * 其中：queues指的是监听的队列
     *      containerFactory指的是监听器所在的容器工厂 - 在RabbitmqConfig中已经进行了AUTO消费模式的配置
     */
    @RabbitListener(queues = "${mq.auto.knowledge.queue.name}", containerFactory = "singleListenerContainerAuto")
    public void consumeAutoMsg(@Payload byte[] msg){
        try {
            //监听消费解析消息体
            KnowledgeInfo info = objectMapper.readValue(msg, KnowledgeInfo.class);
            //打印日志
            logger.info("基于AUTO的确认消费模式 - 消费者监听消费消息 - 内容为：{}", info);
        }catch (Exception e){
            logger.error("基于AUTO的确认消费模式 - 消费者监听消费消息 - 发生异常：" + e.fillInStackTrace());
        }
    }

}
