package com.logpose.ph2.api.dto.sensorData;

import java.util.ArrayList;
import java.util.List;

import com.logpose.ph2.api.dto.graph.GraphDataDTO;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=false)
public class SenseorDataDTO extends GraphDataDTO
	{
	private long interval = 0;
	private List<String> category = new ArrayList<>();
	private List<String> labels = new ArrayList<>();
	}
