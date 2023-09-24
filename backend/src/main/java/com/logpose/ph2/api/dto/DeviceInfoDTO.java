package com.logpose.ph2.api.dto;


import java.util.Date;

import com.logpose.ph2.api.dto.element.Label;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 圃場一覧取得
 *
 */
@Data
@EqualsAndHashCode(callSuper=false)
public class DeviceInfoDTO extends Label //* IDとデバイス名
	{
	//* 圃場ID
	private Long fieldId;
	//* 圃場名
	private String field;
	//* 品種ID
	private Integer brandId;
	//* 品種
	private String brand;
	//* 基準日
	private Date baseDate;
	//* 基準日表示用
	private String baseDateShort;
	//* 登録日時
	private String registeredDate;
	}
