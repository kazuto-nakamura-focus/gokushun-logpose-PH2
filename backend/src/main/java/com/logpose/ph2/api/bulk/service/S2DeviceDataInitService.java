package com.logpose.ph2.api.bulk.service;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.logpose.ph2.api.dao.db.entity.Ph2DeviceDayEntityExample;
import com.logpose.ph2.api.dao.db.entity.Ph2RelBaseDataEntityExample;
import com.logpose.ph2.api.dao.db.mappers.Ph2DeviceDayMapper;
import com.logpose.ph2.api.dao.db.mappers.Ph2RawDataMapper;
import com.logpose.ph2.api.dao.db.mappers.Ph2RelBaseDataMapper;

/**
 * デバイスの各データの初期化を行う
 */
@Service
public class S2DeviceDataInitService
	{
	// ===============================================
	// クラスメンバー
	// ===============================================
	@Autowired
	private Ph2RelBaseDataMapper ph2RelBaseDataMapper;
	@Autowired
	private Ph2RawDataMapper Ph2RawDataMapper;
	@Autowired
	private Ph2DeviceDayMapper ph2DeviceDayMapper;
	// ===============================================
	// 公開関数群
	// ===============================================
	// --------------------------------------------------
	/**
	 * デバイスの各データの削除を行う
	 * @param device
	 */
	// --------------------------------------------------
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void deleteTables(Long deviceId, Date date)
		{
		Ph2RelBaseDataEntityExample relexm = new Ph2RelBaseDataEntityExample();
		Ph2RelBaseDataEntityExample.Criteria criteria = relexm
				.createCriteria().andDeviceIdEqualTo(deviceId);
		if (null != date) criteria.andCastedAtGreaterThan(date);
		this.ph2RelBaseDataMapper.deleteByExample(relexm);
		
		this.Ph2RawDataMapper.deleteByDevice(deviceId);
		
		Ph2DeviceDayEntityExample exm = new Ph2DeviceDayEntityExample();
		Ph2DeviceDayEntityExample.Criteria criteria3 = exm
				.createCriteria().andDeviceIdEqualTo(deviceId);
		if (null != date) criteria3.andDateGreaterThan(date);
		this.ph2DeviceDayMapper.deleteByExample(exm);
		}
	}
