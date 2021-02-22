package com.wangjunyao.middleware.server;

import com.wangjunyao.middleware.server.event.Publisher;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@SpringBootTest(classes = MainApplication.class)
@RunWith(SpringJUnit4ClassRunner.class)
public class EventTest {

    @Autowired
    private Publisher publisher;

    @Test
    public void test1() throws Exception{
        //调用发送消息的方法产生消息
        publisher.sendMsg();
    }

}
