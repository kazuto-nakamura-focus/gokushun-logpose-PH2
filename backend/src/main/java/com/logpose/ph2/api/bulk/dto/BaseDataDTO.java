package com.logpose.ph2.api.bulk.dto;

import java.util.Date;

import lombok.Data;

@Data
public class BaseDataDTO
	{
	private Date castedAt;
	private Float temperature;
	private Double insolation;
	
	private Long dayId;
	}
