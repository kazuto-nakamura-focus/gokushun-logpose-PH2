package com.logpose.ph2.api.dao.db.mappers.joined;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.logpose.ph2.api.dao.db.entity.joined.SensorItemDTO;
import com.logpose.ph2.api.dto.senseor.SensorUnitReference;


@Mapper
public interface SensorJoinMapper
	{
	List<SensorItemDTO> selectSensorList(@Param("deviceId") Long deviceId);
	
    List<SensorUnitReference> selectDetail(@Param("deviceId") Long deviceId);
	}
