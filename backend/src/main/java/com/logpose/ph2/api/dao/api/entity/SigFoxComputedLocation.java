package com.logpose.ph2.api.dao.api.entity;

import lombok.Data;

@Data
public class SigFoxComputedLocation
	{
	private float lat;
	private float lng;
	private int radius;
	private int source;
	private int status;
	}
