package com.logpose.ph2.api.dao.db.cache;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.logpose.ph2.api.dao.db.entity.Ph2BaseDataEntity;
import com.logpose.ph2.api.dao.db.entity.Ph2RawDataEntity;
import com.logpose.ph2.api.dao.db.entity.Ph2InsolationDataEntity;
import com.logpose.ph2.api.dao.db.entity.Ph2RelBaseDataEntity;
import com.logpose.ph2.api.dao.db.mappers.Ph2BaseDataMapper;
import com.logpose.ph2.api.dao.db.mappers.Ph2RawDataMapper;
import com.logpose.ph2.api.dao.db.mappers.Ph2InsolationDataMapper;
import com.logpose.ph2.api.dao.db.mappers.Ph2RelBaseDataMapper;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class MinutesCacher
	{
	// ===============================================
	// クラスメンバー
	// ===============================================
	private long id;
	private Ph2RelBaseDataMapper ph2RelBaseDataMapper;
	private Ph2BaseDataMapper ph2BaseDataMapper;
	private Ph2RawDataMapper ph2DashboardMapper;
	private Ph2InsolationDataMapper ph2InsolationDataMapper;
// * 分ベース関係データ
	private final List<Ph2RelBaseDataEntity> relBaseData = new ArrayList<>();
// * ダッシュボードデータ
	private final List<Ph2RawDataEntity> dashBoads = new ArrayList<>();
// * 光合成有効放射束密度 / 日射強度
	private final List<Ph2InsolationDataEntity> Insolation = new ArrayList<>();
// * 温度基礎データ
	private final List<Ph2BaseDataEntity> baseDatum = new ArrayList<>();

	// ===============================================
	// 公開関数群
	// ===============================================
	public long addRelBaseData(Long deviceId, Date castedDay)
		{
		id++;
		Ph2RelBaseDataEntity rel = new Ph2RelBaseDataEntity();
		rel.setId(id);
		rel.setCastedAt(castedDay);
		rel.setDeviceId(deviceId);
		this.relBaseData.add(rel);
		if (this.relBaseData.size() == 1000)
			{
			this.insertRelBaseData();
			}
		return id;
		}

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
	public void addDashboardData(Long deviceId,
			long sensorId,
			long contentId,
			Date castedAt,
			Double value)
		{
		Ph2RawDataEntity entity = new Ph2RawDataEntity();
		entity.setCastedAt(castedAt);
		entity.setContentId(contentId);
		entity.setDeviceId(deviceId);
		entity.setSensorId(sensorId);
		entity.setValue(String.valueOf(value));
		this.dashBoads.add(entity);
		if (this.dashBoads.size() == 1000)
			this.insertDashBoard();
		}

	// -----------------------------------------------------------------
	public void addBaseData(
			Long relationId,
			Float temperature,
			Long senserId)
		{
		Ph2BaseDataEntity entity = new Ph2BaseDataEntity();
		entity.setRelationId(relationId);
		entity.setTemperature(temperature);
		entity.setSensorId(senserId);
		this.baseDatum.add(entity);
		if (this.baseDatum.size() == 1000)
			{
			if (this.relBaseData.size() > 0)
				this.insertRelBaseData();
			this.insertBaseData();
			}
		}

	// -----------------------------------------------------------------
	public void addInsolationData(
			Long relationId,
			Double value,
			Long senserId)
		{
		Ph2InsolationDataEntity entity = new Ph2InsolationDataEntity();
		entity.setRelationId(relationId);
		entity.setInsolationIntensity(value);
		entity.setSensorId(senserId);
		this.Insolation.add(entity);
		if (this.Insolation.size() == 1000)
			{
			if (this.relBaseData.size() > 0)
				this.insertRelBaseData();
			this.insertInsolationData();
			}
		}

	// -----------------------------------------------------------------
	public void flush()
		{
		if(this.relBaseData.size() > 0) insertRelBaseData();
		if(this.baseDatum.size() > 0) insertBaseData();
		if(this.Insolation.size() > 0) insertInsolationData();
		if(this.dashBoads.size() > 0) insertDashBoard();
		}

	// -----------------------------------------------------------------
	private void insertRelBaseData()
		{
		this.ph2RelBaseDataMapper.multiRowInsert(this.relBaseData);
		this.relBaseData.clear();
		}

	// -----------------------------------------------------------------
	private void insertDashBoard()
		{
		this.ph2DashboardMapper.multiRowInsert(this.dashBoads);
		this.dashBoads.clear();
		}

	// -----------------------------------------------------------------
	private void insertBaseData()
		{
		this.ph2BaseDataMapper.multiRowInsert(this.baseDatum);
		this.baseDatum.clear();
		}

	// -----------------------------------------------------------------
	private void insertInsolationData()
		{
		this.ph2InsolationDataMapper.multiRowInsert(this.Insolation);
		this.Insolation.clear();
		}

	}
