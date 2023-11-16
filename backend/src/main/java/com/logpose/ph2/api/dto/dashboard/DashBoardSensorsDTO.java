package com.logpose.ph2.api.dto.dashboard;

import lombok.Data;

@Data
public class DashBoardSensorsDTO
	{
	private Long deviceId;
	private Long sensorId;
	private String sensorName;
    private Long dataId;
    private String dataName;
    private Short displayNo;
	}
