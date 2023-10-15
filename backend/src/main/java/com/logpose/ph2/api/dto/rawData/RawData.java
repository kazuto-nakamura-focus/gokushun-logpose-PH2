package com.logpose.ph2.api.dto.rawData;

import java.util.Date;

import lombok.Data;

@Data
public class RawData
	{
	// * 受信日時
	private Date castedAt;
	// * センサーID
	private Long sensorId;
	// * 値
	private String value;
	// * コンテントID
	private Long contentId;
	}
