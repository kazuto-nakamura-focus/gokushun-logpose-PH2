package com.logpose.ph2.api.dao.db.mappers.joined;

import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import com.logpose.ph2.api.dto.dashboard.DashBoardDevicesDTO;
import com.logpose.ph2.api.dto.dashboard.DashBoardSensorsDTO;

@Mapper
public interface DashboardDomainMapper
	{
	List<DashBoardDevicesDTO> selectDisplayData();
	
	List<DashBoardSensorsDTO> selectSensorSettings(@Param("deviceId") Long deviceId);
	}
