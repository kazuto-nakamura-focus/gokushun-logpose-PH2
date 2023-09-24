package com.logpose.ph2.api.dto;

import java.util.Date;

import lombok.Data;

/**
 * 生育推定イベントデータ取得
 */
@Data
public class EventDaysDTO
	{
	private String eventName;
	private Date date;
	}
