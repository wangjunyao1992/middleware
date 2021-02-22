package com.wangjunyao.middleware.server.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.support.CorrelationData;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.amqp.SimpleRabbitListenerContainerFactoryConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

@Configuration
public class RabbitmqConfig {

    private static final Logger logger = LoggerFactory.getLogger(RabbitmqConfig.class);

    //自动装配RabbitMQ的链接工厂实例
    @Autowired
    private CachingConnectionFactory connectionFactory;

    //自动装配消息监听器所在的容器工厂配置类实例
    @Autowired
    private SimpleRabbitListenerContainerFactoryConfigurer factoryConfigurer;

    /**
     * 单一消费者实例的配置
     * @return
     */
    @Bean(name = "singleListenerContainer")
    public SimpleRabbitListenerContainerFactory singleListenerContainer(){
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        //设置容器工厂所用的实例
        factory.setConnectionFactory(connectionFactory);
        //设置消息在传输中的格式，这里采用JSON的格式进行传输
        factory.setMessageConverter(new Jackson2JsonMessageConverter());
        //设置并发消费者实例的初始化数量，这里为1个
        factory.setConcurrentConsumers(1);
        //设置并发消费者实例的最大数量，这里为1个
        factory.setMaxConcurrentConsumers(1);
        //设置并发消费者示例中每个实例拉取的消息数量，这里为1个
        factory.setPrefetchCount(1);
        return factory;
    }

    @Bean(name = "multiListenerContainer")
    public SimpleRabbitListenerContainerFactory multiListenerContainer(){
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        //设置容器工厂所用的实例
        factoryConfigurer.configure(factory, connectionFactory);
        //设置消息在传输中的格式，这里采用JSON的格式进行传输
        factory.setMessageConverter(new Jackson2JsonMessageConverter());
        //设置消息的确认消费模式，这里为none，表示不需要确认消费
        factory.setAcknowledgeMode(AcknowledgeMode.NONE);
        //设置并发消费者实例的初始化数量，这里为1个
        factory.setConcurrentConsumers(10);
        //设置并发消费者实例的最大数量，这里为1个
        factory.setMaxConcurrentConsumers(15);
        //设置并发消费者示例中每个实例拉取的消息数量，这里为1个
        factory.setPrefetchCount(10);
        return factory;
    }

    //自定义配置RabbitMQ发送消息的操作组件RabbitTemplate
    @Bean
    public RabbitTemplate rabbitTemplate(){
        //设置 “发送消息后进行确认”
        //设置消息发送确认机制 - 生产确认
        connectionFactory.setPublisherConfirms(true);
        //设置 “发送消息后返回确认信息”
        //设置消息发送确认机制 - 发送成功时返回反馈信息
        connectionFactory.setPublisherReturns(true);
        //构造发送消息组件实例对象
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        //发送消息后，如果发送成功，则输出 “消息发送成功” 的反馈信息
        //设置消息发送确认机制：即发送成功时打印日志
        rabbitTemplate.setConfirmCallback(new RabbitTemplate.ConfirmCallback() {
            @Override
            public void confirm(CorrelationData correlationData, boolean ack, String cause) {
                logger.info("消息发送成功：correlationData({}), ack({}), cause({})", correlationData, ack, cause);
            }
        });
        //发送消息后，如果发送失败，则输出 “消息发送失败 - 消息丢失” 的反馈信息
        //设置消息发送确认机制：即发送完消息后打印反馈信息，如消息是否丢失邓
        rabbitTemplate.setReturnCallback(new RabbitTemplate.ReturnCallback() {
            @Override
            public void returnedMessage(Message message, int replyCode, String replyText, String exchange, String routingKey) {
                logger.info("消息丢失：exchange({}), route({}), replyCode({}), replyText({}), message:{}",
                        exchange, routingKey, replyCode, replyText, message);
            }
        });
        return rabbitTemplate;
    }

    //读取配置文件的环境变量实例
    @Autowired
    private Environment environment;

    /**
     * 创建简单消息模型：队列、交换机和路由
     * @return
     */

    //创建队列
    @Bean(name = "basicQueue")
    public Queue basicQueue(){
        return new Queue(environment.getProperty("mq.basic.info.queue.name"), true);
    }

    @Bean(name = "objectQueue")
    public Queue objectQueue(){
        return new Queue(environment.getProperty("mq.object.info.queue.name"), true);
    }

    //创建交换机：这里以DirectExchange为例
    @Bean
    public DirectExchange basicExchange(){
        return new DirectExchange(environment.getProperty("mq.basic.info.exchange.name"), true, false);
    }

    @Bean
    public DirectExchange objectExchange(){
        return new DirectExchange(environment.getProperty("mq.object.info.exchange.name"), true, false);
    }

    //创建绑定
    @Bean
    public Binding basicBinding(){
        return BindingBuilder.bind(basicQueue()).to(basicExchange())
                .with(environment.getProperty("mq.basic.info.routing.key.name"));
    }

    @Bean
    public Binding objectBinding(){
        return BindingBuilder.bind(objectQueue()).to(objectExchange())
                .with(environment.getProperty("mq.object.info.routing.key.name"));
    }

    /**
     * 创建消息模型 - fanoutExchange
     * @return
     */

    //创建队列1
    @Bean(name = "fanoutQueueOne")
    public Queue fanoutQueueOne(){
        return new Queue(environment.getProperty("mq.fanout.queue.one.name"), true);
    }

    //创建队列2
    @Bean(name = "fanoutQueueTwo")
    public Queue fanoutQueueTwo(){
        return new Queue(environment.getProperty("mq.fanout.queue.two.name"), true);
    }

    //创建交换机 - fanoutExchange
    @Bean
    public FanoutExchange fanoutExchange(){
        return new FanoutExchange(environment.getProperty("mq.fanout.exchange.name"), true, false);
    }

    //创建绑定1
    @Bean
    public Binding fanoutBindingOne(){
        return BindingBuilder.bind(fanoutQueueOne()).to(fanoutExchange());
    }

    //创建绑定2
    @Bean
    public Binding fanoutBindingTwo(){
        return BindingBuilder.bind(fanoutQueueTwo()).to(fanoutExchange());
    }


    /**
     * 创建消息模型 - directExchange
     */

    //创建交换机 - directExchange
    @Bean
    public DirectExchange directExchange(){
        return new DirectExchange(environment.getProperty("mq.direct.exchange.name"), true, false);
    }

    //创建队列1
    @Bean
    public Queue directQueueOne(){
        return new Queue(environment.getProperty("mq.direct.queue.one.name"), true);
    }

    //创建队列2
    @Bean
    public Queue directQueueTwo(){
        return new Queue(environment.getProperty("mq.direct.queue.two.name"), true);
    }

    //创建队列3
    @Bean
    public Queue directQueueThree(){
        return new Queue(environment.getProperty("mq.direct.queue.three.name"), true);
    }

    //创建绑定1
    @Bean
    public Binding directBindingOne(){
        return BindingBuilder.bind(directQueueOne())
                .to(directExchange())
                .with(environment.getProperty("mq.direct.routing.key.one.name"));
    }

    //创建绑定2
    @Bean
    public Binding directBindingTwo(){
        return BindingBuilder.bind(directQueueTwo())
                .to(directExchange())
                .with(environment.getProperty("mq.direct.routing.key.two.name"));
    }

    /**
     * 2、3两个队列 通过同一个路由绑定到  同一个交换机上
     * @return
     */
    //创建绑定3
    @Bean
    public Binding directBindingThree(){
        return BindingBuilder.bind(directQueueThree())
                .to(directExchange())
                .with(environment.getProperty("mq.direct.routing.key.two.name"));
    }


    /**
     * 创建消息模型 - topicExchange
     */

    //创建交换机 - topicExchange
    @Bean
    public TopicExchange topicExchange(){
        return new TopicExchange(environment.getProperty("mq.topic.exchange.name"), true, false);
    }

    //创建队列1
    @Bean(name = "topicQueueOne")
    public Queue topicQueueOne(){
        return new Queue(environment.getProperty("mq.topic.queue.one.name"), true);
    }

    //创建队列2
    @Bean(name = "topicQueueTwo")
    public Queue topicQueueTwo(){
        return new Queue(environment.getProperty("mq.topic.queue.two.name"), true);
    }

    /**
     * 创建绑定 - 通配符为 * 的路由
     * @return
     */
    @Bean
    public Binding topicBindingOne(){
        return BindingBuilder.bind(topicQueueOne())
                .to(topicExchange())
                .with(environment.getProperty("mq.topic.routing.key.one.name"));
    }

    /**
     * 创建绑定 - 通配符为 # 的路由
     * @return
     */
    @Bean
    public Binding topicBindingTwo(){
        return BindingBuilder.bind(topicQueueOne())
                .to(topicExchange())
                .with(environment.getProperty("mq.topic.routing.key.two.name"));
    }

}
