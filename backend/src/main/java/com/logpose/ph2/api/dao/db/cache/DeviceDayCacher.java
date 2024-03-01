package com.logpose.ph2.api.dao.db.cache;

import java.util.ArrayList;
import java.util.List;

import com.logpose.ph2.api.dao.db.entity.Ph2DeviceDayEntity;
import com.logpose.ph2.api.dao.db.entity.Ph2ModelDataEntity;
import com.logpose.ph2.api.dao.db.mappers.Ph2DeviceDayMapper;
import com.logpose.ph2.api.dao.db.mappers.Ph2ModelDataMapper;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class DeviceDayCacher
	{
	// ===============================================
	// クラスメンバー
	// ===============================================
	private long id;
	private static boolean lock = false;
	private Ph2DeviceDayMapper ph2DeviceDayMapper;
	private Ph2ModelDataMapper ph2ModelDataMapper;
// * 分ベース関係データ
	private final List<Ph2DeviceDayEntity> deviceDays = new ArrayList<>();
// * ダッシュボードデータ
	private final List<Ph2ModelDataEntity> modelData = new ArrayList<>();

	// ===============================================
	// 公開関数群
	// ===============================================
	public static synchronized boolean lock()
		{
		if( lock) return false;
		lock = true;
		return lock;
		}
	public static synchronized void unlock()
		{
		lock = false;
		}

	public long addDeviceDayData(Ph2DeviceDayEntity entity)
		{
		id++;
		entity.setId(id);
		this.deviceDays.add(entity);

		Ph2ModelDataEntity model = new Ph2ModelDataEntity();
		model.setDayId(id);
		this.modelData.add(model);

		if (this.deviceDays.size() == 1000)
			{
			this.insertDeviceDay();
			this.insertModelData();
			}
		return id;
		}

	// -----------------------------------------------------------------
	public void flush()
		{
		insertDeviceDay();
		insertModelData();
		}

	// -----------------------------------------------------------------
	private void insertDeviceDay()
		{
		if (this.deviceDays.size() > 0)
			this.ph2DeviceDayMapper.multiRowInsert(this.deviceDays);
		this.deviceDays.clear();
		}

	// -----------------------------------------------------------------
	private void insertModelData()
		{
		if (this.modelData.size() > 0)
			this.ph2ModelDataMapper.multiRowInsert(this.modelData);
		this.modelData.clear();
		}
	}
