package com.logpose.ph2.api.domain.photosynthesis;

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
import com.logpose.ph2.api.dto.graph.ModelGraphDataDTO;

@Component
public class PhotoGraphDomain extends GraphDomain
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
	 * 光合成量モデルグラフデータ取得
	 *    モデルテーブルから検索してデータを取得する
	 *
	 * @param deviceId デバイスID
	 * @param year 対象年度
	 * @return GraphDataDTO
	 */
	// ###############################################
	public ModelGraphDataDTO getModelGraph(Long deviceId, Short year)
		{
		List<ModelDataEntity> entites = this.ph2ModelDataMapper
				.selectModelDataByType(deviceId, year);
		// * データが存在しない場合 nullを返す
		if (0 == entites.size()) return null;
		if (null == entites.get(0).getfValue()) return null;

		// * 前日の日付
		Date titlleDate = new DeviceDayAlgorithm().getPreviousDay();

		ModelGraphDataContainer resultData = new ModelGraphDataContainer();
// * 日付カテゴリ
		MaxValue max = new MaxValue();
		Double prev = null;

		for (ModelDataEntity entity : entites)
			{
			// * 取得日
			resultData.addCategory(entity.getDate());

			Double value = entity.getCulmitiveCnopyPs();

			if (value == null) continue;
			// * タイトルに表示する値
			if (entity.getDate().getTime() == titlleDate.getTime())
				{
				resultData.setEstimated(value.doubleValue());
				}
			
			value = ((double) Math.round(value * 1000)) / 1000;
			if (null != prev)
				{
				if ((prev.doubleValue() - value.doubleValue()) > 5) continue;
				}
			prev = value;
			if (entity.getIsReal())
				{
				resultData.getValues().add(value.doubleValue());
				resultData.getPredictValues().add(null);
				}
			else
				{
				resultData.getValues().add(null);
				resultData.getPredictValues().add(value.doubleValue());
				}
// * 日付カテゴリの設定
			max.setMax(value);
			}
		
// * カテゴリーから X軸の最大値と最小値を設定する
		resultData.setX();
		resultData.setY(0, max.getMax());

// * コメント
		super.setComment(deviceId, year, resultData);

		return resultData;
		}
	}
