package com.wangjunyao.middleware.server.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;

/**
 * 实体对象信息
 */
@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class EventInfo implements Serializable {

    /**
     * id标识
     */
    private Integer id;

    /**
     * 模块
     */
    private String module;

    /**
     * 名称
     */
    private String name;

    /**
     * 描述
     */
    private String desc;

}
