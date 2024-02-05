package com.logpose.ph2.api.bulk.domain;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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
	 * 指定されたデバイスに対して初期化済みであることを設定する
	 * @param deviceId
	 */
	// --------------------------------------------------
	public void setDataInitialized(Long deviceId)
		{
		this.setDataStatus(deviceId, (short) 0);
		}
	// ===============================================
	// 保護関数群
	// ===============================================
	public void setDataStatus(Long deviceId, Short status)
		{
		Ph2DevicesEnyity entity = this.ph2DevicesMapper.selectByPrimaryKey(deviceId);
		entity.setDataStatus(status);
		entity.setDataStatusDate(new Date());
		this.ph2DevicesMapper.updateByPrimaryKey(entity);
		}
	}
