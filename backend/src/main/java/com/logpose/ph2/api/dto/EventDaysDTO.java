package com.logpose.ph2.api.dto;

import java.util.Date;

import lombok.Builder;
import lombok.Getter;

/**
 * 生育推定イベントデータ取得
 */
@Getter
@Builder
public class EventDaysDTO 
	{
	private String name;
	private Date date;
	private Double value;
	private String color;
	}
