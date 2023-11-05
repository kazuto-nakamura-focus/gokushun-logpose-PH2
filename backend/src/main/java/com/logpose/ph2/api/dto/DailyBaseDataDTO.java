package com.logpose.ph2.api.dto;

import java.util.Date;

import lombok.Data;

@Data
public class DailyBaseDataDTO
	{
	private Long dayId;
	private Short lapseDay;
	private Date date;
	private float tm;
	private float average;
	private double cdd;
	private double par;
	private long sunTime;
	private boolean isReal;
	}
