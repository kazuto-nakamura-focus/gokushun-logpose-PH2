package com.logpose.ph2.api.dto.senseor;

import com.logpose.ph2.api.dto.element.Label;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=false)
public class SensorUnitUpdate extends Label //* センサーDとセンサー名
	{
	//* Channel
	private Integer channel;
	//* センサー型番
	private Long modelId;
	//* センサー内容ID
	private Long displayId;
	//* センサーサイズID
	private Long sizeId;
	private Double kst;
	//* 茎径
	private Double stemDiameter;
	}
