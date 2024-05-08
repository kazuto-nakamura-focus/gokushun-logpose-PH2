package com.logpose.ph2.api.domain.photosynthesis;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.logpose.ph2.api.dao.db.entity.Ph2RealPsAmountEntity;
import com.logpose.ph2.api.dao.db.entity.Ph2RealPsAmountEntityExample;
import com.logpose.ph2.api.dao.db.entity.joined.ModelAndDailyDataEntity;
import com.logpose.ph2.api.dao.db.mappers.Ph2ModelDataMapper;
import com.logpose.ph2.api.dao.db.mappers.Ph2RealPsAmountMapper;
import com.logpose.ph2.api.dto.photosynthesis.PhotosynthesisParamSetDTO;
import com.logpose.ph2.api.formula.Formula;

@Component
public class PSModelDataDomain
	{
	// ===============================================
	// クラスメンバー
	// ===============================================
	@Autowired
	private Ph2RealPsAmountMapper ph2RealPsAmountMapper;
	@Autowired
	private PSParameterSetDomain pSParameterSetDomain;
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
	public void updateModelData(Long devieId, Short year, List<ModelAndDailyDataEntity> data)
		{
// * モデルデータの更新
		this.updateModelData(devieId, year, null, null, null, data);
		}
	// ###############################################
	/**
	 * デバイスのモデルテーブルを更新する
	 * 
	 * @param deviceId デバイスID
	 * @param year 年度
	 * @param fValue F値
	 * @param gValue G値
	 * @param map 実測値Map
	 * @param data 更新対象となる日ごととモデルのデータ
	 */
	// ###############################################
	public void updateModelData(Long deviceId, Short year, Double fValue, Double gValue,
			Map<Date, Ph2RealPsAmountEntity> map, List<ModelAndDailyDataEntity> data)
		{
// * 葉面積パラメータセットの取得
		if ((null == fValue) || (null == gValue))
			{
			PhotosynthesisParamSetDTO parameters = this.pSParameterSetDomain.getParmaters(deviceId, year);
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
			this.updateModelData(fValue, gValue, map, this.ph2ModelDataMapper, data);
			}
		}
	// ===============================================
	// 保護関数群
	// ===============================================
	// ###############################################
	/**
	 * 実測値を取得する
	 * 存在しない場合はデフォルト値を設定して返却
	 * 
	 * @param deviceId-デバイスID
	 * @param year-対象年度
	 * @return Ph2ParamsetGrowthEntity
	 */
	// ###############################################
	protected Map<Date, Ph2RealPsAmountEntity> setRealValueDateMap(Long deviceId, Short year)
		{
		Ph2RealPsAmountEntityExample exm = new Ph2RealPsAmountEntityExample();
		exm.createCriteria().andDeviceIdEqualTo(deviceId).andYearEqualTo(year);
		List<Ph2RealPsAmountEntity> entities = this.ph2RealPsAmountMapper.selectByExample(exm);
		Map<Date, Ph2RealPsAmountEntity> map = new HashMap<>();
		for(Ph2RealPsAmountEntity entity : entities)
			{
			map.put(entity.getDate(), entity);
			}
		return map;
		}
	// ###############################################
	/**
	 * 各光合成のモデルデータを対象年度とデバイスに対して生成し、DBに保存する
	 * @param deviceId デバイスID
	 * @param year 年度
	 * @param fValue F値
	 * @param gValue G値
	 * @param map 実測値Map
	 * @param dailyData 元となる日ごとデータ
	 */
	// ###############################################
	private void updateModelData(
			double fValue,
			double gValue,
			Map<Date, Ph2RealPsAmountEntity>  realDataMap,
			Ph2ModelDataMapper ph2ModelDataMapper,
			List<ModelAndDailyDataEntity> dailyData)
		{
// * 積算値
		double accumulatedValue = 0;
		for (ModelAndDailyDataEntity data : dailyData)
			{
// * 該当日の実測データを取得する。
			Ph2RealPsAmountEntity entity = realDataMap.get(data.getDate());
// * 存在する場合は実測値にパラメータを入れ替える
			if (null != entity)
				{
				if (null != entity.getValueF()) fValue = entity.getValueF();
				if (null != entity.getValueG()) gValue = entity.getValueG();
				}
// * 葉面積推定の値を得る
			double value = Formula.toPsAmount(fValue, gValue, accumulatedValue,
					data.getCrownLeafArea(), data.getPar(), data.getSunTime());
// * 推定積算樹冠光合成量データの更新
			data.setCulmitiveCnopyPs(value);
// * モデルデータの更新
			ph2ModelDataMapper.updateByPrimaryKey(data);
// * 積算値の更新
			accumulatedValue =  ((double)Math.round(value * 100))/100;
			}
		}
	}
