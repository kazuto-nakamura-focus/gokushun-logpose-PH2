package com.logpose.ph2.api.dto.element;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=false)
public class FieldData extends Label
	{
	//* 値
	private String value;
	//* 受信日時
	private String castedAt;
	// * センサーID
//	private String sensorName;
	// * 表示順
	private Short displayNo;
	}
