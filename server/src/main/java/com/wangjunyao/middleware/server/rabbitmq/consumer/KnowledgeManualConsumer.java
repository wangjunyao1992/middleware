package com.wangjunyao.middleware.server.rabbitmq.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.Channel;
import com.wangjunyao.middleware.server.rabbitmq.entity.KnowledgeInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.ChannelAwareMessageListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 由于其采用手动确认消费的模式，因而该监听器需要实现“RabbitMQ通道确认消息监听器”，
 * 即ChannelAwareMessageListener接口，并实现onMessage()方法。
 * 在该方法体中实现相应的业务逻辑，并在执行完业务逻辑之后，手动调用channel.basicAck()和channel.basicReject()
 * 等方法确认消费队列中的消息，最终将该消息从队列中移除
 *
 *
 * 确认消费模式 - 手动确认消费 - 监听器
 *
 */
@Component("knowledgeManualConsumer")
public class KnowledgeManualConsumer implements ChannelAwareMessageListener {

    private static final Logger logger = LoggerFactory.getLogger(KnowledgeManualConsumer.class);

    //定义json序列化和反序列化组件
    @Autowired
    private ObjectMapper objectMapper;

    /**
     * 监听消费消息
     * @param message
     * @param channel
     * @throws Exception
     */
    @Override
    public void onMessage(Message message, Channel channel) throws Exception {
        //获取消息属性
        MessageProperties messageProperties = message.getMessageProperties();
        //获取消息分发时的全局唯一标识
        long deliveryTag = messageProperties.getDeliveryTag();
        try {
            //获得消息体
            byte[] msg = message.getBody();
            //解析消息体
            KnowledgeInfo info = objectMapper.readValue(msg, KnowledgeInfo.class);
            //打印日志信息
            logger.info("确认消费模式 - 人为手动确认消费 - 监听器监听消费消息 - 内容为：{}", info);
            /**
             * 执行完业务逻辑后，手动进行确认消费，其中：
             * 第一个参数为：消息的分发标识（全局唯一）
             * 第二个参数为：是否允许批量确认消费（这里设置为true）
             */
            /**
             * 成功确认
             * void basicAck(long deliveryTag, boolean multiple) throws IOException;
             *
             * deliveryTag:该消息的index
             *
             * multiple：是否批量. true：将一次性ack所有小于deliveryTag的消息。
             */
            channel.basicAck(deliveryTag, true);
        }catch (Exception e){
            logger.info("确认消费模式 - 手动确认消费 - 监听器监听消费消息 - 发生异常：", e.fillInStackTrace());
            //如果在处理消息的过程中发生了异常，则依旧需要人为手动确认消费掉该消息
            //否则该消息将一直留在队列中，从而导致消息的重复消费
            /**
             * 失败确认
             *
             * void basicNack(long deliveryTag, boolean multiple, boolean requeue)
             *
             * deliveryTag:该消息的index。
             *
             * multiple：是否批量. true：将一次性拒绝所有小于deliveryTag的消息。
             *
             * requeue：被拒绝的是否重新入队列。
             *
             *
             *
             * void basicReject(long deliveryTag, boolean requeue) throws IOException;
             *
             * deliveryTag:该消息的index。
             *
             * requeue：被拒绝的是否重新入队列。
             *
             * channel.basicNack 与 channel.basicReject 的区别在于basicNack可以批量拒绝多条消息，而basicReject一次只能拒绝一条消息。
             *
             */
            channel.basicReject(deliveryTag, false);
        }
    }
}
