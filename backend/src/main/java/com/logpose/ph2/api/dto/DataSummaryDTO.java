package com.logpose.ph2.api.dto;

import java.util.List;

import com.logpose.ph2.api.dto.element.FieldData;

import lombok.Data;

/**
 * 全圃場サマリーデータ取得
 */
@Data
public class DataSummaryDTO
	{
	//* 圃場名ID
	private Long fieldId;
	//* 圃場名
	private String field;
	//* デバイスID
	private Long deviceId;
	//* デバイス名
	private String device;
	//* 各データリスト
	private List<FieldData> dataList;
	}
