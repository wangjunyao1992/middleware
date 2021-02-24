package com.wangjunyao.middleware.server.rabbitmq.entity;

import lombok.Data;
import lombok.ToString;

/**
 * 确认消费实体对象信息
 */
@Data
@ToString
public class KnowledgeInfo {

    /**
     * id标识
     */
    private Integer id;

    /**
     * 模式名称
     */
    private String mode;

    /**
     * 对应编码
     */
    private String code;

}
