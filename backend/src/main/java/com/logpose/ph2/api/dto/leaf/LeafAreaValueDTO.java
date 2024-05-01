package com.logpose.ph2.api.dto.leaf;

import lombok.Data;

@Data
public class LeafAreaValueDTO
	{
	//* 実施日
	private String date;
	//* 新梢辺り葉枚数
	private Integer count;
	//* 平均個葉面積
	private Float averageArea;
	//* 葉面積
	private Double totalArea;
	//* モデル値
	private Double estimatedArea;
	}
