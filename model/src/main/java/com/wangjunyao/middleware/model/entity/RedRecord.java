package com.wangjunyao.middleware.model.entity;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 发红包记录实体类
 */
@Data
public class RedRecord {
    private Integer id;

    private Integer userId;

    private String redPacked;

    private Integer total;

    private BigDecimal amount;

    private Byte isActive;

    private Date createTime;

}