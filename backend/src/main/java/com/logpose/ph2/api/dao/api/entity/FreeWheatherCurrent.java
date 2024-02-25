package com.logpose.ph2.api.dao.api.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class FreeWheatherCurrent
	{
	@JsonProperty("last_updated_epoch") 
	private Long epoch;
	private FreeWheatherCondition condition;
	}
