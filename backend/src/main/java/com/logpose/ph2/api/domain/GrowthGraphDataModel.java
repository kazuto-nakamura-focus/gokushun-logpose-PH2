package com.logpose.ph2.api.domain;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.logpose.ph2.api.dao.db.entity.Ph2ParamsetGrowthEntity;
import com.logpose.ph2.api.dao.db.entity.Ph2RealGrowthFStageEntity;
import com.logpose.ph2.api.dto.DailyBaseDataDTO;
import com.logpose.ph2.api.dto.RealModelGraphDataDTO;
import com.logpose.ph2.api.formula.Formula;
import com.logpose.ph2.api.utility.DateTimeUtility;

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
	 * グラフデータの作成
	 *
	 * @return グラフデータ
	 * @throws ParseException 
	 */
	// --------------------------------------------------
	public RealModelGraphDataDTO toGraphData() throws ParseException
	{
	RealModelGraphDataDTO resultData = new RealModelGraphDataDTO();
	// * 最小値・最大値の設定
	Double firstValue = (fRealValues.size()>0)?fRealValues.get(0):fModelValues.get(0);
	Double endValue = (fRealValues.size()>0)?
			fRealValues.get(fRealValues.size()-1):fModelValues.get(fModelValues.size()-1);
	resultData
			.setXStart(DateTimeUtility.getStringFromDate(startDate));
	resultData.setXEnd(DateTimeUtility.getStringFromDate(endDate));
	resultData.setYStart(firstValue);
	resultData.setYEnd(endValue);
	resultData.setValues(fRealValues);
	resultData.setPredictValues(fModelValues);
	return resultData;
	}
	
	// --------------------------------------------------
	/**
	 * F値を算出し、リストに格納する
	 *
	 * @param list 平均気温リスト
	 * @param paramSet  パラメータセット
	 * @param fStageInfoList F値情報
	 * @param sproutStage 萌芽前ステージ
	 */
	// --------------------------------------------------
	public void calculateFvalues(
			List<DailyBaseDataDTO> list,
			Ph2ParamsetGrowthEntity param,
			List<Ph2RealGrowthFStageEntity> fStageInfoList,
			short sproutStage)
		{
		startDate = list.get(0).getDate();
		endDate = list.get(list.size()-1).getDate();
		short startDay = 0;
		for(Ph2RealGrowthFStageEntity fstageInfo : fStageInfoList)
			{
			Double dValue = 0.0;
			Double eValue = 0.0;
			// * 萌芽前の場合
			if( fstageInfo.getStageEnd() <= sproutStage )
				{
				dValue = param.getBeforeD();
				eValue = param.getBeforeE();
				}
			else
				{
				dValue = param.getAfterD();
				eValue = param.getAfterE();
				}
			startDay = this.addDate(startDay, list, dValue, eValue, fstageInfo.getAccumulatedF());
			}
		this.addDate(startDay, list, param.getAfterD(), param.getAfterE(), Double.MAX_VALUE);
		}

	// --------------------------------------------------
	/**
	 * 計算開始日数から最大F値までF値を積算し、リストに格納する
	 *
	 * @param startDay 計算開始日
	 * @param list 平均気温リスト
	 * @param dValue d値
	 * @param eValue e値
	 * @param limitFValue 最大F値
	 * @return 計算最終日
	 */
	// --------------------------------------------------
	public short addDate(
			short startDay,
			List<DailyBaseDataDTO> list,
			double dValue,
			double eValue,
			double limitFValue)
		{
		short index = startDay;
		for (; index < list.size(); index++)
			{
			// * 平均気温
			DailyBaseDataDTO temperature = list.get(index);
			// * F値算出
			double fValue = Formula.toFValue(temperature.getAverage(), dValue,
					eValue);
			// * 積算F値
			lastValue = lastValue + fValue;
			if (temperature.isReal())
				{
				fRealValues.add(lastValue);
				}
			else
				{
				fModelValues.add(lastValue);
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
