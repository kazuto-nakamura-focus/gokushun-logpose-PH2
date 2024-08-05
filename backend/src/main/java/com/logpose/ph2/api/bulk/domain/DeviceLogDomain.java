package com.logpose.ph2.api.bulk.domain;

import java.sql.Timestamp;

import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.logpose.ph2.api.dao.db.entity.Ph2DeviceLogEntity;
import com.logpose.ph2.api.dao.db.entity.Ph2DeviceLogEntityExample;
import com.logpose.ph2.api.dao.db.mappers.Ph2DeviceLogMapper;

@Component
public class DeviceLogDomain
	{
	// ===============================================
	// クラスメンバー
	// ===============================================
	@Autowired
	private Ph2DeviceLogMapper ph2DeviceLogMapper;

	// ===============================================
	// 公開関数群
	// ===============================================
	// --------------------------------------------------
	/**
	 * ログを初期化する
	 * @param deviceId
	 */
	// --------------------------------------------------
	@Transactional(rollbackFor = Exception.class)
	public void cleanUp(Long deviceId)
		{
		Ph2DeviceLogEntityExample exm = new Ph2DeviceLogEntityExample();
		exm.createCriteria().andDeviceIdEqualTo(deviceId);
		
		this.ph2DeviceLogMapper.deleteByExample(exm);
		}

	// --------------------------------------------------
	/**
	 * ログを追加する
	 * @param deviceId
	 * @param classObj
	 * @param message
	 */
	// --------------------------------------------------
	@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRES_NEW)
	public void log(Logger logger, Long deviceId, Class<?> classObj, String message)
		{
		Ph2DeviceLogEntity entity = new Ph2DeviceLogEntity();
		entity.setDeviceId(deviceId);
		Timestamp time = new Timestamp(System.currentTimeMillis());
		entity.setTime(time);
		entity.setClassName(classObj.getName());
		entity.setMessage(message);
		this.ph2DeviceLogMapper.insert(entity);
		logger.info(deviceId.toString() + ":" + message);
		}
	}
