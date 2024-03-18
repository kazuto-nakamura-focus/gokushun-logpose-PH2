package com.logpose.ph2.api.domain.photosynthesis;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.logpose.ph2.api.algorythm.DeviceDayAlgorithm;
import com.logpose.ph2.api.dao.db.entity.joined.ModelDataEntity;
import com.logpose.ph2.api.dao.db.mappers.Ph2ModelDataMapper;
import com.logpose.ph2.api.domain.GraphDomain;
import com.logpose.ph2.api.domain.common.MaxValue;
import com.logpose.ph2.api.dto.RealModelGraphDataDTO;

@Component
public class PhotoGraphDomain extends GraphDomain
	{
	// ===============================================
	// クラスメンバー
	// ===============================================
	@Autowired
	private Ph2ModelDataMapper ph2ModelDataMapper;

	// --------------------------------------------------
	/**
	 * 光合成量モデルグラフデータ取得
	 *    モデルテーブルから検索してデータを取得する
	 *
	 * @param deviceId デバイスID
	 * @param year 対象年度
	 * @return GraphDataDTO
	 * @throws ParseException 
	 */
	// --------------------------------------------------
	public RealModelGraphDataDTO getModelGraph(Long deviceId, Short year)
			throws ParseException
		{
		List<ModelDataEntity> entites = this.ph2ModelDataMapper
				.selectModelDataByType(deviceId, year);
		// * データが存在しない場合 nullを返す
		if (0 == entites.size()) return null;
		if (null == entites.get(0).getfValue()) return null;

		// * 前日の日付
		Calendar cal = Calendar.getInstance();
		Date titlleDate = new DeviceDayAlgorithm().getPreviousDay();

		RealModelGraphDataDTO areaModel = new RealModelGraphDataDTO();
// * 日付カテゴリ
		List<String> category = new ArrayList<>();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
		MaxValue max = new MaxValue();
		Double prev = null;

		for (ModelDataEntity entity : entites)
			{
			category.add(sdf.format(entity.getDate()));

			Double value = entity.getCulmitiveCnopyPs();

			if (value == null) continue;
			value = ((double) Math.round(value * 100)) / 100;
			if (null != prev)
				{
				if ((prev.doubleValue() - value.doubleValue()) > 5) continue;
				}
			prev = value;
			if (entity.getIsReal())
				{
				areaModel.getValues().add(value.doubleValue());
				areaModel.getPredictValues().add(null);
				}
			else
				{
				areaModel.getValues().add(null);
				areaModel.getPredictValues().add(value.doubleValue());
				}
// * 日付カテゴリの設定
			max.setMax(value);
// * タイトルに表示する値
			if (entity.getDate().getTime() == titlleDate.getTime())
				{
				areaModel.setEstimated(value.doubleValue());
				}
			}
// * 最小値・最大値の設定
		String first = category.get(0);
		String last = category.get(category.size() - 1);
		areaModel
				.setXStart(first);
		areaModel.setXEnd(last);
		areaModel.setYStart((double) 0);
		areaModel.setYEnd(max.getMax());
// * コメント
		super.setComment(deviceId, year, areaModel);

// * 光合成推定グラフの日付カテゴリの設定
		areaModel.setCategory(category);
		return areaModel;
		}
	}
