package com.logpose.ph2.api.bulk.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.logpose.ph2.api.bulk.vo.LoadCoordinator;
import com.logpose.ph2.api.dao.db.entity.Ph2DevicesEntity;
import com.logpose.ph2.api.dao.db.mappers.Ph2DevicesMapper;
import com.logpose.ph2.api.dao.db.mappers.Ph2RelBaseDataMapper;
import com.logpose.ph2.api.dao.db.mappers.joined.Ph2JoinedMapper;

@Service
public class S0Initializer
	{
	@Autowired
	private Ph2DevicesMapper ph2DeviceMapper;
	@Autowired
	private Ph2RelBaseDataMapper ph2RelBaseDataMapper;
	@Autowired
	Ph2JoinedMapper ph2JoinedMapper;

	// ===============================================
	// パブリック関数群
	// ===============================================
	// --------------------------------------------------
	/**
	 *  デバイス情報を取得する
	 * @param deviceId 取得するデバイス情報のID
	 * @return デバイス情報
	 */
	// --------------------------------------------------
	@Transactional(readOnly = true)
	public Ph2DevicesEntity getDeviceInfo(Long deviceId)
		{
		return this.ph2DeviceMapper.selectByPrimaryKey(deviceId);
		}

	// --------------------------------------------------
	/**
	 *  データロードのパラメータを作成する
	 * @param device デバイス情報
	 * @param isAllData 全てのデータを更新するかどうか
	 * @return LoadCoordinator
	 */
	// --------------------------------------------------
	@Transactional(readOnly = true)
	public LoadCoordinator initializeCoordinator(Ph2DevicesEntity device, boolean isAllData)
		{
		LoadCoordinator coordinator = new LoadCoordinator(isAllData, ph2RelBaseDataMapper);
		coordinator.setDevice(device);
		coordinator.setSensors(this.ph2JoinedMapper);
		if (!isAllData) coordinator.setInitialStartDate();
		return coordinator;
		}

	}
