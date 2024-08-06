package com.logpose.ph2.api.bulk.domain;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

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

    private SimpleDateFormat FMT = new SimpleDateFormat("yyyy年MM月dd日 HH時mm分ss秒");

	// ===============================================
	// 公開関数群
	// ===============================================
	// --------------------------------------------------
	/**
	 * 日付を変換する
	 * @param Date
	 * @param 代替文
	 */
	// --------------------------------------------------
	public String date(Date date, String sub)
		{
		if(null != date)
			{
        // フォーマットを適用してDateオブジェクトを文字列に変換
			return FMT.format(date);
			}
		else return sub;

		}
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
	
	// --------------------------------------------------
	/**
	 * ログを取得する
	 * @param deviceId
	 */
	// --------------------------------------------------
	@Transactional(rollbackFor = Exception.class, readOnly=true)
	public List<Ph2DeviceLogEntity> getLog(Long deviceId)
		{
		Ph2DeviceLogEntityExample exm = new Ph2DeviceLogEntityExample();
		exm.createCriteria().andDeviceIdEqualTo(deviceId);
		exm.setOrderByClause("time asc");
		return this.ph2DeviceLogMapper.selectByExample(exm);
		}
	}
