package com.logpose.ph2.api.dao.api.entity;

import lombok.Data;

@Data
public class FreeWeatherRequest
	{
	private String url;
	private String key;
	private String latitude;
	private String longitude;
	private final String defaults = "&days=1&aqi=no&alerts=no";
	}
