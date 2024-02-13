package com.logpose.ph2.api.bulk.domain;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.logpose.ph2.api.dao.db.entity.Ph2DevicesEnyity;
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
		status = (status | 1)  -1;
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
		if(null == status) status = 0;
		if((status.intValue() & 1) == 0 )
			{
			this.setDataStatus(deviceId, 1);
			return true;
			}
		else return false;
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
