package com.logpose.ph2.api.dto;

import lombok.Data;

/**
 *新梢辺り葉枚数・平均個葉面積登録処理
 *
 */
@Data
public class LeafvaluesDTO
	{
	//* デバイスID
	private Long deviceId;
	//* 実施日
	private String date;
	//* 新梢辺り葉枚数
	private Integer count;
	//* 平均個葉面積
	private Float averageArea;
	//* 葉面積
	private Double totalArea;
	}
