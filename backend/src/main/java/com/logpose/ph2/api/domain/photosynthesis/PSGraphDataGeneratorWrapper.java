package com.logpose.ph2.api.domain.photosynthesis;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.logpose.ph2.api.dao.db.entity.Ph2RealPsAmountEntity;
import com.logpose.ph2.api.dao.db.entity.joined.ModelAndDailyDataEntity;
import com.logpose.ph2.api.dao.db.mappers.Ph2ModelDataMapper;
import com.logpose.ph2.api.dto.photosynthesis.PhotosynthesisParamSetDTO;

@Component
public class PSGraphDataGeneratorWrapper extends PSModelDataParameterAggregator
	{
	// ===============================================
	// クラスメンバー
	// ===============================================
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
	 * @param fValue F値
	 * @param gValue G値
	 * @param map 実測値Map
	 */
	// ###############################################
	public void updateModelTable(Long deviceId, Short year, Double fValue, Double gValue,
			Map<Date, Ph2RealPsAmountEntity> map)
		{
// * 指定デバイスの指定年度の日ごとおよびモデルデータを取得する
		List<ModelAndDailyDataEntity> data = this.ph2ModelDataMapper.selectModelAndDailyData(deviceId, year);

// * 葉面積パラメータセットの取得
		if ((null == fValue) && (null == gValue))
			{
			PhotosynthesisParamSetDTO parameters = this.getParmaters(deviceId, year);
			fValue = parameters.getFieldF();
			gValue = parameters.getFieldG();
			}

// * 実測値Mapの設定
		if (null == map)
			map = this.setRealValueDateMap(deviceId, year);

// * データの出力先の設定をする
		if (0 != data.size())
			{
			// * データ生成のためのパラメータを作成する。
			new PSGraphDataGenerator(fValue, gValue, map, this.ph2ModelDataMapper, data);
			}
		}
	}
