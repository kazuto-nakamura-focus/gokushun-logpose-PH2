package com.logpose.ph2.api.dto;

import java.util.Date;

import lombok.Data;

@Data
public class DeviceDateDTO
	{
	//* デバイスID
	private Long deviceId;
	
	private Short year;
	//* 日付
	private Date date;
	}
