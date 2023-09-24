package com.logpose.ph2.api.dto;

import java.util.List;

import com.logpose.ph2.api.dao.db.entity.Ph2RealGrowthFStageEntity;

import lombok.Data;

@Data
public class FDataListDTO
	{
	//* 該当年度
	private Short year;
	//* 該当デバイス
	private Long deviceId;
	//* F値データリスト
	private List<Ph2RealGrowthFStageEntity> list;
	}
