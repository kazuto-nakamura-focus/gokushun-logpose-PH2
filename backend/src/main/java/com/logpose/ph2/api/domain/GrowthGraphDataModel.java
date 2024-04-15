package com.logpose.ph2.api.domain;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.logpose.ph2.api.dao.db.entity.Ph2ModelDataEntity;
import com.logpose.ph2.api.dao.db.entity.Ph2ParamsetGrowthEntity;
import com.logpose.ph2.api.dao.db.entity.Ph2RealGrowthFStageEntity;
import com.logpose.ph2.api.dao.db.mappers.Ph2ModelDataMapper;
import com.logpose.ph2.api.dto.DailyBaseDataDTO;
import com.logpose.ph2.api.formula.Formula;

public class GrowthGraphDataModel
	{
	// ===============================================
	// クラスメンバー
	// ===============================================
	private List<Double> fRealValues = new ArrayList<>();
	private List<Double> fModelValues = new ArrayList<>();
	private Date startDate;
	private Date endDate;

	private double lastValue = 0;

	// ===============================================
	// 公開関数群
	// ===============================================

	// --------------------------------------------------
	/**
	 * F値を算出し、リストに格納する
	 *
	 * @param list 平均気温リスト
	 * @param paramSet  パラメータセット
	 * @param fStageInfoList F値情報
	 * @param sproutStage 萌芽前ステージ
	 * @param mapper DBマッパー
	 */
	// --------------------------------------------------
	public void calculateFvalues(
			List<DailyBaseDataDTO> list,
			Ph2ParamsetGrowthEntity param,
			List<Ph2RealGrowthFStageEntity> fStageInfoList,
			short sproutStage,
			Ph2ModelDataMapper mapper)
		{
		startDate = list.get(0).getDate();  // 年度の初日
		endDate = list.get(list.size() - 1).getDate();  // 年度の最後
		short startDay = 0;
		for (Ph2RealGrowthFStageEntity fstageInfo : fStageInfoList)
			{
			Double dValue = 0.0;
			Double eValue = 0.0;
			// * 萌芽前の場合
			if (fstageInfo.getStageEnd() <= sproutStage)
				{
				dValue = param.getBeforeD();
				eValue = param.getBeforeE();
				}
			// * 萌芽後の場合
			else
				{
				dValue = param.getAfterD();
				eValue = param.getAfterE();
				}
			startDay = this.addDate(startDay, list, dValue, eValue, fstageInfo.getAccumulatedF(), mapper);
			}
		this.addDate(startDay, list, param.getAfterD(), param.getAfterE(), Double.MAX_VALUE, mapper);
		}

	// --------------------------------------------------
	/**
	 * 計算開始日数から最大F値までF値を積算し、リストに格納する
	 *
	 * @param startDay 計算開始日
	 * @param list ディリーデータのリスト
	 * @param dValue d値
	 * @param eValue e値
	 * @param limitFValue 最大F値
	 * @param mapper DBマッパー
	 * @return 計算最終日
	 */
	// --------------------------------------------------
	public short addDate(
			short startDay,
			List<DailyBaseDataDTO> list,
			double dValue,
			double eValue,
			double limitFValue,
			Ph2ModelDataMapper mapper)
		{
		short index = startDay;
		for (; index < list.size(); index++)
			{
// * 平均気温を取り出す
			DailyBaseDataDTO temperature = list.get(index);
// * F値を算出する
			double fValue = Formula.toFValue(temperature.getTm(), dValue,
					eValue);
// * 積算F値を算出する
			lastValue = lastValue + fValue;
// * Mapperがある場合DBを更新する
			if (null != mapper)
				{
				Ph2ModelDataEntity model = mapper.selectByPrimaryKey(temperature.getDayId());
				model.setfValue(lastValue);
				mapper.updateByPrimaryKey(model);
				}
			else
				{
				if (temperature.isReal())
					{
					fRealValues.add(lastValue);
					}
				else
					{
					fModelValues.add(lastValue);
					}
				}
			// * Max値に達した場合
			if (lastValue > limitFValue)
				{
				return (short) (index + 1);
				}
			}
		return index;
		}
	}
