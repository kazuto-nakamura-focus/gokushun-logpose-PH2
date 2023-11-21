package com.logpose.ph2.api.domain.leaf;

import java.text.ParseException;
import java.util.List;

import com.logpose.ph2.api.dto.DailyBaseDataDTO;
import com.logpose.ph2.api.formula.Formula;

public class LeafModelDataGenerator
	{
	// ===============================================
	// コンストラクタ
	// ===============================================
	// --------------------------------------------------
	/**
	 * 各葉面積のモデルデータを対象年度とデバイスに対して生成する
	 * @param parameters
	 * @param exporter
	 * @param dailyData
	 */
	// --------------------------------------------------
	public LeafModelDataGenerator(
			LeafModelDataParameters parameters,
			LeafModelDataExporter exporter,
			List<DailyBaseDataDTO> dailyData) throws ParseException
		{
		for (DailyBaseDataDTO data : dailyData)
			{
// * 萌芽日からの経過日数を算出
			int lapse_day = data.getLapseDay() - parameters.getLapseDay();
			
			double tla = 0; // * 樹冠葉面積モデル値
// * 経過日数が１以上なら
			if( lapse_day > 0 )
				{
	// * wible値を取得
				double wible = parameters.getWibles().get(lapse_day).getValue();
	// * 樹冠葉面積モデル値を算出
				tla = Formula.toLeafArea(parameters.getShootCount(), 
					parameters.getParams(), data.getCdd(), wible);
				}
// * 葉枚数モデル式
			double lc = Formula.toCountLeaf(parameters.getParams(), data);
// * エキスポート(DBへ)
			exporter.add(data.getDayId(), tla, lc);
			}
		}
	}
