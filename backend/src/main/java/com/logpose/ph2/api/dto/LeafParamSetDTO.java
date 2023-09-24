package com.logpose.ph2.api.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=false)
public class LeafParamSetDTO extends ParamSetDTO
	{
	//* 樹冠葉面積モデルパラメータa値
	private Double areaA;
	//* 樹冠葉面積モデルパラメータb値
	private Double areaB;
	//* 樹冠葉面積モデルパラメータc値
	private Double areaC;
	//* 葉枚数モデルパラメータc値
	private Double countC;
	//* 葉枚数モデルパラメータd値
	private Double countD;
	}