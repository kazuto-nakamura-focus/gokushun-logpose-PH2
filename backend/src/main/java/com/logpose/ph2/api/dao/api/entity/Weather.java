package com.logpose.ph2.api.dao.api.entity;

import lombok.Data;

@Data
public class Weather
	{
	private WeatherHourly hourly;
	private WeatherDaily daily;
	}
