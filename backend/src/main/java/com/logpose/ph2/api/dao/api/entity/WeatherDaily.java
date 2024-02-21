package com.logpose.ph2.api.dao.api.entity;

import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class WeatherDaily
	{
	private List<Date> time;
	
	@JsonProperty("temperature_2m_max") 
	private List<Float>	temperature2mMax;
	
	@JsonProperty("temperature_2m_min") 
	private List<Float>	temperature2mMin;
	
	@JsonProperty("temperature_2m_mean") 
	private List<Float>	temperature2mMean;
	
	@JsonProperty("daylight_duration") 
	private List<Long>	 daylightDuration;
	}
