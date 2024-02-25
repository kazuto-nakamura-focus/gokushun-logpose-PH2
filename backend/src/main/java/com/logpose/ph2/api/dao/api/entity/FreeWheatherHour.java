package com.logpose.ph2.api.dao.api.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class FreeWheatherHour
	{
	@JsonProperty("time_epoch") 
	private long timeEpoch;
	@JsonProperty("temp_c") 
	private float temp;
	@JsonProperty("chance_of_rain") 
	private short chanceOfRain;
	@JsonProperty("chance_of_snow") 
	private short chanceOfSnow;
	private FreeWheatherCondition condition;
	}
