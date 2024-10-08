package com.logpose.ph2.api.domain.growth;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.logpose.ph2.api.algorythm.DeviceDayAlgorithm;
import com.logpose.ph2.api.container.ModelGraphDataContainer;
import com.logpose.ph2.api.dao.db.entity.joined.ModelDataEntity;
import com.logpose.ph2.api.dao.db.mappers.Ph2ModelDataMapper;
import com.logpose.ph2.api.domain.GraphDomain;
import com.logpose.ph2.api.domain.common.MaxValue;
import com.logpose.ph2.api.dto.EventDaysDTO;
import com.logpose.ph2.api.dto.graph.ModelGraphDataDTO;

@Component
public class GrowthGraphDomain extends GraphDomain
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
	 * 生育推定モデルグラフデータ取得
	 *    モデルテーブルから検索してデータを取得する
	 *
	 * @param deviceId デバイスID
	 * @param year 対象年度
	 * @return ModelGraphDataDTO
	 */
	// ###############################################
	public ModelGraphDataDTO getModelGraph(Long deviceId, Short year)
		{
		List<ModelDataEntity> entites = this.ph2ModelDataMapper
				.selectModelDataByType(deviceId, year);
// * データが存在しない場合 nullを返す
		if (0 == entites.size()) return null;
		if (null == entites.get(0).getfValue()) return null;

		ModelGraphDataContainer resultData = new ModelGraphDataContainer();

		List<Double> values = new ArrayList<>();
		List<Double> predictValues = new ArrayList<>();
// * 最大値
		MaxValue maxValue = new MaxValue();
// * 前日の日付
		Date titlleDate = new DeviceDayAlgorithm().getPreviousDay();
// * 日付カテゴリ
		boolean prev = entites.get(0).getIsReal();
		for (ModelDataEntity entity : entites)
			{
			if (entity.getIsReal())
				{
				values.add(entity.getfValue());
				if (!prev)
					{
					predictValues.add(entity.getfValue());
					prev = true;
					}
				else
					predictValues.add(null);
				}
			else
				{
				predictValues.add(entity.getfValue());
				if (prev)
					{
					values.add(entity.getfValue());
					prev = false;
					}
				else
					values.add(null);
				}
			// * 取得日
			resultData.addCategory(entity.getDate());
			// * 最大値
			maxValue.setMax(entity.getfValue());
			// * タイトルに表示する値
			if (entity.getDate().getTime() == titlleDate.getTime())
				{
				resultData.setEstimated(entity.getfValue());
				}
			}
// * 値の設定
		resultData.setValues(values);
		resultData.setPredictValues(predictValues);
// * アノテーションデータの生成
		List<EventDaysDTO> annotations = super.getEvent(deviceId, year);
		resultData.setAnnotations(annotations);
// * カテゴリーから X軸の最大値と最小値を設定する
		resultData.setX();
		maxValue.setMax(annotations.get(annotations.size() - 1).getValue() + 10);
		resultData.setY(0, maxValue.getMax());
// * コメント
		super.setComment(deviceId, year, resultData);
// * グラフデータの返却
		return resultData;
		}
	}
