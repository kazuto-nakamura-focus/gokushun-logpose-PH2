package com.logpose.ph2.api.dao.db.mappers.joined;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.logpose.ph2.api.dto.SensorDataDTO;

@Mapper
public interface Ph2JoinedMapper
	{
	List<SensorDataDTO> getSensorData(@Param("deviceId") Long deviceId);
	}
