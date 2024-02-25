package com.logpose.ph2.api.dao.api.entity;

import lombok.Data;

@Data
public class FreeWheatherResponse
	{
	private FreeWheatherCurrent current;
	private FreeWheatherForcast forecast;
	}
