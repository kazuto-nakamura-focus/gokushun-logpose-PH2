package com.logpose.ph2.api.domain;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.logpose.ph2.api.dao.db.entity.joined.ModelAndDailyDataEntity;
import com.logpose.ph2.api.dao.db.mappers.Ph2ModelDataMapper;

public class ModelDataDomain
	{
	// ===============================================
	// クラスメンバー
	// ===============================================
	@Autowired
	private Ph2ModelDataMapper ph2ModelDataMapper;
	
	// ===============================================
	// 公開関数群
	// ===============================================
	// ###############################################
	/**
	 * モデルデータの取得を行う
	 * @param deviceId-デバイスID
	 * @param year-対象年度
	 */
	// ###############################################
	public List<ModelAndDailyDataEntity> get(long deviceId, short year)
		{
		return this.ph2ModelDataMapper.selectModelAndDailyData(deviceId, year);
		}
	// ###############################################
	/**
	 * モデルデータの更新を行う
	 * @param dailyData List<ModelAndDailyDataEntity>
	 */
	// ###############################################
	public void upate(List<ModelAndDailyDataEntity> dailyData) 
		{
		for(final ModelAndDailyDataEntity entity : dailyData)
			{
			this.ph2ModelDataMapper.updateByPrimaryKey(entity);
			}
		}

	}
