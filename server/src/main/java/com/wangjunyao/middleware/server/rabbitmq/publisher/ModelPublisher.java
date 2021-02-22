package com.wangjunyao.middleware.server.rabbitmq.publisher;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Strings;
import com.wangjunyao.middleware.server.entity.EventInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

/**
 * 消息模型 - 生产者
 */
@Component
public class ModelPublisher {

    private static final Logger logger = LoggerFactory.getLogger(ModelPublisher.class);

    /**
     * json序列化和反序列化组件
     */
    @Autowired
    private ObjectMapper objectMapper;

    /**
     * 定义发送消息的操作组件
     */
    @Autowired
    private RabbitTemplate rabbitTemplate;

    /**
     * 读取环境变量的实例
     */
    @Autowired
    private Environment environment;

    /**
     * 发送消息 - 基于fanoutExchange消息模型
     * @param info
     */
    public void sendMsgFanout(EventInfo info){
        //判断是否为null
        if (info != null){
            try {
                //定义消息的传输格式，这里为json
                rabbitTemplate.setMessageConverter(new Jackson2JsonMessageConverter());
                //设置广播式交换机FanoutExchange
                rabbitTemplate.setExchange(environment.getProperty("mq.fanout.exchange.name"));
                //创建消息实例
                Message message = MessageBuilder.withBody(objectMapper.writeValueAsBytes(info)).build();
                //发送消息
                rabbitTemplate.convertAndSend(message);
                //打印日志
                logger.info("消息模型fanoutExchange - 生产者 - 发送消息：{}", info);
            }catch (Exception e){
                logger.info("消息模型fanoutExchange - 生产者 - 发送消息发生异常：{}", info, e.fillInStackTrace());
            }
        }
    }

    /**
     * 发送消息 - 基于directExchange消息模型 - one
     * @param info
     */
    public void sendMsgDirectOne(EventInfo info){
        //判断对象是否为null
        if (info != null){
            try {
                //定义消息的传输格式，这里为json
                rabbitTemplate.setMessageConverter(new Jackson2JsonMessageConverter());
                //设置交换机
                rabbitTemplate.setExchange(environment.getProperty("mq.direct.exchange.name"));
                //设置路由1
                rabbitTemplate.setRoutingKey(environment.getProperty("mq.direct.routing.key.one.name"));
                //创建消息
                Message message = MessageBuilder.withBody(objectMapper.writeValueAsBytes(info)).build();
                //发送消息
                rabbitTemplate.convertAndSend(message);
                //打印日志
                logger.info("消息模型DirectExchange - one - 生产者 - 发送消息：{}", info);
            }catch (Exception e){
                logger.info("消息模型DirectExchange - one - 生产者 - 发送消息发生异常：{}", info, e.fillInStackTrace());
            }
        }
    }

    /**
     * 发送消息 - 基于directExchange消息模型 - two
     * @param info
     */
    public void sendMsgDirectTwo(EventInfo info){
        //判断对象是否为null
        if (info != null){
            try {
                //定义消息的传输格式，这里为json
                rabbitTemplate.setMessageConverter(new Jackson2JsonMessageConverter());
                //设置交换机
                rabbitTemplate.setExchange(environment.getProperty("mq.direct.exchange.name"));
                //设置路由1
                rabbitTemplate.setRoutingKey(environment.getProperty("mq.direct.routing.key.two.name"));
                //创建消息
                Message message = MessageBuilder.withBody(objectMapper.writeValueAsBytes(info)).build();
                //发送消息
                rabbitTemplate.convertAndSend(message);
                //打印日志
                logger.info("消息模型DirectExchange - two - 生产者 - 发送消息：{}", info);
            }catch (Exception e){
                logger.info("消息模型DirectExchange - two - 生产者 - 发送消息发生异常：{}", info, e.fillInStackTrace());
            }
        }
    }

    /**
     * 发送消息 - 基于TopicExchange消息模型
     */
    public void sendMsgTopic(String msg, String routingKey){
        //判断是否为null
        if (!Strings.isNullOrEmpty(msg) && !Strings.isNullOrEmpty(routingKey)){
            try {
                //设置消息的传输格式为json
                rabbitTemplate.setMessageConverter(new Jackson2JsonMessageConverter());
                //指定交换机
                rabbitTemplate.setExchange(environment.getProperty("mq.topic.exchange.name"));
                //指定路由的实际取值，根据不同取值，RabbitMQ将自行进行匹配通配符，从而路由到不同的队列中
                rabbitTemplate.setRoutingKey(routingKey);
                //创建消息
                Message message = MessageBuilder.withBody(msg.getBytes("utf-8")).build();
                //发送消息
                rabbitTemplate.convertAndSend(message);
                //打印日志
                logger.info("消息模型topicExchange - 生产者 - 发送消息：{} 路由：{}", msg, routingKey);
            }catch (Exception e){
                logger.info("消息模型topicExchange - 生产者 - 发送消息发生异常：{} 路由：{}", msg, routingKey);
            }
        }
    }

}
