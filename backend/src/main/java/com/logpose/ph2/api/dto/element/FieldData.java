package com.logpose.ph2.api.dto.element;

import java.util.Date;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=false)
public class FieldData extends Label
	{
	//* 値
	private String value;
	//* 受信日時
	private Date castedAt;
	// * センサーID
	private String sensorName;
	}
