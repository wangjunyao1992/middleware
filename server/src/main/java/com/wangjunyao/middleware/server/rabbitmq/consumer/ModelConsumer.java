package com.wangjunyao.middleware.server.rabbitmq.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wangjunyao.middleware.server.entity.EventInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

/**
 * 消息模型 - 消费者
 */
@Component
public class ModelConsumer {

    private static final Logger logger = LoggerFactory.getLogger(ModelConsumer.class);

    //json序列化和反序列化组件
    @Autowired
    public ObjectMapper objectMapper;

    /**
     * 监听并消费队列中的消息 - fanoutExchangeOne
     */
    @RabbitListener(queues = "${mq.fanout.queue.one.name}", containerFactory = "singleListenerContainer")
    public void consumeFanoutMsgOne(@Payload byte[] msg){
        try {
            //监听消费队列中的消息，并进行解析处理
            EventInfo info = objectMapper.readValue(msg, EventInfo.class);
            logger.info("消息模型fanoutExchange - one - 消费者 - 监听消费到消息：{}", info);
        }catch (Exception e){
            logger.error("消息模型 - 消费者 - 发生异常：", e.fillInStackTrace());
        }

    }

    /**
     * 监听并消费队列中的消息 - fanoutExchangeTwo
     */
    @RabbitListener(queues = "${mq.fanout.queue.two.name}", containerFactory = "singleListenerContainer")
    public void consumeFanoutMsgTwo(@Payload byte[] msg){
        try {
            //监听消费队列中的消息，并进行解析处理
            EventInfo info = objectMapper.readValue(msg, EventInfo.class);
            logger.info("消息模型fanoutExchange - two - 消费者 - 监听消费到消息：{}", info);
        }catch (Exception e){
            logger.error("消息模型 - 消费者 - 发生异常：", e.fillInStackTrace());
        }
    }

    /**
     * 监听并消费队列中的消息 - 消息模型directExchangeOne
     */
    @RabbitListener(queues = "${mq.direct.queue.one.name}", containerFactory = "singleListenerContainer")
    public void consumeDirectMsgOne(@Payload byte[] msg){
        try {
            //监听消费队列中的消息，并进行解析处理
            EventInfo info = objectMapper.readValue(msg, EventInfo.class);
            logger.info("消息模型directExchange - one - 消费者 - 监听消费到消息：{}", info);
        }catch (Exception e){
            logger.error("消息模型 - 消费者 - 发生异常：", e.fillInStackTrace());
        }
    }

    /**
     * 监听并消费队列中的消息 - 消息模型directExchangeTwo
     */
    @RabbitListener(queues = "${mq.direct.queue.two.name}", containerFactory = "singleListenerContainer")
    public void consumeDirectMsgTwo(@Payload byte[] msg){
        try {
            //监听消费队列中的消息，并进行解析处理
            EventInfo info = objectMapper.readValue(msg, EventInfo.class);
            logger.info("消息模型directExchange - two - 消费者 - 监听消费到消息：{}", info);
        }catch (Exception e){
            logger.error("消息模型 - 消费者 - 发生异常：", e.fillInStackTrace());
        }
    }

    /**
     * 监听并消费队列中的消息 - 消息模型directExchangeThree
     */
    @RabbitListener(queues = "${mq.direct.queue.three.name}", containerFactory = "singleListenerContainer")
    public void consumeDirectMsgThree(@Payload byte[] msg){
        try {
            //监听消费队列中的消息，并进行解析处理
            EventInfo info = objectMapper.readValue(msg, EventInfo.class);
            logger.info("消息模型directExchange - three - 消费者 - 监听消费到消息：{}", info);
        }catch (Exception e){
            logger.error("消息模型 - 消费者 - 发生异常：", e.fillInStackTrace());
        }
    }

    /**
     * 监听并消费队列中的消息 - topicExchange - * 通配符
     */
    @RabbitListener(queues = "${mq.topic.queue.one.name}", containerFactory = "singleListenerContainer")
    public void consumeTopicMsgOne(@Payload byte[] msg){
        try {
            String message = new String(msg, "utf-8");
            logger.info("消息模型topicExchange - * - 消费者 - 监听消费到消息：{}", message);
        }catch (Exception e){
            logger.error("消息模型topicExchange - * - 消费者 - 监听消费发生异常：", e.fillInStackTrace());
        }
    }

    /**
     * 监听并消费队列中的消息 - topicExchange - # 通配符
     */
    @RabbitListener(queues = "${mq.topic.queue.two.name}", containerFactory = "singleListenerContainer")
    public void consumeTopicMsgTwo(@Payload byte[] msg){
        try {
            String message = new String(msg, "utf-8");
            logger.info("消息模型topicExchange - # - 消费者 - 监听消费到消息：{}", message);
        }catch (Exception e){
            logger.error("消息模型topicExchange - # - 消费者 - 监听消费发生异常：", e.fillInStackTrace());
        }
    }

}
