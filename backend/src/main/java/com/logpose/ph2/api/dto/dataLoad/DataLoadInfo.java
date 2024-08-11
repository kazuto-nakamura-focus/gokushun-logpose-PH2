package com.logpose.ph2.api.dto.dataLoad;

import lombok.Data;

@Data
public class DataLoadInfo
	{
	private String name;
	private Long id;
	private String status;
	private String loadTime;
	private String updateTime;
	private String dataStatus;
	}
