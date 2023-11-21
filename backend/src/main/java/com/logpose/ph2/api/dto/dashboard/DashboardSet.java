package com.logpose.ph2.api.dto.dashboard;

import java.util.List;

import lombok.Data;

@Data
public class DashboardSet
	{
	private Long deviceId;
	private List<DashboardDisplayOrder> sensors;
	}
