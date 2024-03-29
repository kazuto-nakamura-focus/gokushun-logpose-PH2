package com.logpose.ph2.api.dto.dashboard;

import java.util.List;

import com.logpose.ph2.api.dao.db.entity.Ph2DashBoardSensorsEntity;

import lombok.Data;

@Data
public class DashBoardSensorsDTO
	{
	private Long deviceId;
	private List<Ph2DashBoardSensorsEntity> sensors;
	}
