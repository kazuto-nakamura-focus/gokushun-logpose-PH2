package com.logpose.ph2.api.dao.db.cache;

import java.util.ArrayList;
import java.util.List;

import com.logpose.ph2.api.dao.db.entity.Ph2WeatherDailyMasterEntity;
import com.logpose.ph2.api.dao.db.mappers.Ph2WeatherDailyMasterMapper;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class WeatherDailyMasterCacher
	{
	// ===============================================
	// クラスメンバー
	// ===============================================
	private Ph2WeatherDailyMasterMapper ph2WeatherDailyMasterCacher;
// * 日別データ
	private final List<Ph2WeatherDailyMasterEntity> entities = new ArrayList<>();

	// ===============================================
	// 公開関数群
	// ===============================================
	public void add(Ph2WeatherDailyMasterEntity entity)
		{
		this.entities.add(entity);
		if (this.entities.size() == 1000)
			{
			this.insert();
			}
		}
	// -----------------------------------------------------------------
	private void insert()
		{
		this.ph2WeatherDailyMasterCacher.multiRowInsert(this.entities);
		this.entities.clear();
		}

	// -----------------------------------------------------------------
	public void flush()
		{
		if(this.entities.size() > 0) insert();
		}	
	}
