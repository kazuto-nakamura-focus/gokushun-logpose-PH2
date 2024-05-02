package com.logpose.ph2.api.domain.growth;

import java.util.List;

import com.logpose.ph2.api.dao.db.entity.Ph2ParamsetGrowthEntity;
import com.logpose.ph2.api.dao.db.entity.joined.ModelAndDailyDataEntity;
import com.logpose.ph2.api.formula.Formula;

public class GrowthModelDataGenerator
	{
	// ===============================================
	// クラスメンバー
	// ===============================================
	private double lastValue = 0;

	// ===============================================
	// 公開関数群
	// ===============================================
	// ###############################################
	/**
	 * F値を算出し、リストに格納する
	 *
	 * @param dailyData
	 * @param param パラメータセット
	 * @param sproutDay 萌芽日（経過日）
	 */
	// ###############################################
	public GrowthModelDataGenerator(
			List<ModelAndDailyDataEntity> dailyData,
			Ph2ParamsetGrowthEntity param,
			short sproutDay)
		{
		Double dValue = param.getBeforeD();
		Double eValue = param.getBeforeE();
		for (ModelAndDailyDataEntity data : dailyData)
			{
			// * 萌芽後の場合
			if (data.getLapseDay() >= sproutDay)
				{
				dValue = param.getBeforeD();
				eValue = param.getAfterE();
				}
			double fValue = Formula.toFValue(data.getTm(), dValue,
					eValue);
// * 積算F値を算出する
			lastValue = lastValue + fValue;
			data.setfValue(lastValue);
			}
		}
	}
