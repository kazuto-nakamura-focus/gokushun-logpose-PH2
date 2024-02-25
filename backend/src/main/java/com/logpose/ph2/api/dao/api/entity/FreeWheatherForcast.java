package com.logpose.ph2.api.dao.api.entity;

import java.util.List;

import lombok.Data;

@Data
public class FreeWheatherForcast
	{
	private List<FreeWheatherDay> forecastday;
	}
