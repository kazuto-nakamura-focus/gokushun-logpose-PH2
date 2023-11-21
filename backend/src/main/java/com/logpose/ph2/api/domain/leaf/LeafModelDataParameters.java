package com.logpose.ph2.api.domain.leaf;

import java.util.List;

import com.logpose.ph2.api.dao.db.entity.Ph2WibleMasterEntity;
import com.logpose.ph2.api.dto.LeafParamSetDTO;

import lombok.Data;

@Data
public class LeafModelDataParameters
	{
// * 葉面積パラメータセット
	private LeafParamSetDTO params;
// * 萌芽日
	private int lapseDay;
// * 新梢数
	private int shootCount;
// * ワイブル分布のリスト
	private List<Ph2WibleMasterEntity> wibles;
	}
