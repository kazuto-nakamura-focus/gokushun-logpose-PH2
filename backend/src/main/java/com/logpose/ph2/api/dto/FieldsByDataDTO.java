package com.logpose.ph2.api.dto;

import java.util.List;

import com.logpose.ph2.api.dto.element.FieldData;
import com.logpose.ph2.api.dto.element.Label;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 検知データ圃場別取得
 */
@Data
@EqualsAndHashCode(callSuper=false)
public class FieldsByDataDTO extends Label
	{
	//* デバイス名
	private String device;
	//* データに対するフィールドごとの値
	private List<FieldData> fieldList;
	}
