package com.logpose.ph2.api.dto.rawData;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class RawDataList
	{
	private List<Long> headers = new ArrayList<>();
	private List<List<String>> data = new ArrayList<>();
	}
