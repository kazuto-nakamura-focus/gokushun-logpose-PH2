package com.logpose.ph2.api.bulk.domain;

import java.util.ArrayList;
import java.util.List;

import com.logpose.ph2.api.dao.db.entity.Ph2DeviceDayEntity;
import com.logpose.ph2.api.dao.db.entity.Ph2DeviceDayEntityExample;
import com.logpose.ph2.api.dao.db.mappers.Ph2DeviceDayMapper;
import com.logpose.ph2.api.dto.DataSourceType;

import lombok.val;

public class DeviceDayDataSelector
	{
	// ===============================================
	// クラスメンバー
	// ===============================================
	private final List<DataSourceType> dataList = new ArrayList<>();
	private DataSourceType item;
	// ===============================================
	// コンストラクタ
	// ===============================================
	public DeviceDayDataSelector(Ph2DeviceDayMapper mapper, Long deviceId, Short year)
		{
		Ph2DeviceDayEntityExample exm = new Ph2DeviceDayEntityExample();
		exm.createCriteria().andDeviceIdEqualTo(deviceId).andYearEqualTo(year);
		exm.setOrderByClause("lapse_day asc");
		List<Ph2DeviceDayEntity> records = mapper.selectByExample(exm);
		for(val item : records)
			{
			this.checkData(item);
			}
		}
	// ===============================================
	// 公開関数群
	// ===============================================
	// --------------------------------------------------
	/**
	 * デバイスディデータの性質情報を作成し、返却する
	 * @param deviceId
	 * @param year
	 */
	// --------------------------------------------------
	public List<DataSourceType> createList()
		{
		dataList.add(item);
		item = null;
		return dataList;
		}
	// ===============================================
	// 保護関数群
	// ===============================================
	// --------------------------------------------------
	/**
	 * デバイスディデータの性質情報を作成し、返却する
	 * @param deviceId
	 * @param year
	 */
	// --------------------------------------------------
	public void checkData(Ph2DeviceDayEntity entity)
		{
		short sourceType = null == entity.getSourceType() ?  0 : entity.getSourceType();
		if (null == item)
			{
			item = new DataSourceType(entity.getDate(), entity.getDate(), sourceType);
			}
		else if (item.getStatus() == sourceType)
			{
			item.setEndDate(entity.getDate());
			}
		else
			{
			dataList.add(item);
			item = new DataSourceType(entity.getDate(), entity.getDate(), sourceType);
			}
		}


	}
