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
 * 确认消费模式 - 手动确认消费 - 生产者
 */
@Component
public class KnowledgeManualPublisher {

    private static final Logger logger = LoggerFactory.getLogger(KnowledgeManualPublisher.class);

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private Environment environment;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    /**
     * 基于MANUAL机制 - 生产者发生消息
     * @param info
     */
    public void sendManualMsg(KnowledgeInfo info){
        try {
            if (info != null){
                //设置消息传输的格式为json
                rabbitTemplate.setMessageConverter(new Jackson2JsonMessageConverter());
                //设置交换机
                rabbitTemplate.setExchange(environment.getProperty("mq.manual.knowledge.exchange.name"));
                //设置路由
                rabbitTemplate.setRoutingKey(environment.getProperty("mq.manual.knowledge.routing.key.name"));
                //创建消息
                Message message = MessageBuilder.withBody(objectMapper.writeValueAsBytes(info))
                        .setDeliveryMode(MessageDeliveryMode.PERSISTENT).build();
                //发送消息
                rabbitTemplate.convertAndSend(message);
                //打印日志
                logger.info("基于MANUAL机制 - 生产者发送消息 - 内容为：{}", info);
            }
        }catch (Exception e){
            logger.error("基于MANUAL机制 - 生产者发送消息 - 发生异常：{}", info, e.fillInStackTrace());
        }
    }


}
