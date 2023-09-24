package com.logpose.ph2.api.dto;

import lombok.Data;

/**
 * 着果量更新時のデータ
 *
 */
@Data
public class FruitValuesByDevice
	{
	//* デバイスID
	private Long deviceId;
	//* イベントID
	private Short eventId;// null
	//* 対象年度
	private Short year;
	//* 実測日
	private String date;
	//* 平均房重
	private Float weight;
	//* 実測着果数
	private Integer count;
	}
