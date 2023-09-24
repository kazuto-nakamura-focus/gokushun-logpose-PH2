package com.logpose.ph2.api.dao.db.mappers.joined;

import java.util.Date;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.logpose.ph2.api.dao.db.entity.joined.FruitModelDataEntity;

@Mapper
public interface Ph2FruitsModelMapper
	{
	List<FruitModelDataEntity> listParamerSet(@Param("deviceId") Long deviceId, @Param("targetDate") Date targetDate);
	}
