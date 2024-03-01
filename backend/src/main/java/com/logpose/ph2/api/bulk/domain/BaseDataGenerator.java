package com.logpose.ph2.api.bulk.domain;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.logpose.ph2.api.dao.db.cache.MinutesCacher;
import com.logpose.ph2.api.dao.db.mappers.Ph2InsolationDataMapper;
import com.logpose.ph2.api.dao.db.mappers.Ph2RawDataMapper;
import com.logpose.ph2.api.dao.db.mappers.Ph2RelBaseDataMapper;
import com.logpose.ph2.api.dto.SensorDataDTO;
import com.logpose.ph2.api.formula.Formula;

@Component
public class BaseDataGenerator
	{
	// ===============================================
	// クラスメンバー
	// ===============================================
	@Autowired
	Ph2RelBaseDataMapper ph2RelBaseDataMapper;
	@Autowired
	Ph2RawDataMapper dashboardMapper;
	@Autowired
	Ph2InsolationDataMapper ph2InsolationDataMapper;

	// ===============================================
	// 公開関数群
	// ===============================================
	// -----------------------------------------------------------------
	/**
	 * ダッシュボード・生データ・グラフデータの作成をする
	 * @param deviceId
	 * @param dataList
	 * @return 処理をした受信日
	 */
	// -----------------------------------------------------------------
	public Date generate(
			Long deviceId,
			List<SensorDataDTO> records,
			DataListModel dataList,
			MinutesCacher cache)
		{
		long id = cache.addRelBaseData(deviceId, dataList.getCastedAt());

		for (SensorDataDTO sensor : records)
			{
			final int channel = sensor.getChannel() - 1; // * チャネル番号のずれ解消
			long contentId = sensor.getSensorContentId().longValue();
			// * 温度の場合
			if (1 == contentId)
				{
				// * 電圧取得
				double x = dataList.getVoltages().get(channel);
				// * 計算実行
				double value = Formula.toTemperature(x);
				// * 基礎データテーブルへの登録
				cache.addBaseData(id, (float) value, sensor.getSensorId());
				// * ダッシュボード登録
				cache.addRawDataData(deviceId, sensor.getSensorId(), sensor.getSensorContentId(),
						dataList.getCastedAt(),
						value);
				}
			// * 湿度
			else if (2 == contentId)
				{
				// * 電圧取得
				double x = dataList.getVoltages().get(channel);
				// * 計算実行
				double value = Formula.toHumidity(x);
				// * ダッシュボード登録
				cache.addRawDataData(deviceId, sensor.getSensorId(), sensor.getSensorContentId(),
						dataList.getCastedAt(),
						value);
				}
			// * 日射
			else if (3 == contentId)
				{
				// * 電圧取得
				double x = dataList.getVoltages().get(channel);
				// * 計算実行
				double value = Formula.toSunLight(x);
				// * ダッシュボード登録
				cache.addRawDataData(deviceId, sensor.getSensorId(), sensor.getSensorContentId(),
						dataList.getCastedAt(),
						value);
				// * 光合成有効放射束密度 = 9.19 * x
				value = 9.19 * x;
				// * ダッシュボード登録
				cache.addInsolationData(id, value, sensor.getSensorId());
				}
			// * 樹液流
			else if (4 == contentId)
				{
				// * 電圧取得
				double x1 = dataList.getVoltages().get(channel);
				double x2 = dataList.getVoltages().get(channel + 1);
				double x3 = dataList.getVoltages().get(channel + 2);
				double x4 = dataList.getVoltages().get(channel + 3);
				// * 計算実行
				Calendar calendar = Calendar.getInstance();
				calendar.setTime(dataList.getCastedAt());
				Double value = Double.valueOf(0);
				try
					{
					value = Formula.toSapFlow(x1, x2, x3, x4, sensor,
							calendar);
					}
				catch (NullPointerException e)
					{
					// valueは０のままなので、下で警告表示
					}
				if (value != null)
					{
					// * ダッシュボード登録
					cache.addRawDataData(deviceId, sensor.getSensorId(),
							sensor.getSensorContentId(),
							dataList.getCastedAt(),
							value);
					}
				}
			// * デンドロ
			else if (5 == contentId)
				{
				// * 電圧取得
				double x = dataList.getVoltages().get(channel);
				// * 計算実行
				double value = Formula.toDendro(x);
				// * ダッシュボード登録
				cache.addRawDataData(deviceId, sensor.getSensorId(), sensor.getSensorContentId(),
						dataList.getCastedAt(),
						value);
				}
			// * 葉面濡れ
			else if (6 == contentId)
				{
				// * 電圧取得
				double x = dataList.getVoltages().get(channel);
				// * 計算実行
				// double value = Formula.toResitence(x);
				double value = x;
				// * ダッシュボード登録
				cache.addRawDataData(deviceId, sensor.getSensorId(), sensor.getSensorContentId(),
						dataList.getCastedAt(),
						value);
				}
			// * 土壌水分
			else if (7 == contentId)
				{
				// * 電圧取得
				double x = dataList.getVoltages().get(channel);
				// * 計算実行
				double value = Formula.toMoisture(x);
				// * ダッシュボード登録
				cache.addRawDataData(deviceId, sensor.getSensorId(), sensor.getSensorContentId(),
						dataList.getCastedAt(),
						value);
				}
			// * 土壌温度
			else if (8 == contentId)
				{
				// * 電圧取得
				double x = dataList.getVoltages().get(channel);
				// * 計算実行
				double value = Formula.toTemperature(x);
				// * ダッシュボード登録
				cache.addRawDataData(deviceId, sensor.getSensorId(), sensor.getSensorContentId(),
						dataList.getCastedAt(),
						value);
				}
			// * 土壌温度
			else if (8 == contentId)
				{
				// * 電圧取得
				double x = dataList.getVoltages().get(channel);
				// * 計算実行
				double value = Formula.toTemperature(x);
				// * ダッシュボード登録
				cache.addRawDataData(deviceId, sensor.getSensorId(), sensor.getSensorContentId(),
						dataList.getCastedAt(),
						value);
				}
			// * オーガニック土壌体積含水率算出
			else if (9 == contentId)
				{
				// * 電圧取得
				double x = dataList.getVoltages().get(channel);
				// * 計算実行
				double value = Formula.toVMCOrgaic(x) * 100;
				// * ダッシュボード登録
				cache.addRawDataData(deviceId, sensor.getSensorId(), sensor.getSensorContentId(),
						dataList.getCastedAt(),
						value);
				}
			// * ミネラル土壌体積含水率算出
			else if (10 == contentId)
				{
				// * 電圧取得
				double x = dataList.getVoltages().get(channel);
				// * 計算実行
				double value = Formula.toVMCMineral(x) * 100;
				// * ダッシュボード登録
				cache.addRawDataData(deviceId, sensor.getSensorId(), sensor.getSensorContentId(),
						dataList.getCastedAt(),
						value);
				}
			}
		return dataList.getCastedAt();
		}
	}
