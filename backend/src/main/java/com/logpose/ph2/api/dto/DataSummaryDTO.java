package com.logpose.ph2.api.dto;

import java.util.List;

import com.logpose.ph2.api.dao.db.entity.Ph2WeatherForecastEntity;
import com.logpose.ph2.api.dto.element.FieldData;

import lombok.Data;

/**
 * 全圃場サマリーデータDTO
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
// * 更新時刻
	private String date;
//* 各データリスト
	private List<FieldData> dataList;
// * 気象情報
	private List<Ph2WeatherForecastEntity> forecastList;
	}
