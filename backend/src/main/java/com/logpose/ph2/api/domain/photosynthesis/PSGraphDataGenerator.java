package com.logpose.ph2.api.domain.photosynthesis;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.logpose.ph2.api.dao.db.entity.Ph2RealPsAmountEntity;
import com.logpose.ph2.api.dao.db.entity.joined.ModelAndDailyDataEntity;
import com.logpose.ph2.api.dao.db.mappers.Ph2ModelDataMapper;
import com.logpose.ph2.api.formula.Formula;

public class PSGraphDataGenerator
	{
	// ===============================================
	// コンストラクタ
	// ===============================================
	// ###############################################
	/**
	 * 各光合成のモデルデータを対象年度とデバイスに対して生成し、DBに保存する
	 * @param deviceId デバイスID
	 * @param year 年度
	 * @param fValue F値
	 * @param gValue G値
	 * @param map 実測値Map
	 * @param dailyData 元となる日ごとデータ
	 */
	// ###############################################
	public PSGraphDataGenerator(
			double fValue,
			double gValue,
			Map<Date, Ph2RealPsAmountEntity>  realDataMap,
			Ph2ModelDataMapper ph2ModelDataMapper,
			List<ModelAndDailyDataEntity> dailyData)
		{
// * 積算値
		double accumulatedValue = 0;
		for (ModelAndDailyDataEntity data : dailyData)
			{
// * 該当日の実測データを取得する。
			Ph2RealPsAmountEntity entity = realDataMap.get(data.getDate());
// * 存在する場合は実測値にパラメータを入れ替える
			if (null != entity)
				{
				if (null != entity.getValueF()) fValue = entity.getValueF();
				if (null != entity.getValueG()) gValue = entity.getValueG();
				}
// * 葉面積推定の値を得る
			double value = Formula.toPsAmount(fValue, gValue, accumulatedValue,
					data.getCrownLeafArea(), data.getPar(), data.getSunTime());
// * 推定積算樹冠光合成量データの更新
			data.setCulmitiveCnopyPs(value);
// * モデルデータの更新
			ph2ModelDataMapper.updateByPrimaryKey(data);
// * 積算値の更新
			accumulatedValue =  ((double)Math.round(value * 100))/100;
			}
		}
	}
