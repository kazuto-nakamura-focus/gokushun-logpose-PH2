package com.logpose.ph2.api.domain.leaf;

import java.text.ParseException;
import java.util.List;

import com.logpose.ph2.api.dao.db.entity.Ph2DailyBaseDataEntity;
import com.logpose.ph2.api.dao.db.mappers.Ph2DailyBaseDataMapper;
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
			Ph2DailyBaseDataMapper mapper,
			LeafModelDataParameters parameters,
			LeafModelDataExporter exporter,
			List<DailyBaseDataDTO> dailyData) throws ParseException
		{
		double cdd = 0;
		for (DailyBaseDataDTO data : dailyData)
			{
// * 萌芽日からの経過日数を算出
			int lapse_day = data.getLapseDay() - parameters.getLapseDay();
			
			Ph2DailyBaseDataEntity daily_base_data = mapper.selectByPrimaryKey(data.getDayId());
			double tla = 0; // * 樹冠葉面積モデル値
// * 経過日数が１以上なら
			if( lapse_day > 0 )
				{
				cdd += data.getRawCdd();
				daily_base_data.setCdd(cdd);
				data.setCdd(cdd);
	// * wible値を取得
				double wible = parameters.getWibles().get(lapse_day).getValue();
	// * 樹冠葉面積モデル値を算出
				tla = Formula.toLeafArea(parameters.getShootCount(), 
					parameters.getParams(), cdd, wible);
				}
			else
				{
				daily_base_data.setCdd((double) 0);
				data.setCdd(0);
				}
			mapper.updateByPrimaryKey(daily_base_data);
			
// * 葉枚数モデル式
			double lc = Formula.toCountLeaf(parameters.getParams(), data);
// * エキスポート(DBへ)
			exporter.add(data.getDayId(), tla, lc);
			}
		}
	}
