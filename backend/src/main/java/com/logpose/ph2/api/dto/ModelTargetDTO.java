package com.logpose.ph2.api.dto;


import lombok.Data;

/**
 * 圃場一覧取得
 *
 */
@Data
public class ModelTargetDTO
	{
	private Long fieldId;
	private String field;
	private Long deviceId;
	private String device;
	private Short year;
	private String startDate;
	}
