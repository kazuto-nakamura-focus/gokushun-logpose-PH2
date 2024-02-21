package com.logpose.ph2.api.dao.api.entity;

import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class WeatherHourly
	{
	private List<Date> time;
	@JsonProperty("terrestrial_radiation_instant") 
	private List<Float> terrestrialRadiationInstant;
	}
