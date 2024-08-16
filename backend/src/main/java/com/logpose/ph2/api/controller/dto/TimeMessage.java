package com.logpose.ph2.api.controller.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TimeMessage
	{
	private String date;
	private boolean status;
	private String message;
	}
