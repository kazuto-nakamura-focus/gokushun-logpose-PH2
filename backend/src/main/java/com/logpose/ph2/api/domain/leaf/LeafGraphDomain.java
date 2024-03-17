package com.logpose.ph2.api.domain.leaf;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.logpose.ph2.api.dao.db.entity.joined.LeafModelDataEntity;
import com.logpose.ph2.api.dao.db.mappers.Ph2ModelDataMapper;
import com.logpose.ph2.api.domain.GraphDomain;
import com.logpose.ph2.api.domain.common.MaxValue;
import com.logpose.ph2.api.dto.RealModelGraphDataDTO;

import lombok.Data;

@Component
public class LeafGraphDomain extends GraphDomain
	{
	// ===============================================
	// クラスメンバー
	// ===============================================
	@Autowired
	private Ph2ModelDataMapper ph2ModelDataMapper;

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
		List<LeafModelDataEntity> entites = this.ph2ModelDataMapper
				.selectLeafModelDataByType(deviceId, year);
		// * データが存在しない場合 nullを返す
		if (0 == entites.size()) return null;
		if (null == entites.get(0).getfValue()) return null;

		RealModelGraphDataDTO areaModel = new RealModelGraphDataDTO();
		RealModelGraphDataDTO countModel = new RealModelGraphDataDTO();
// * 日付カテゴリ
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
		List<String> category = new ArrayList<>();

		MaxValue maxArea =  new MaxValue();
		MaxValue maxCount = new MaxValue();

		List<MeasureDataItem> measureDataList = new ArrayList<>();
		int index = 0;
		for (LeafModelDataEntity entity : entites)
			{
// * 実測値の値代入
			areaModel.getMeauredValues().add(entity.getRealArea());
// * 実績値がある場合
			if (null != entity.getRealArea())
				{
				MeasureDataItem item = new MeasureDataItem();
				item.setIndex(index);
				item.setMeasure(entity.getRealArea());
				item.setModel(entity.getCrownLeafArea());
				item.setDiff(item.getMeasure() - item.getModel());
				measureDataList.add(item);
				}
			index++;
// * 実績値の葉枚数と面積の代入
			if (entity.getIsReal())
				{
				areaModel.getValues().add(entity.getCrownLeafArea());
				countModel.getValues().add(entity.getLeafCount());
				areaModel.getPredictValues().add(null);
				countModel.getPredictValues().add(null);
				}
// * 予想値の葉枚数と面積の代入
			else
				{
				areaModel.getPredictValues().add(entity.getCrownLeafArea());
				countModel.getPredictValues().add(entity.getLeafCount());
				areaModel.getValues().add(null);
				countModel.getValues().add(null);
				}
// * 葉面積グラフの最大値の設定
				maxArea.setMax(entity.getCrownLeafArea());
// * 実測値がある場合は葉面積グラフの最大値をその値も参照する
				maxArea.setMax(entity.getRealArea());
// * 葉枚数の最大値の設定
				maxCount.setMax(entity.getLeafCount());

// * 日付カテゴリの設定
			category.add(sdf.format(entity.getDate()));
			}
// * 実測値のモデルデータ作成
		this.setMeasuredValues(areaModel.getValues(), areaModel.getMeauredValues(), measureDataList, maxArea);
// * 最小値・最大値の設定
		String first = category.get(0);
		String last = category.get(category.size() - 1);
		areaModel.setXStart(first);
		areaModel.setXEnd(last);

		areaModel.setYStart((double) 0);
		areaModel.setYEnd(maxArea.getMax());
// * 葉面積グラフのコメント設定
		super.setComment(deviceId, year, areaModel);
// * 葉面積グラフの日付カテゴリの設定
		areaModel.setCategory(category);

		countModel.setXStart(areaModel.getXStart());
		countModel.setXEnd(areaModel.getXEnd());
		countModel.setYStart((double) 0);
		countModel.setYEnd(maxCount.getMax());
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

	// --------------------------------------------------
	/**
	 * 実測値から想定グラフを作成する
	 *
	 * @param deviceId デバイスID
	 * @param year 対象年度
	 * @return GraphDataDTO
	 * @throws ParseException 
	 */
	// --------------------------------------------------
	private void setMeasuredValues(List<Double> modelDataList, List<Double> measureDataList,
			List<MeasureDataItem> existData, MaxValue max)
		{
		MeasureDataItem prev = null;

		for (MeasureDataItem item : existData)
			{
			if (null == prev)
				{
				prev = item;
				continue;
				}
			if ((item.getIndex() - prev.getIndex()) > 1)
				{
				// * 前回との差の差分
				double diff = item.getDiff() - prev.getDiff();
				// * 差の分割
				double diff_per_day = diff / (item.getIndex() - prev.getIndex());
				double dayValueDiff = prev.getDiff();
				for (int i = prev.getIndex() + 1; i < item.getIndex(); i++)
					{
					// その日のdiffを決定する
					dayValueDiff = dayValueDiff + diff_per_day;
					// その日のモデル値
					double model = modelDataList.get(i);
					// その日の実測値
					double value = model + dayValueDiff;
					// その日の実測値を設定
					measureDataList.set(i, value);
					// 最大値の設定
					max.setMax(value);
					}
				}
			prev = item;
			}
		}
	}

@Data
class MeasureDataItem
	{
	private int index;
	private double measure;
	private double model;
	private double diff;
	}