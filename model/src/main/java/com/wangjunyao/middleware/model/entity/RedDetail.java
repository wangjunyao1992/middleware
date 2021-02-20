package com.wangjunyao.middleware.model.entity;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class RedDetail {
    private Integer id;

    private Integer recordId;

    private BigDecimal amount;

    private Byte isActive;

    private Date createTime;

}