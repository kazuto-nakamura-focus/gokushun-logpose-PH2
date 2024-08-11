package com.logpose.ph2.api.dto.dataLoad;

import java.util.Date;

import lombok.Data;

@Data
public class DataLog
	{
	private Date time;
	private String message;
	private String status;
	}
