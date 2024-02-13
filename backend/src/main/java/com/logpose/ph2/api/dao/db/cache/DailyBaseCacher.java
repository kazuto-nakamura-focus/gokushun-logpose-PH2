package com.logpose.ph2.api.dao.db.cache;

import java.util.ArrayList;
import java.util.List;

import com.logpose.ph2.api.dao.db.entity.Ph2DailyBaseDataEntity;
import com.logpose.ph2.api.dao.db.mappers.Ph2DailyBaseDataMapper;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class DailyBaseCacher
	{
	// ===============================================
	// クラスメンバー
	// ===============================================
	private Ph2DailyBaseDataMapper ph2DailyBaseDataMapper;
// * 日別データ
	private final List<Ph2DailyBaseDataEntity> dailyBaseData = new ArrayList<>();

	// ===============================================
	// 公開関数群
	// ===============================================
	public void addDailyBaseData(Ph2DailyBaseDataEntity entity)
		{
		this.dailyBaseData.add(entity);
		if (this.dailyBaseData.size() == 1000)
			{
			this.insertDailyBaseData();
			}
		}
	// -----------------------------------------------------------------
	private void insertDailyBaseData()
		{
		this.ph2DailyBaseDataMapper.multiRowInsert(this.dailyBaseData);
		this.dailyBaseData.clear();
		}

	// -----------------------------------------------------------------
	public void flush()
		{
		if(this.dailyBaseData.size() > 0) insertDailyBaseData();
		}	
	}
