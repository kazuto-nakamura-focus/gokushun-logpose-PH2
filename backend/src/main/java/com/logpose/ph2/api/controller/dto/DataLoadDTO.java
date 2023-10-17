package com.logpose.ph2.api.controller.dto;

import java.util.Date;

import lombok.Data;

@Data
public class DataLoadDTO
	{
	//* デバイスID
	private Long deviceId;
	private	Boolean isAll;
	private Date startDate;
	}
