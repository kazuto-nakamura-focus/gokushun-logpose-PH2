package com.logpose.ph2.api.dao.db.mappers.joined;

import java.util.Date;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.logpose.ph2.api.bulk.dto.BaseDataDTO;

@Mapper
public interface Ph2JoinedModelMapper
	{
	List<BaseDataDTO> getBaseData(
				@Param("deviceId") Long deviceId, 
				@Param("startDate") Date startDate,
				@Param("endDate") Date endDate);
	
	Long countEffectiveData(@Param("deviceId") Long deviceId, @Param("date") Date date);
	}
