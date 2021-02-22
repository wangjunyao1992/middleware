package com.wangjunyao.middleware.server.rabbitmq.publisher;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Strings;
import com.sun.xml.internal.stream.buffer.sax.Properties;
import com.wangjunyao.middleware.server.entity.Person;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.AbstractJavaTypeMapper;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

/**
 * 生产者
 */
@Component
public class BasicPublisher {

    private static final Logger logger = LoggerFactory.getLogger(BasicPublisher.class);

    /**
     * 定义json序列化和反序列化实例
     */
    @Autowired
    private ObjectMapper objectMapper;

    /**
     * 定义RabbitMQ消息操作组件
     */
    @Autowired
    private RabbitTemplate rabbitTemplate;

    /**
     * 定义环境变量读取实例
     */
    @Autowired
    private Environment environment;

    public void sendMsg(String msg){
        //判断字符串值是否为空
        if (!Strings.isNullOrEmpty(msg)){
            try{
                //定义消息传输的格式为json字符串格式
                rabbitTemplate.setMessageConverter(new Jackson2JsonMessageConverter());
                //指定消息模型中的交换机
                rabbitTemplate.setExchange(environment.getProperty("mq.basic.info.exchange.name"));
                //指定消息模型中的路由
                rabbitTemplate.setRoutingKey(environment.getProperty("mq.basic.info.routing.key.name"));
                //将字符串值转化为待发送的消息，即一串二进制的数据流
                Message message = MessageBuilder.withBody(msg.getBytes("utf-8")).build();
                //转化并发送消息
                rabbitTemplate.convertAndSend(message);
                //打印日志信息
                logger.info("基本消息模型-生产者-发送消息：{}", msg);
            }catch (Exception e){
                logger.error("基本消息模型-生产者-发送消息发生异常：{}", msg, e.fillInStackTrace());
            }
        }
    }

    public void sendObjectMsg(Person person){
        //判断对象是否为null
        if (person != null){
            try {
                //定义消息传输的格式为json字符串格式
                rabbitTemplate.setMessageConverter(new Jackson2JsonMessageConverter());
                //指定消息模型中的交换机
                rabbitTemplate.setExchange(environment.getProperty("mq.object.info.exchange.name"));
                //指定消息模型中的路由
                rabbitTemplate.setRoutingKey(environment.getProperty("mq.object.info.routing.key.name"));
                rabbitTemplate.convertAndSend(person, new MessagePostProcessor() {
                    @Override
                    public Message postProcessMessage(Message message) throws AmqpException {
                        //获取消息的属性
                        MessageProperties messageProperties = message.getMessageProperties();
                        //设置消息的持久化模式
                        messageProperties.setDeliveryMode(MessageDeliveryMode.PERSISTENT);
                        //设置消息的类型
                        messageProperties.setHeader(AbstractJavaTypeMapper.DEFAULT_CONTENT_CLASSID_FIELD_NAME, Person.class);
                        return message;
                    }
                });
                //打印日志信息
                logger.info("基本消息模型 - 生产者 - 发送对象类型的消息：{}", person);
            }catch (Exception e){
                logger.error("基本消息模型 - 生产者 - 发送对象类型的消息发生异常：{}", person, e.fillInStackTrace());
            }
        }
    }

}
