package com.logpose.ph2.api.dto.bearing;

import java.util.List;

import lombok.Data;

@Data
public class RealFruitesValues
	{
	private String startDate;
	private String endDate;
	private List<RealFruitsValueDTO> values;
	}