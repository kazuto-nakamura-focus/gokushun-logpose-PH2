package com.logpose.ph2.api.domain.growth;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.logpose.ph2.api.dao.db.entity.Ph2ParamsetGrowthEntity;
import com.logpose.ph2.api.dao.db.entity.joined.ModelAndDailyDataEntity;
import com.logpose.ph2.api.domain.model.AppliedModel;
import com.logpose.ph2.api.dto.GrowthParamSetDTO;
import com.logpose.ph2.api.formula.Formula;

@Component
public class GrowthModelDomain
	{
	// ===============================================
	// クラスメンバー
	// ===============================================
	@Autowired
	private AppliedModel appliedModel;
	@Autowired
	private GrowthParameterDomain growthParameterDomain;

	// ===============================================
	// 公開関数群
	// ===============================================
	// ###############################################
	/**
	 * デバイスのモデルテーブルを更新する
	 * 
	 * @param deviceId デバイスID
	 * @param year 年度
	 * @param data 更新対象となる日ごととモデルのデータ
	 */
	// ###############################################
	public void updateModelData(Long deviceId, Short year, List<ModelAndDailyDataEntity> data)
		{
		this.updateModelData(deviceId, year, null, data);
		}
	// ===============================================
	// 保護関数群
	// ===============================================
	// ###############################################
	/**
	 * デバイスのモデルテーブルを更新する
	 * 
	 * @param deviceId デバイスID
	 * @param year 年度
	 * @param param パラメータセット
	 * @param data 更新対象となる日ごととモデルのデータ
	 */
	// ###############################################
	public void updateModelData(Long deviceId, Short year, GrowthParamSetDTO param,
			List<ModelAndDailyDataEntity> data)
		{
// * 日ごとデータがある場合
		if (0 != data.size())
			{
			if (null == param)
				{
// * デバイスID、年度からパラメータセットを取得
				Ph2ParamsetGrowthEntity enyity = this.growthParameterDomain.getParmaters(deviceId, year);
				param = new GrowthParamSetDTO();
				param.setAd(enyity.getAfterD());
				param.setAe(enyity.getAfterE());
				param.setBd(enyity.getBeforeD());
				param.setBe(enyity.getBeforeE());
				}
// * 萌芽日（経過日）を取得する
			short sproutDay = this.appliedModel.getSproutDay(deviceId, year);

// * モデルデータの更新
			this.updateModelData(data, param, sproutDay);
			}
		}
	// ###############################################
	/**
	 * F値を算出し、リストに格納する
	 *
	 * @param dailyData
	 * @param param パラメータセット
	 * @param sproutDay 萌芽日（経過日）
	 */
	// ###############################################
	private void updateModelData(
			List<ModelAndDailyDataEntity> dailyData,
			GrowthParamSetDTO param,
			short sproutDay)
		{
		double lastValue = 0;
		Double dValue = param.getBd();
		Double eValue = param.getBe();
		for (ModelAndDailyDataEntity data : dailyData)
			{
			// * 萌芽後の場合
			if (data.getLapseDay() >= sproutDay)
				{
				dValue = param.getAd();
				eValue = param.getAe();
				}
			double fValue = Formula.toFValue(data.getTm(), dValue,
					eValue);
// * 積算F値を算出する
			lastValue = lastValue + fValue;
			data.setfValue(lastValue);
			}
		}
	}
