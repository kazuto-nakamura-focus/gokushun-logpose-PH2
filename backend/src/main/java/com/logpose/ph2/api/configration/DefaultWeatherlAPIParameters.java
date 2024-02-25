package com.logpose.ph2.api.configration;

import lombok.Data;

@Data
public class DefaultWeatherlAPIParameters
	{
	private String hourly;
	private String daily;
	private String historyUrl;
	private String currentUrl;
	}
