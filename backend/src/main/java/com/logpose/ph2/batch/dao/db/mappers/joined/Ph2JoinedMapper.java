package com.logpose.ph2.batch.dao.db.mappers.joined;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.logpose.ph2.api.bulk.dto.SensorDataDTO;

@Mapper
public interface Ph2JoinedMapper
	{
	List<SensorDataDTO> getSensorData(@Param("deviceId") Long deviceId);
	}
