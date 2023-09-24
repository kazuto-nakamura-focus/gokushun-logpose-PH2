package com.logpose.ph2.api.dto.element;

import java.util.LinkedHashMap;
import java.util.Map;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=false)
public class LabelMap extends Label
	{
	private  Map<Long, LabelMap> map = new LinkedHashMap<>();
	}
