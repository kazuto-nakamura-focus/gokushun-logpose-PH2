package com.logpose.ph2.api.dto.photosynthesis;

import com.logpose.ph2.api.dto.ParamSetDTO;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=false)
public class PhotosynthesisParamSetDTO extends ParamSetDTO
	{
	//* フィールド値モデルパラメータf値
	private Double fieldF;
	//* フィールド値モデルパラメータg値
	private Double fieldG;
	//* ワイブル分布モデルパラメータα値
	private Double weibullA;
	//* ワイブル分布モデルパラメータβ値
	private Double weibullB;
	//* ワイブル分布モデルパラメータλ値
	private Double weibullL;
	}