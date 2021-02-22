package com.wangjunyao.middleware.server.event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Component;

/**
 * 消费者
 */
@Component
@EnableAsync
public class Consumer implements ApplicationListener<LoginEvent> {

    private static final Logger logger = LoggerFactory.getLogger(Consumer.class);

    /**
     * 监听消费消息
     * @param loginEvent
     */
    @Override
    @Async
    public void onApplicationEvent(LoginEvent loginEvent) {
        logger.info("Spring事件驱动模型 - 接收消息{}", loginEvent);
        //后续为实现自身的业务逻辑 - 比如写入数据库等
    }
}
