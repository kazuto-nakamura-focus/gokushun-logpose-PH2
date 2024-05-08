package com.logpose.ph2.api.dto.photosynthesis;

import lombok.Data;

/**
 * 光合成推定実績値更新
 *
 */
@Data
public class PhotosynthesisValueDTO
	{
	//* デバイスID
	private Long deviceId;
	//* 年度
	private Short year;
	//* 実測日
	private String date;
	//* f値
	private Double f;
	//* g値
	private Double g;
	}
