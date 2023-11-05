package com.logpose.ph2.api.domain.photosynthesis;

import java.util.Date;
import java.util.Map;

import com.logpose.ph2.api.dao.db.entity.Ph2RealPsAmountEntity;
import com.logpose.ph2.api.dao.db.mappers.Ph2ModelDataMapper;
import com.logpose.ph2.api.dto.PhotosynthesisParamSetDTO;

import lombok.Data;

@Data
public class PSModelDataParameters
	{
// * 新梢数
	private int shootCount;
// * 光合成パラメータ実績マップ
	private Map<Date, Ph2RealPsAmountEntity> realParamMap;
// * 光合成推定パラメータセット
	private PhotosynthesisParamSetDTO params;
// * モデルデータマッパー
	private Ph2ModelDataMapper ph2ModelDataMapper;
	}
