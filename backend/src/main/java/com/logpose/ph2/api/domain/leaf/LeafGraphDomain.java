package com.logpose.ph2.api.domain.leaf;

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
import com.logpose.ph2.api.utility.DateTimeUtility;

@Component
public class LeafGraphDomain extends GraphDomain
	{
	// ===============================================
	// クラスメンバー
	// ===============================================
	@Autowired
	private Ph2ModelDataMapper ph2ModelDataMapper;
	@Autowired
	private GrowthDomainMapper growthDomainMapper;

	// ===============================================
	// 公開メソッド
	// ===============================================
	// --------------------------------------------------
	/**
	 * 葉面積モデルグラフデータ取得
	 *    モデルテーブルから検索してデータを取得する
	 *
	 * @param deviceId デバイスID
	 * @param year 対象年度
	 * @return GraphDataDTO
	 * @throws ParseException 
	 */
	// --------------------------------------------------
	public List<RealModelGraphDataDTO> getModelGraph(Long deviceId, Short year)
			throws ParseException
		{
		List<ModelDataEntity> entites = this.ph2ModelDataMapper
				.selectModelDataByType(deviceId, year);
		// * データが存在しない場合 nullを返す
		if (0 == entites.size()) return null;
		if (null == entites.get(0).getfValue()) return null;

		RealModelGraphDataDTO areaModel = new RealModelGraphDataDTO();
		RealModelGraphDataDTO countModel = new RealModelGraphDataDTO();
// * 日付カテゴリ
		SimpleDateFormat sdf = new SimpleDateFormat("MM/dd");
		List<String> category = new ArrayList<>();

		Double minArea = Double.MAX_VALUE;
		Double maxArea = Double.MIN_VALUE;
		Double minCount = Double.MAX_VALUE;
		Double maxCount = Double.MIN_VALUE;
		for (ModelDataEntity entity : entites)
			{
			if (entity.getIsReal())
				{
				areaModel.getValues().add(entity.getCrownLeafArea());
				countModel.getValues().add(entity.getLeafCount());
				}
			else
				{
				areaModel.getPredictValues().add(entity.getCrownLeafArea());
				countModel.getPredictValues().add(entity.getLeafCount());
				}
			double area = (null == entity.getCrownLeafArea()) ? 0 : entity.getCrownLeafArea();
			if (minArea > area)
				minArea = area;
			if (maxArea < area)
				maxArea = area;
			double count = (null == entity.getLeafCount()) ? 0 : entity.getLeafCount();
			if (minCount > count)
				minCount = count;
			if (maxCount < count)
				maxCount = count;
// * 日付カテゴリの設定
			category.add(sdf.format(entity.getDate()));
			}
		// * 最小値・最大値の設定
		ModelDataEntity first = entites.get(0);
		ModelDataEntity last = entites.get(entites.size() - 1);
		areaModel
				.setXStart(DateTimeUtility.getStringFromDate(first.getDate()));
		areaModel.setXEnd(DateTimeUtility.getStringFromDate(last.getDate()));
		countModel.setXStart(areaModel.getXStart());
		countModel.setXEnd(areaModel.getXEnd());

		areaModel.setYStart(minArea);
		areaModel.setYEnd(maxArea);
// * 葉面積グラフのコメント設定
		super.setComment(deviceId, year, areaModel);
// * 葉面積グラフの日付カテゴリの設定
		areaModel.setCategory(category);

		countModel.setYStart(minCount);
		countModel.setYEnd(maxCount);
// * 葉枚数グラフのコメント設定
		super.setComment(deviceId, year, countModel);
// * 葉枚数グラフの日付カテゴリの設定
		countModel.setCategory(category);
		// * 値の設定
		List<RealModelGraphDataDTO> resultData = new ArrayList<>();
		resultData.add(areaModel);
		resultData.add(countModel);
		return resultData;
		}
	}
