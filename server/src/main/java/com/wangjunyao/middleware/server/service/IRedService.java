package com.wangjunyao.middleware.server.service;

import com.wangjunyao.middleware.server.dto.RedPacketDto;

import java.math.BigDecimal;
import java.util.List;

/**
 * 红包业务逻辑处理过程数据记录接口 - 异步实现
 */
public interface IRedService {

    /**
     * 记录发红包时红包的全局唯一标识串、随机金额列表和个数等信息数据入库
     * @param dto
     * @param redId
     * @param list
     */
    void recordRedPacket(RedPacketDto dto, String redId, List<Integer> list);

    /**
     * 成功抢到红包时，将当前用户账号信息及对应的红包金额等信息记入数据库中
     * @param userId 用户账号id
     * @param redId 红包全局唯一标识
     * @param amount 抢到的红包金额
     */
    void recordRobRedPacket(Integer userId, String redId, BigDecimal amount);
}
