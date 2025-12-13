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

import com.logpose.ph2.api.dao.db.entity.Ph2BatchLogEntity;
import com.logpose.ph2.api.dao.db.entity.Ph2DeviceLogEntity;
import com.logpose.ph2.api.dao.db.entity.Ph2DeviceLogEntityExample;
import com.logpose.ph2.api.dao.db.entity.Ph2DevicesEntity;
import com.logpose.ph2.api.dao.db.entity.joined.Ph2BatchLogEntityExtendDevices;
import com.logpose.ph2.api.dao.db.mappers.Ph2BatchLogMapper;
import com.logpose.ph2.api.dao.db.mappers.Ph2DeviceLogMapper;

@Component
public class DeviceLogDomain
	{
	// ===============================================
	// クラスメンバー
	// ===============================================
	public static final short TYPE_UPLOAD = 1;
	public static final short TYPE_UPDATE = 2;
	@Autowired
	private Ph2DeviceLogMapper ph2DeviceLogMapper;
	@Autowired
	private Ph2BatchLogMapper ph2BatchLogMapper;

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
		if (null != date)
			{
			// フォーマットを適用してDateオブジェクトを文字列に変換
			return FMT.format(date);
			}
		else
			return sub;

		}

	// --------------------------------------------------
	/**
	 * バッチログの開始
	 * @param deviceId
	 */
	// --------------------------------------------------
	@Transactional(rollbackFor = Exception.class)
	public boolean startBatch(boolean isAll, Long deviceId)
		{
		Ph2BatchLogEntity entity;
		entity = this.ph2BatchLogMapper.selectByPrimaryKey(deviceId);
		if (null == entity)
			{
			entity = new Ph2BatchLogEntity();
			}
// * ログの削除
		Ph2DeviceLogEntityExample exm = new Ph2DeviceLogEntityExample();
		short mode = isAll ? TYPE_UPLOAD : TYPE_UPDATE;
		exm.createCriteria().andDeviceIdEqualTo(deviceId).andModeEqualTo(mode);
		this.ph2DeviceLogMapper.deleteByExample(exm);

		Date now = new Timestamp(System.currentTimeMillis());
		if (isAll)
			{
			entity.setUploadBeginTime(now);
			entity.setUploadEndTime(null);
			}
		else
			{
			entity.setUpdateBeginTime(now);
			entity.setUpdateEndTime(null);
			}
		if (null != entity.getDeviceId())
			{
			this.ph2BatchLogMapper.updateByPrimaryKey(entity);
			}
		else
			{
			entity.setDeviceId(deviceId);
			this.ph2BatchLogMapper.insert(entity);
			}
		return true;
		}

	// --------------------------------------------------
	/**
	 * バッチログの終了
	 * @param deviceId
	 */
	// --------------------------------------------------
	@Transactional(rollbackFor = Exception.class)
	public void endBatch(boolean isAll, Long deviceId)
		{
		Ph2BatchLogEntity entity = this.ph2BatchLogMapper.selectByPrimaryKey(deviceId);
		Date now = new Timestamp(System.currentTimeMillis());
		if (isAll)
			{
			entity.setUploadEndTime(now);
			}
		else
			{
			entity.setUpdateEndTime(now);
			}
		this.ph2BatchLogMapper.updateByPrimaryKey(entity);
		}

	// --------------------------------------------------
	/**
	 * ログを追加する
	 * @param deviceId
	 * @param classObj
	 * @param message
	 */
	// --------------------------------------------------
	public void log(Logger logger, Ph2DevicesEntity device, Class<?> classObj, List<String> message, boolean isAll)
		{
		for(String val : message)
			{
			this.log(logger, device, classObj, val, isAll);
			}
		}

	@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRES_NEW)
	public void log(Logger logger, Ph2DevicesEntity device, Class<?> classObj, String message, boolean isAll)
		{
		Ph2DeviceLogEntity entity = new Ph2DeviceLogEntity();
		entity.setDeviceId(device.getId());
		Timestamp time = new Timestamp(System.currentTimeMillis());
		entity.setTime(time);
		entity.setClassName(classObj.getName());
		entity.setMessage(message);
		entity.setStatus(device.getDataStatus());
		short mode = isAll ? TYPE_UPLOAD : TYPE_UPDATE;
		entity.setMode(mode);
		this.ph2DeviceLogMapper.insert(entity);
		logger.info(device.getId() + ":" + message);
		}

	// --------------------------------------------------
	/**
	 * バッチ状態を取得する
	 * @param deviceId
	 */
	// --------------------------------------------------
	@Transactional(rollbackFor = Exception.class, readOnly = true)
	public List<Ph2BatchLogEntityExtendDevices> getLog()
		{
		return this.ph2BatchLogMapper.selectWithStatus();
		}

	// --------------------------------------------------
	/**
	 * ログを取得する
	 * @param deviceId
	 */
	// --------------------------------------------------
	@Transactional(rollbackFor = Exception.class, readOnly = true)
	public List<Ph2DeviceLogEntity> getBatchStatus(Long deviceId, short type)
		{
		Ph2DeviceLogEntityExample exm = new Ph2DeviceLogEntityExample();
		exm.createCriteria().andDeviceIdEqualTo(deviceId).andModeEqualTo(type);
		exm.setOrderByClause("time asc");
		return this.ph2DeviceLogMapper.selectByExample(exm);
		}

	// --------------------------------------------------
	/**
	 * 更新バッチのタイムアウト判定 update_begin_time が入り、update_end_time が null のまま
	 * 指定時間以上経過している場合 true
	 */
	// --------------------------------------------------
	@Transactional(rollbackFor = Exception.class, readOnly = true)
	public boolean isUpdateBatchTimedOut(Long deviceId, long timeoutMillis)
		{
		Ph2BatchLogEntity entity = this.ph2BatchLogMapper.selectByPrimaryKey(deviceId);
		if (null == entity)
			return false;

		Date begin = entity.getUpdateBeginTime();
		Date end = entity.getUpdateEndTime();

		// 開始していない or すでに終了している ⇒ タイムアウト扱いにはしない
		if (null == begin || null != end)
			return false;

		long diff = System.currentTimeMillis() - begin.getTime();
		return diff >= timeoutMillis;
		}

	// --------------------------------------------------
	/**
	 * 全ロード(UPLOAD)バッチのタイムアウト判定 upload_begin_time が入り、upload_end_time が null のまま
	 * 指定時間以上経過している場合 true
	 */
	// --------------------------------------------------
	@Transactional(rollbackFor = Exception.class, readOnly = true)
	public boolean isUploadBatchTimedOut(Long deviceId, long timeoutMillis)
		{
		Ph2BatchLogEntity entity = this.ph2BatchLogMapper.selectByPrimaryKey(deviceId);
		if (null == entity)
			return false;

		Date begin = entity.getUploadBeginTime();
		Date end = entity.getUploadEndTime();

		if (null == begin || null != end)
			return false;

		long diff = System.currentTimeMillis() - begin.getTime();
		return diff >= timeoutMillis;
		}

	}
