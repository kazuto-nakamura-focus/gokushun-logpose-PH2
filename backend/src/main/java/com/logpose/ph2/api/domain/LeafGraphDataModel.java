package com.logpose.ph2.api.domain;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import com.logpose.ph2.api.dto.DailyBaseDataDTO;
import com.logpose.ph2.api.dto.LeafParamSetDTO;
import com.logpose.ph2.api.dto.RealModelGraphDataDTO;
import com.logpose.ph2.api.formula.Formula;
import com.logpose.ph2.api.utility.DateTimeUtility;

public class LeafGraphDataModel
	{
	// ===============================================
	// クラスメンバー
	// ===============================================
	RealModelGraphDataDTO leafCoundModel = new RealModelGraphDataDTO();
	RealModelGraphDataDTO leafAreaModel = new RealModelGraphDataDTO();
	List<Long> dayIds = new ArrayList<>();

	// ===============================================
	// コンストラクタ
	// ===============================================
	// --------------------------------------------------
	/**
	 * 各葉面積のモデルデータを対象年度とデバイスに対して生成する
	 * @param params
	 * @param lapseDay
	 * @param shootCount
	 * @param days
	 * @throws ParseException 
	 */
	// --------------------------------------------------
	public LeafGraphDataModel(
			LeafParamSetDTO params,
			int lapseDay,
			int shootCount,
			List<DailyBaseDataDTO> days) throws ParseException
		{
		double minTla = Double.MAX_VALUE;
		double maxTla = Double.MIN_VALUE;
		double minLc = Double.MAX_VALUE;
		double maxLc = Double.MIN_VALUE;
		for (DailyBaseDataDTO data : days)
			{
			this.dayIds.add(data.getDayId());
			// * 樹冠葉面積モデル
			double tla = Formula.toLeafArea(lapseDay, shootCount, params, data);
			if (tla < minTla)
				minTla = tla;
			if (tla > maxTla)
				maxTla = tla;
			// * 葉枚数モデル式
			double lc = Formula.toCountLeaf(params, data);
			if (lc < minLc)
				minLc = lc;
			if (lc > maxLc)
				maxLc = lc;
			if (data.isReal())
				{
				this.leafCoundModel.getValues().add(lc);
				this.leafAreaModel.getValues().add(tla);
				}
			else
				{
				this.leafCoundModel.getPredictValues().add(lc);
				this.leafAreaModel.getPredictValues().add(tla);
				}
			}
		DailyBaseDataDTO first = days.get(0);
		DailyBaseDataDTO last = days.get(days.size() - 1);
		this.leafCoundModel
				.setXStart(DateTimeUtility.getStringFromDate(first.getDate()));
		this.leafCoundModel
				.setXEnd(DateTimeUtility.getStringFromDate(last.getDate()));
		this.leafAreaModel
				.setXStart(DateTimeUtility.getStringFromDate(first.getDate()));
		this.leafAreaModel
				.setXEnd(DateTimeUtility.getStringFromDate(last.getDate()));
		this.leafCoundModel.setYStart(minLc);
		this.leafCoundModel.setYEnd(maxLc);
		this.leafAreaModel.setYStart(minTla);
		this.leafAreaModel.setYEnd(maxTla);
		}
	}
