package com.wangjunyao.middleware.server.event;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.context.ApplicationEvent;

import java.io.Serializable;

@Getter
@Setter
@ToString
public class LoginEvent extends ApplicationEvent implements Serializable {

    /**
     * 用户名
     */
    private String userName;

    /**
     * 登陆时间
     */
    private String loginTime;

    /**
     * 所在IP
     */
    private String ip;

    public LoginEvent(Object source) {
        super(source);
    }

    /**
     * 继承ApplicationEvent类时需要重写的构造方法
     * @param source
     * @param userName
     * @param loginTime
     * @param ip
     */
    public LoginEvent(Object source, String userName, String loginTime, String ip) {
        super(source);
        this.userName = userName;
        this.loginTime = loginTime;
        this.ip = ip;
    }
}
