package com.logpose.ph2.api.domain.photosynthesis;

import java.text.ParseException;
import java.util.List;

import com.logpose.ph2.api.dao.db.entity.Ph2ModelDataEntity;
import com.logpose.ph2.api.dao.db.entity.Ph2ModelDataEntityExample;
import com.logpose.ph2.api.dao.db.entity.Ph2RealPsAmountEntity;
import com.logpose.ph2.api.dto.DailyBaseDataDTO;
import com.logpose.ph2.api.formula.Formula;

public class PSGraphDataGenerator
	{
	// ===============================================
	// コンストラクタ
	// ===============================================
	// --------------------------------------------------
	/**
	 * 各光合成のモデルデータを対象年度とデバイスに対して生成し、出力する。
	 * @param parameters
	 * @param exporter
	 * @param dailyData
	 */
	// --------------------------------------------------
	public PSGraphDataGenerator(
			PSModelDataParameters parameters,
			PSModelDataExporter exporter,
			List<DailyBaseDataDTO> dailyData) throws ParseException
		{
		Float f_value = parameters.getParams().getFieldF().floatValue();
		Float g_value = parameters.getParams().getFieldG().floatValue();
		double prev = 0;
		for (DailyBaseDataDTO data : dailyData)
			{
// * 該当日の実測データを取得する。
			Ph2RealPsAmountEntity entity = parameters.getRealParamMap().get(data.getDate());
// * 存在する場合は実測値にパラメータを入れ替える
			if (null != entity)
				{
				if (null != entity.getValueF()) f_value = entity.getValueF();
				if (null != entity.getValueG()) g_value = entity.getValueG();
				}
// * 葉面積推定の値を得る
			Ph2ModelDataEntityExample exm = new Ph2ModelDataEntityExample();
			exm.createCriteria().andDayIdEqualTo(data.getDayId());
			Ph2ModelDataEntity model = parameters.getPh2ModelDataMapper().selectByExample(exm)
					.get(0);
			double value = Formula.toPsAmount(f_value, g_value, prev,
					model.getCrownLeafArea(), data.getPar(), data.getSunTime(),
					parameters.getShootCount());
			exporter.add(data.getDayId(), value);
			prev = value;
			}
		}
	}
