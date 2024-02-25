package com.logpose.ph2.api.dto.dashboard;

import java.util.List;

import com.logpose.ph2.api.dao.db.entity.Ph2DashBoardSensorsEntity;
import com.logpose.ph2.api.dto.element.Label;

import lombok.Data;

@Data
public class DashboardSensorMenuDTO
	{
	private List<Label> menu;
	private List<Ph2DashBoardSensorsEntity> displays;
	}
