package com.wangjunyao.middleware.server.entity;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class User {

    private Integer id;

    private String userName;

    private String name;

    public User(){}

    public User(Integer id, String userName, String name){
        this.id = id;
        this.userName = userName;
        this.name = name;
    }

}
