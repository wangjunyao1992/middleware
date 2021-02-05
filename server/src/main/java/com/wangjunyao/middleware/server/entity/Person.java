package com.wangjunyao.middleware.server.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Person implements Serializable {

    private Integer id;

    private Integer age;

    private String name;

    private String userName;

    private String address;

}
