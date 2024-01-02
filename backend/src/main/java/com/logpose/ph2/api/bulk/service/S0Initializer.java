package com.logpose.ph2.api.bulk.service;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.logpose.ph2.api.bulk.vo.LoadCoordinator;
import com.logpose.ph2.api.dao.db.entity.Ph2DevicesEnyity;
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
	 *  データロードのパラメータを作成する
	 * @param device デバイス情報
	 * @param isAllData 全てのデータを更新するかどうか
	 * @param startDate 日付指定。NULLなら最後の更新日
	 * @return LoadCoordinator
	 */
	// --------------------------------------------------
	@Transactional(readOnly = true)
	public LoadCoordinator initializeCoordinator(Ph2DevicesEnyity device, boolean isAllData,
			Date startDate)
		{
		LoadCoordinator coordinator = new LoadCoordinator(ph2RelBaseDataMapper);
		coordinator.setDevice(device);
		if (!isAllData)
			coordinator.setInitialStartDate(startDate);
		coordinator.setSensors(this.ph2JoinedMapper);
		return coordinator;
		}

	@Transactional(readOnly = true)
	public LoadCoordinator initializeCoordinator(Long deviceId, boolean isAllData, Date startDate)
		{
		LoadCoordinator coordinator = new LoadCoordinator(ph2RelBaseDataMapper);
		coordinator.setDevice(deviceId, this.ph2DeviceMapper);
		if (!isAllData)
			coordinator.setInitialStartDate(startDate);
		coordinator.setSensors(this.ph2JoinedMapper);
		return coordinator;
		}

	// --------------------------------------------------
	/**
	 *  全てのデバイス情報をDBから取得して返す
	 * @return デバイス情報
	 */
	// --------------------------------------------------
	@Transactional(readOnly = true)
	public List<Ph2DevicesEnyity> getDeviceAllInfo()
		{
		return this.ph2DeviceMapper.selectAll();
		}
	}
