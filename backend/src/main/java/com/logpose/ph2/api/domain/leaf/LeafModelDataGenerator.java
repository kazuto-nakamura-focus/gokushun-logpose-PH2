package com.logpose.ph2.api.domain.leaf;

import java.util.List;

import com.logpose.ph2.api.dao.db.entity.Ph2WibleMasterEntity;
import com.logpose.ph2.api.dao.db.entity.joined.ModelAndDailyDataEntity;
import com.logpose.ph2.api.dto.LeafParamSetDTO;
import com.logpose.ph2.api.formula.Formula;

public class LeafModelDataGenerator
	{
	// ===============================================
	// コンストラクタ
	// ===============================================
	// ###############################################
	/**
	 * 各葉面積のモデルデータを作成する
	 * @param lapseDay
	 * @param shootCount
	 * @param parameters
	 * @param dailyData
	 */
	// ###############################################
	public LeafModelDataGenerator(
			short lapseDay, // 経過日
			long shootCount, // 実測新梢数
			LeafParamSetDTO parameters, // 葉面積データ
			List<Ph2WibleMasterEntity> wibles,
			List<ModelAndDailyDataEntity> dailyData)
		{
		double cdd = 0;
		for (ModelAndDailyDataEntity data : dailyData)
			{
// * 葉枚数モデル式
			double lc = Formula.toCountLeaf(parameters, data.getTm());
// * 葉枚数の更新
			data.setLeafCount(lc);

// * 樹冠葉面積モデル値
			double tla = 0;
// * 萌芽日からの経過日数を算出
			int lapse_day = lapseDay - data.getLapseDay().shortValue();
// * 経過日数が１以上なら萌芽後として以下の処理を行う
			if (lapse_day > 0)
				{
				cdd += data.getRawCdd();
				// * wible値を取得
				double wible = wibles.get(lapse_day).getValue();
				// * 樹冠葉面積モデル値を算出
				tla = Formula.toLeafArea(shootCount, parameters, cdd, wible);
				}
// * 樹冠葉面積データの更新
			data.setCrownLeafArea(tla);
			}
		}
	}
