package com.logpose.ph2.api.dao.api.entity;

import lombok.Data;

@Data
public class WeatherRequest
	{
	private String url;
	private String latitude;
	private String longitude;
	private String startDate;
	private String endDate;
	private String hourly;
	private String daily;
	private String timeZone;
	}
