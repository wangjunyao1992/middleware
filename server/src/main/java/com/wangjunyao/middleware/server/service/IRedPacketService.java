package com.wangjunyao.middleware.server.service;

import com.wangjunyao.middleware.server.dto.RedPacketDto;

import java.math.BigDecimal;

/**
 * 红包业务逻辑处理接口
 */
public interface IRedPacketService {

    /**
     * 发红包核心业务逻辑的实现
     * @param dto
     * @return
     */
    String handOut(RedPacketDto dto) throws Exception;

    /**
     * 抢红包业务逻辑
     * @param userId
     * @param redId
     * @return
     */
    BigDecimal rob(Integer userId, String redId) throws Exception;
}
