package com.logpose.ph2.api.dao.db.mappers.joined;

import java.util.Date;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.logpose.ph2.api.dto.DataSummaryDTO;
import com.logpose.ph2.api.dto.ModelTargetDTO;
import com.logpose.ph2.api.dto.element.FieldData;
import com.logpose.ph2.api.dto.rawData.RawData;
import com.logpose.ph2.api.dto.top.FieldDataWithSensor;

@Mapper
public interface TopDomainMapper
	{
	List<DataSummaryDTO> selectFieldDeviceList();
	
	List<FieldData> selectFieldDataList(@Param("deviceId") Long deviceId);
	List<RawData> selectRawData(@Param("startDate") Date startDate, 
				@Param("endDate") Date endDate,@Param("deviceId") Long deviceId);
	List<FieldDataWithSensor> selectDeviceDataList(@Param("contentId") Long contentId);

	List<ModelTargetDTO> selectModelTargets();
	
	List<ModelTargetDTO> selectRawDataTargets();
	
	}
