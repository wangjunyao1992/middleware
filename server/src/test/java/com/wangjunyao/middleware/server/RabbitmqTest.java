package com.wangjunyao.middleware.server;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wangjunyao.middleware.server.entity.EventInfo;
import com.wangjunyao.middleware.server.entity.Person;
import com.wangjunyao.middleware.server.rabbitmq.entity.KnowledgeInfo;
import com.wangjunyao.middleware.server.rabbitmq.publisher.BasicPublisher;
import com.wangjunyao.middleware.server.rabbitmq.publisher.KnowledgeManualPublisher;
import com.wangjunyao.middleware.server.rabbitmq.publisher.KnowledgePublisher;
import com.wangjunyao.middleware.server.rabbitmq.publisher.ModelPublisher;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@SpringBootTest(classes = MainApplication.class)
@RunWith(SpringJUnit4ClassRunner.class)
public class RabbitmqTest {

    private static final Logger logger = LoggerFactory.getLogger(RabbitmqTest.class);

    /**
     * 定义json序列化和反序列化实例
     */
    @Autowired
    private ObjectMapper objectMapper;

    /**
     * 定义基本消息模型中 发送消息 的生产者实例
     */
    @Autowired
    private BasicPublisher basicPublisher;

    @Test
    public void test1() throws Exception{
        String msg = "~~~这是一串字符串消息~~~";
        //生产者实例发送消息
        basicPublisher.sendMsg(msg);
    }

    @Test
    public void test2() throws Exception{
        Person person = new Person(1, 29, "大剩", "debug", "陕西·宝鸡");
        //生产者实例发送消息
        basicPublisher.sendObjectMsg(person);
    }

    @Autowired
    private ModelPublisher modelPublisher;

    @Test
    public void test3() throws Exception{
        //创建对象实例
        EventInfo info = new EventInfo(1, "增删改查模块",
                "基于fanoutExchange的消息模块",
                "这是基于fanoutExchange的消息模型");
        //生产者发送消息
        modelPublisher.sendMsgFanout(info);
    }

    @Test
    public void test4() throws Exception{
        //创建对象实例 - 1
        EventInfo info = new EventInfo(1, "增删改查模块 - 1",
                "基于directExchange的消息模块 - 1",
                "这是基于fanoutExchange的消息模型 - 1");
        //生产者发送第一个消息
        modelPublisher.sendMsgDirectOne(info);

        //创建对象实例 - 2
        info = new EventInfo(2, "增删改查模块 - 2",
                "基于directExchange的消息模块 - 2",
                "这是基于fanoutExchange的消息模型 - 2");
        //生产者发送第二个消息
        modelPublisher.sendMsgDirectTwo(info);
    }

    @Test
    public void test5() throws Exception{
        //定义待发送的消息
        String msg = "这是TopicExchange消息模型的消息";

        //此时相当于*，即Java替代了*的位置
        //当然由于#表示任意单词，因而也将路由到#表示的路由和对应的队列中
        String routingKeyOne = "local.middleware.mq.topic.routing.java.key";

        //此时相当于#：即php.python替代了#的位置
        String routingKeyTwo = "local.middleware.mq.topic.routing.php.python.key";

        //此时相当于#：即0个单词
        String routingKeyThree = "local.middleware.mq.topic.routing.key";

        //下面分批进行测试，以便能看出运行效果
        //modelPublisher.sendMsgTopic(msg, routingKeyOne);
        //modelPublisher.sendMsgTopic(msg, routingKeyTwo);
        modelPublisher.sendMsgTopic(msg, routingKeyThree);
    }

    @Autowired
    private KnowledgePublisher knowledgePublisher;

    /**
     * 单一消费者 - 确认模式为AUTO
     * @throws Exception
     */
    @Test
    public void test6() throws Exception{
        KnowledgeInfo info = new KnowledgeInfo();
        info.setId(10010);
        info.setCode("auto");
        info.setMode("基于AUTO的消息确认消费模式");
        //调用生产者发送消息
        knowledgePublisher.sendAutoMsg(info);
    }

    @Autowired
    private KnowledgeManualPublisher knowledgeManualPublisher;

    /**
     * 单一消费者 - 确认模式为MANUAL
     * @throws Exception
     */
    @Test
    public void test7() throws Exception{
        KnowledgeInfo info = new KnowledgeInfo();
        info.setId(10011);
        info.setCode("manual");
        info.setMode("基于MANUAL的消息确认消费模式");
        //调用生产者发送消息
        knowledgeManualPublisher.sendManualMsg(info);
    }

}
