package com.wangjunyao.middleware.model.entity;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class RedRobRecord {
    private Integer id;

    private Integer userId;

    private String redPacket;

    private BigDecimal amount;

    private Date robTime;

    private Byte isActive;
}