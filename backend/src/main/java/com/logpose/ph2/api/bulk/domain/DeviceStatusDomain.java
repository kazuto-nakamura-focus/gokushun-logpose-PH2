package com.logpose.ph2.api.bulk.domain;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.logpose.ph2.api.dao.db.entity.Ph2DevicesEnyity;
import com.logpose.ph2.api.dao.db.entity.Ph2DevicesEnyityExample;
import com.logpose.ph2.api.dao.db.mappers.Ph2DevicesMapper;

/**
 *  デバイスデータのステータスを設定する
 */
@Component
public class DeviceStatusDomain
	{
	// ===============================================
	// クラスメンバー
	// ===============================================
	private static final String ONLOAD_ERROR = "関連デバイスが更新中です。しばらくしたら試してください。";
	@Autowired
	private Ph2DevicesMapper ph2DevicesMapper;

	// ===============================================
	// 公開関数群
	// ===============================================
	// --------------------------------------------------
	/**
	 * 指定されたデバイスのローディング中ステータスを解消する
	 * @param deviceId
	 */
	// --------------------------------------------------
	@Transactional(rollbackFor = Exception.class)
	public void setDataNotLoading(Long deviceId)
		{
		Ph2DevicesEnyity entity = this.ph2DevicesMapper.selectByPrimaryKey(deviceId);
		int status = entity.getDataStatus();
		status = (status | 1) - 1;
		this.setDataStatus(deviceId, status);
		}

	// --------------------------------------------------
	/**
	 * 指定されたデバイスに対してローディング中であることを設定する
	 * @param deviceId
	 */
	// --------------------------------------------------
	@Transactional(rollbackFor = Exception.class)
	public boolean setDataOnLoad(Long deviceId)
		{
		Ph2DevicesEnyity entity = this.ph2DevicesMapper.selectByPrimaryKey(deviceId);
		Integer status = entity.getDataStatus();
		if (null == status) status = 0;
		if ((status.intValue() & 1) == 0)
			{
			if ((status.intValue() & 128) == 0)
				{
				this.setDataStatus(deviceId, 129);
				}
			else
				this.setDataStatus(deviceId, 1);
			return true;
			}
		else
			return false;
		}

	// --------------------------------------------------
	/**
	 * 指定されたデバイスに対して初期化済みであることを設定する
	 * @param deviceId
	 */
	// --------------------------------------------------
	@Transactional(rollbackFor = Exception.class)
	public void setDataInitialized(Long deviceId)
		{
		this.setDataStatus(deviceId, 3);
		}

	// --------------------------------------------------
	/**
	 * 指定されたデバイスに対して生データ済みであることを設定する
	 * @param deviceId
	 */
	// --------------------------------------------------
	@Transactional(rollbackFor = Exception.class)
	public void setRawDataLoaded(Long deviceId)
		{
		this.setDataStatus(deviceId, 7);
		}

	// --------------------------------------------------
	/**
	 * 指定されたデバイスに対して更新バッチ時全データを更新することを通知
	 * @param deviceId
	 */
	// --------------------------------------------------
	@Transactional(rollbackFor = Exception.class)
	public void setUpdateAll(Long deviceId)
		{
		Ph2DevicesEnyity entity = this.ph2DevicesMapper.selectByPrimaryKey(deviceId);
		Integer status = entity.getDataStatus().intValue() | 128;
		entity.setDataStatus(status);
		this.ph2DevicesMapper.updateByPrimaryKey(entity);
		}

	// --------------------------------------------------
	/**
	 * 指定されたデバイスに対して更新バッチ時全データを更新することを通知
	 * @param deviceId
	 */
	// --------------------------------------------------
	@Transactional(rollbackFor = Exception.class)
	public List<Long> lockDevices(Ph2DevicesEnyity device)
		{
		List<Long> rockList = new ArrayList<>();
		if (null != device.getPreviousDeviceId())
			{
			if (!this.setDataOnLoad(device.getPreviousDeviceId()))
				{
				new RuntimeException(ONLOAD_ERROR);
				}
			rockList.add(device.getPreviousDeviceId());
			}
		Ph2DevicesEnyityExample exm = new Ph2DevicesEnyityExample();
		exm.createCriteria().andPreviousDeviceIdEqualTo(device.getId());
		List<Ph2DevicesEnyity> devices = this.ph2DevicesMapper.selectByExample(exm);
		for (Ph2DevicesEnyity ref : devices)
			{
			if (!this.setDataOnLoad(ref.getId()))
				{
				new RuntimeException(ONLOAD_ERROR);
				}
			rockList.add(ref.getId());
			}
		return rockList;
		}

	// --------------------------------------------------
	/**
	 * 指定されたデバイスリストに対してロックを解除する 
	 * @param devices
	 */
	// --------------------------------------------------
	@Transactional(rollbackFor = Exception.class)
	public void unLockDevices(List<Long> devices)
		{
		for (Long ref : devices)
			{
			this.setDataNotLoading(ref);
			}
		}

	// ===============================================
	// 保護関数群
	// ===============================================
	public void setDataStatus(Long deviceId, Integer status)
		{
		Ph2DevicesEnyity entity = this.ph2DevicesMapper.selectByPrimaryKey(deviceId);
		entity.setDataStatus(status);
		entity.setDataStatusDate(new Date());
		this.ph2DevicesMapper.updateByPrimaryKey(entity);
		}
	}
