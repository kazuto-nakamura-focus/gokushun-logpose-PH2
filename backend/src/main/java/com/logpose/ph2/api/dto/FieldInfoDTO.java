package com.logpose.ph2.api.dto;

import com.logpose.ph2.api.dto.element.Label;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 圃場一覧取得
 *
 */
@Data
@EqualsAndHashCode(callSuper=false)
public class FieldInfoDTO extends Label //* IDと圃場名
	{
	//* 場所
	private String location;
	//* 緯度
	private Double latitude;
	//* 経度
	private Double longitude;
	//* 取引先
	private String contructor;
	//* 登録日時
	private String registeredDate;
	}
