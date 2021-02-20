package com.wangjunyao.middleware.model.mapper;

import com.wangjunyao.middleware.model.entity.RedRobRecord;

public interface RedRobRecordMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(RedRobRecord record);

    int insertSelective(RedRobRecord record);

    RedRobRecord selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(RedRobRecord record);

    int updateByPrimaryKey(RedRobRecord record);
}