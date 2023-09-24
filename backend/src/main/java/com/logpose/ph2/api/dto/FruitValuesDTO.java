package com.logpose.ph2.api.dto;

import lombok.Data;

/**
 * 着果量着果負担表示画面
 *
 */
@Data
public class FruitValuesDTO
	{
	//* 着果負担
	private Double burden;
	//* 積算推定樹冠光合成量あたりの着果量（g/mol）
	private Double amount;
	//* 実測着果数/樹冠葉面積（房数/㎠）
	private Double count;
	}
