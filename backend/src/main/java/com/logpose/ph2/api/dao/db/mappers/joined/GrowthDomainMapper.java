package com.logpose.ph2.api.dao.db.mappers.joined;

import java.util.Date;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.logpose.ph2.api.dao.db.entity.joined.FDataEntity;
import com.logpose.ph2.api.dto.DailyBaseDataDTO;

@Mapper
public interface GrowthDomainMapper
	{
	List<DailyBaseDataDTO> selectDailyata(
			@Param("deviceId") Long deviceId, @Param("year") short year, 
			@Param("date") Date date, @Param("isReal") boolean isReal);
	
	List<DailyBaseDataDTO> selectDailyData(
			@Param("deviceId") Long deviceId, @Param("year") short year, 
			@Param("date") Date date);
	
	List<FDataEntity> selectValueAndDays(@Param("deviceId") Long deviceId, @Param("year") short year);

	}
