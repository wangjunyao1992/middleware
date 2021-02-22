package com.wangjunyao.middleware.server.event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 生产者
 */
@Component
public class Publisher {

    private static final Logger logger = LoggerFactory.getLogger(Publisher.class);

    @Autowired
    private ApplicationEventPublisher publisher;

    public void sendMsg(){
        //构造登陆成功后用户的实体信息
        LoginEvent event = new LoginEvent(this, "debug", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()), "127.0.0.1");
        publisher.publishEvent(event);
        logger.info("Spring事件驱动模型 - 发送消息{}", event);
    }

}
