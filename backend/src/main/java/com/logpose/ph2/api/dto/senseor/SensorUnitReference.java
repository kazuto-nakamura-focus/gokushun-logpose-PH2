package com.logpose.ph2.api.dto.senseor;

import com.logpose.ph2.api.dto.element.Label;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=false)
public class SensorUnitReference extends Label //* センサーDとセンサー名
	{
	//* Channel
	private Integer channel;
	//* センサー型番
	private Long modelId;
	private String modelName;
	//* センサー内容
	private Long displayId;
	private String displayName;
	//* センサーサイズ
	private Long sizeId;
	private Integer size;
	// * KST(樹液流)
	private Double kst;
	//* 茎径
	private Double stemDiameter;
	}
