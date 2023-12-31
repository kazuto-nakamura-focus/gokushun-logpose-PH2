package com.logpose.ph2.api.dto.sensorData;

import java.util.ArrayList;
import java.util.List;

import com.logpose.ph2.api.dto.GraphDataDTO;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=false)
public class SenseorDataDTO extends GraphDataDTO
	{
	private List<String> category = new ArrayList<>();
	}
