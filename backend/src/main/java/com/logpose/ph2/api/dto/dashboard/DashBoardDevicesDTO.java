package com.logpose.ph2.api.dto.dashboard;

import lombok.Data;

@Data
public class DashBoardDevicesDTO
	{
    private Long fieldId;
	private Long deviceId;
	private String deviceName;
	private Boolean isDisplay;
	}
