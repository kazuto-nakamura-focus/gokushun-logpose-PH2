package com.logpose.ph2.api.domain;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import com.logpose.ph2.api.dao.db.entity.Ph2ModelDataEntity;
import com.logpose.ph2.api.dao.db.entity.Ph2ModelDataEntityExample;
import com.logpose.ph2.api.dao.db.mappers.Ph2ModelDataMapper;
import com.logpose.ph2.api.dto.DailyBaseDataDTO;
import com.logpose.ph2.api.dto.PhotosynthesisParamSetDTO;
import com.logpose.ph2.api.dto.RealModelGraphDataDTO;
import com.logpose.ph2.api.formula.Formula;
import com.logpose.ph2.api.utility.DateTimeUtility;

public class PsGraphDataModel
	{
	// ===============================================
	// クラスメンバー
	// ===============================================
	RealModelGraphDataDTO psModel = new RealModelGraphDataDTO();
	List<Long> dayIds = new ArrayList<>();

	// ===============================================
	// コンストラクタ
	// ===============================================
	// --------------------------------------------------
	/**
	 * 各光合成のモデルデータを対象年度とデバイスに対して生成する
	 * @param realF
	 * @param realG
	 * @param params
	 * @param mapper
	 * @param days
	 * @throws ParseException
	 */
	// --------------------------------------------------
	public PsGraphDataModel(
			Float realF,
			Float realG,
			PhotosynthesisParamSetDTO params,
			Ph2ModelDataMapper mapper,
			List<DailyBaseDataDTO> days) throws ParseException
		{
		double minTla = Double.MAX_VALUE;
		double maxTla = Double.MIN_VALUE;
		if (null == realF)
			realF = params.getFieldF().floatValue();
		if (null == realG)
			realG = params.getFieldG().floatValue();
		double prev = 0;
		for (DailyBaseDataDTO data : days)
			{
			this.dayIds.add(data.getDayId());

			Ph2ModelDataEntityExample exm = new Ph2ModelDataEntityExample();
			exm.createCriteria().andDayIdEqualTo(data.getDayId());
			Ph2ModelDataEntity model = mapper.selectByExample(exm).get(0);

			double value = 0;
			if (data.isReal())
				{
				value = Formula.toPsAmount(realF, realG, prev,
						model.getCrownLeafArea(), data.getPar());
				}
			else
				{
				value = Formula.toPsAmount(params.getFieldF(), params.getFieldG(), prev,
						model.getCrownLeafArea(), data.getPar());
				}
			if (value < minTla)
				minTla = value;
			if (value > maxTla)
				maxTla = value;
			if (data.isReal())
				{
				this.psModel.getValues().add(value);
				}
			else
				{
				this.psModel.getPredictValues().add(value);
				}
			}
		DailyBaseDataDTO first = days.get(0);
		DailyBaseDataDTO last = days.get(days.size() - 1);
		this.psModel
				.setXStart(DateTimeUtility.getStringFromDate(first.getDate()));
		this.psModel
				.setXEnd(DateTimeUtility.getStringFromDate(last.getDate()));
		this.psModel.setYStart(minTla);
		this.psModel.setYEnd(maxTla);
		}
	}
