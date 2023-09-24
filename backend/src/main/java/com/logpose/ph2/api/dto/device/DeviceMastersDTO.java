package com.logpose.ph2.api.dto.device;

import java.util.List;

import com.logpose.ph2.api.dto.element.Label;

import lombok.Data;

@Data
public class DeviceMastersDTO
	{
	private List<Label> sensorContents;
	private List<Label> sensorModels;
	private List<Label> sensorSizes;
	}
