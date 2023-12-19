package com.logpose.ph2.api.domain.photosynthesis;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.logpose.ph2.api.dao.db.entity.joined.ModelDataEntity;
import com.logpose.ph2.api.dao.db.mappers.Ph2ModelDataMapper;
import com.logpose.ph2.api.dao.db.mappers.joined.GrowthDomainMapper;
import com.logpose.ph2.api.domain.GraphDomain;
import com.logpose.ph2.api.dto.RealModelGraphDataDTO;

@Component
public class PhotoGraphDomain extends GraphDomain
	{
	// ===============================================
	// クラスメンバー
	// ===============================================
	@Autowired
	private Ph2ModelDataMapper ph2ModelDataMapper;
	@Autowired
	private GrowthDomainMapper growthDomainMapper;

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
		
		RealModelGraphDataDTO areaModel = new RealModelGraphDataDTO();
// * 日付カテゴリ
		List<String> category = new ArrayList<>();
		SimpleDateFormat sdf = new SimpleDateFormat("MM/dd");
		for (ModelDataEntity entity : entites)
			{
			if (entity.getIsReal())
				{
				areaModel.getValues().add(entity.getCulmitiveCnopyPs());
				}
			else
				{
				areaModel.getPredictValues().add(entity.getCulmitiveCnopyPs());
				}
// * 日付カテゴリの設定
			category.add(sdf.format(entity.getDate()));
			}
// * 最小値・最大値の設定
		String first = category.get(0);
		String last = category.get(category.size() - 1);
		areaModel
				.setXStart(first);
		areaModel.setXEnd(last);
		areaModel.setYStart(entites.get(0).getCulmitiveCnopyPs());
		areaModel.setYEnd(entites.get(entites.size() - 1).getCulmitiveCnopyPs());
// * コメント
		super.setComment(deviceId, year, areaModel);
		
// * 光合成推定グラフの日付カテゴリの設定
		areaModel.setCategory(category);
		return areaModel;
		}
	}
