package com.logpose.ph2.api.domain.growth;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.logpose.ph2.api.dao.db.entity.Ph2ParamsetGrowthEntity;
import com.logpose.ph2.api.dao.db.entity.joined.ModelAndDailyDataEntity;
import com.logpose.ph2.api.dao.db.mappers.Ph2ModelDataMapper;
import com.logpose.ph2.api.dto.FDataListDTO;
import com.logpose.ph2.api.dto.GrowthParamSetDTO;
import com.logpose.ph2.api.formula.Formula;

@Component
public class GrowthModelDomain
	{
	// ===============================================
	// クラスメンバー
	// ===============================================
	@Autowired
	private FValueDomain fValueDomain;
	@Autowired
	private GrowthParameterDomain growthParameterDomain;
	@Autowired
	private Ph2ModelDataMapper ph2ModelDataMapper;

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
// * 萌芽日（経過日）のF値を取得する
			double sproutFvalue = this.fValueDomain.getSproutFValue(deviceId, year);

// * モデルデータの更新
			this.updateModelData(data, param, sproutFvalue);
			}
		}

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
	public List<ModelAndDailyDataEntity> updateModelDataByFValue(Long deviceId, Short year, FDataListDTO dto)
		{
		List<ModelAndDailyDataEntity> data = this.ph2ModelDataMapper.selectModelAndDailyData(deviceId, year);
// * デバイスID、年度からパラメータセットを取得
		Ph2ParamsetGrowthEntity enyity = this.growthParameterDomain.getParmaters(deviceId, year);
		GrowthParamSetDTO param = new GrowthParamSetDTO();
		param.setAd(enyity.getAfterD());
		param.setAe(enyity.getAfterE());
		param.setBd(enyity.getBeforeD());
		param.setBe(enyity.getBeforeE());

// * 萌芽日（経過日）を取得する
		Date sproutDate = null;
		for (var item : dto.getList())
			{
			if ((item.getStageStart() <= 4)&&(item.getStageEnd() >= 4))
				{
				sproutDate = (null != item.getActualDate()) ? item.getActualDate() : item.getEstimateDate();
				break;
				}
			}

// * モデルデータの更新
		this.updateModelDataByDate(data, param, sproutDate);

		return data;
		}

	// ===============================================
	// 保護関数群
	// ===============================================
	// ###############################################
	/**
	 * F値を算出し、リストに格納する
	 *
	 * @param dailyData
	 * @param param パラメータセット
	 * @param fValue 萌芽日のF値
	 */
	// ###############################################
	private void updateModelData(
			List<ModelAndDailyDataEntity> dailyData,
			GrowthParamSetDTO param,
			double sprountFvalue)
		{
		double lastValue = 0;
		Double dValue = param.getBd();
		Double eValue = param.getBe();
		boolean isUpdated = false;

		for (ModelAndDailyDataEntity data : dailyData)
			{
			if ((!isUpdated) && (lastValue >= sprountFvalue))
			// * 萌芽後の場合
				{
				dValue = param.getAd();
				eValue = param.getAe();
				isUpdated = true;
				}
			double fValue = Formula.toFValue(data.getTm(), dValue,
					eValue);
// * 積算F値を算出する
			lastValue = lastValue + fValue;
			data.setfValue(lastValue);
			}
		}

	// ###############################################
	/**
	 * F値を算出し、リストに格納する
	 *
	 * @param dailyData
	 * @param param パラメータセット
	 * @param fValue 萌芽日のF値
	 */
	// ###############################################
	private void updateModelDataByDate(
			List<ModelAndDailyDataEntity> dailyData,
			GrowthParamSetDTO param,
			Date sproutDate)
		{
		double lastValue = 0;
		Double dValue = param.getBd();
		Double eValue = param.getBe();
		boolean isUpdated = false;
		for (ModelAndDailyDataEntity data : dailyData)
			{
			if ((!isUpdated) && (null != sproutDate) && (data.getDate().getTime() > sproutDate.getTime()))
			// * 萌芽後の場合
				{
				dValue = param.getAd();
				eValue = param.getAe();
				isUpdated = true;
				}
			double fValue = Formula.toFValue(data.getTm(), dValue,
					eValue);
// * 積算F値を算出する
			lastValue = lastValue + fValue;
			data.setfValue(lastValue);
			}
		}
	}
