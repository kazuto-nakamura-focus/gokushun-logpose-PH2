package com.logpose.ph2.batch.domain;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.logpose.ph2.batch.dao.db.entity.Ph2BaseDataEntity;
import com.logpose.ph2.batch.dao.db.entity.Ph2DashBoardEntity;
import com.logpose.ph2.batch.dao.db.entity.Ph2InsolationDataEntity;
import com.logpose.ph2.batch.dao.db.entity.Ph2RelBaseDataEntity;
import com.logpose.ph2.batch.dao.db.entity.Ph2RelBaseDataEntityExample;
import com.logpose.ph2.batch.dao.db.mappers.Ph2BaseDataMapper;
import com.logpose.ph2.batch.dao.db.mappers.Ph2DashBoardMapper;
import com.logpose.ph2.batch.dao.db.mappers.Ph2InsolationDataMapper;
import com.logpose.ph2.batch.dao.db.mappers.Ph2RelBaseDataMapper;
import com.logpose.ph2.batch.dao.db.mappers.Ph2VoltageDataMapper;
import com.logpose.ph2.batch.dto.SensorDataDTO;
import com.logpose.ph2.batch.formula.Formula;

@Component
public class BaseDataGenerator
	{
	// ===============================================
	// クラスメンバー
	// ===============================================

	@Autowired
	Ph2RelBaseDataMapper ph2RelBaseDataMapper;
	@Autowired
	Ph2VoltageDataMapper Ph2voltageDataMapper;
	@Autowired
	Ph2BaseDataMapper ph2BaseDataMapper;
	@Autowired
	Ph2DashBoardMapper dashboardMapper;
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
	public Date generate(Long deviceId, List<SensorDataDTO> records, DataListModel dataList)
		{
// * 関係レコードの取得または追加取得
		Ph2RelBaseDataEntityExample relexm = new Ph2RelBaseDataEntityExample();
		relexm.createCriteria().andCastedAtEqualTo(dataList.getCastedAt())
				.andDeviceIdEqualTo(deviceId);
		List<Ph2RelBaseDataEntity> rdbs = this.ph2RelBaseDataMapper.selectByExample(relexm);
		Ph2RelBaseDataEntity rel;
		if (rdbs.size() == 0)
			{
			rel = new Ph2RelBaseDataEntity();
			rel.setCastedAt(dataList.getCastedAt());
			rel.setDeviceId(deviceId);
			long id = this.ph2RelBaseDataMapper.insert(rel);
			rel.setId(id);
			}
		else
			{
			rel = rdbs.get(0);
			}

		for (SensorDataDTO sensor : records)
			{
			final int channel = sensor.getChannel() - 1; // * チャネル番号のずれ解消
			// * 温度の場合
			if (1 == sensor.getSensorContentId())
				{
				// * 電圧取得
				double x = dataList.getVoltages().get(channel);
				// * 計算実行
				double value = Formula.toTemperature(x);
				// * 基礎データテーブルへの登録
				Ph2BaseDataEntity entity = new Ph2BaseDataEntity();
				entity.setRelationId(rel.getId());
				entity.setTemperature((float) value);
				entity.setSensorId(sensor.getSensorId());
				this.ph2BaseDataMapper.insert(entity);
				// * ダッシュボード登録
				this.addDashboardData(deviceId, sensor.getSensorId(), sensor.getSensorContentId(), dataList.getCastedAt(),
						value);
				}
			// * 湿度
			else if (2 == sensor.getSensorContentId())
				{
				// * 電圧取得
				double x = dataList.getVoltages().get(channel);
				// * 計算実行
				double value = Formula.toHumidity(x);
				// * ダッシュボード登録
				this.addDashboardData(deviceId, sensor.getSensorId(), sensor.getSensorContentId(), dataList.getCastedAt(),
						value);
				}
			// * 日射
			else if (3 == sensor.getSensorContentId())
				{
				// * 電圧取得
				double x = dataList.getVoltages().get(channel);
				// * 計算実行
				double value = Formula.toSunLight(x);
				// * ダッシュボード登録
				this.addDashboardData(deviceId, sensor.getSensorId(), sensor.getSensorContentId(), dataList.getCastedAt(),
						value);
				// * 光合成有効放射束密度 = 9.19 * x
				value = 9.19 * x;
				Ph2InsolationDataEntity entity = new Ph2InsolationDataEntity();
				entity.setRelationId(rel.getId());
				entity.setInsolationIntensity(value);
				entity.setSensorId(sensor.getSensorId());
				// * 日射強度の登録
				this.ph2InsolationDataMapper.insert(entity);
				// * ダッシュボード登録
				this.addDashboardData(deviceId, sensor.getSensorId(), sensor.getSensorContentId(), dataList.getCastedAt(),
						value);
				}
			// * 樹液流
			else if (4 == sensor.getSensorContentId())
				{
				// * 電圧取得
				double x1 = dataList.getVoltages().get(channel);
				double x2 = dataList.getVoltages().get(channel + 1);
				double x3 = dataList.getVoltages().get(channel + 2);
				double x4 = dataList.getVoltages().get(channel + 3);
				// * 計算実行
				Calendar calendar = Calendar.getInstance();
				calendar.setTime(dataList.getCastedAt());
				double value = Formula.toSapFlow(x1, x2, x3, x4, sensor,
						calendar);
				// * ダッシュボード登録
				this.addDashboardData(deviceId, sensor.getSensorId(), sensor.getSensorContentId(),
						dataList.getCastedAt(), value);
				}
			// * デンドロ
			else if (5 == sensor.getSensorContentId())
				{
				// * 電圧取得
				double x = dataList.getVoltages().get(channel);
				// * 計算実行
				double value = Formula.toDendro(x);
				// * ダッシュボード登録
				this.addDashboardData(deviceId, sensor.getSensorId(), sensor.getSensorContentId(), dataList.getCastedAt(),
						value);
				}
			// * 葉面濡れ
			else if (6 == sensor.getSensorContentId())
				{
				// * 電圧取得
				double x = dataList.getVoltages().get(channel);
				// * 計算実行
				double value = Formula.toResitence(x);
				// * ダッシュボード登録
				this.addDashboardData(deviceId, sensor.getSensorId(),sensor.getSensorContentId(), dataList.getCastedAt(),
						value);
				}
			// * 土壌水分
			else if (7 == sensor.getSensorContentId())
				{
				// * 電圧取得
				double x = dataList.getVoltages().get(channel);
				// * 計算実行
				double value = Formula.toMoisture(x);
				// * ダッシュボード登録
				this.addDashboardData(deviceId, sensor.getSensorId(), sensor.getSensorContentId(), dataList.getCastedAt(),
						value);
				}
			// * 土壌温度
			else if (8 == sensor.getSensorContentId())
				{
				// * 電圧取得
				double x = dataList.getVoltages().get(channel);
				// * 計算実行
				double value = Formula.toTemperature(x);
				// * ダッシュボード登録
				this.addDashboardData(deviceId, sensor.getSensorId(), sensor.getSensorContentId(), dataList.getCastedAt(),
						value);
				}
			}

		return dataList.getCastedAt();
		}

	// ===============================================
	// プライベート関数群
	// ===============================================
	// -----------------------------------------------------------------
	/**
	 * ダッシュボードテーブルへの登録
	 * 
	 * @param deviceId
	 * @param contentId
	 * @param castedAt
	 * @param value
	 */
	// -----------------------------------------------------------------
	private void addDashboardData(Long deviceId,
			long sensorId,
			long contentId,
			Date castedAt,
			Double value)
		{
		Ph2DashBoardEntity entity = new Ph2DashBoardEntity();
		entity.setCastedAt(castedAt);
		entity.setContentId(contentId);
		entity.setDeviceId(deviceId);
		entity.setSensorId(sensorId);
		entity.setValue(String.valueOf(value));

		this.dashboardMapper.insert(entity);
		}
	}
