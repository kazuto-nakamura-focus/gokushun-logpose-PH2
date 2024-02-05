package com.logpose.ph2.api.bulk.vo;

import java.util.Date;
import java.util.List;

import com.logpose.ph2.api.dao.db.entity.Ph2DevicesEnyity;
import com.logpose.ph2.api.dao.db.mappers.Ph2RelBaseDataMapper;
import com.logpose.ph2.api.dao.db.mappers.joined.Ph2JoinedMapper;
import com.logpose.ph2.api.dto.SensorDataDTO;

import lombok.Getter;

public class LoadCoordinator
	{
	// ===============================================
	// クラスメンバー
	// ===============================================
	private Ph2RelBaseDataMapper ph2RelBaseDataMapper;
	@Getter
	private Ph2DevicesEnyity device;
	@Getter
	private boolean isAll = false;
	@Getter
	private Date lastHadledDate = null;
	@Getter
	private List<SensorDataDTO> sensors;

	// ===============================================
	// コンストラクタ
	// ===============================================
	public LoadCoordinator(boolean isAll, Ph2RelBaseDataMapper ph2RelBaseDataMapper)
		{
		this.isAll = isAll;
		this.ph2RelBaseDataMapper = ph2RelBaseDataMapper;
		}

	// ===============================================
	// 公開関数群
	// ===============================================
	// --------------------------------------------------
	/**
	 * デバイスを設定する
	 * @param device Ph2DevicesEnyity
	 */
	// --------------------------------------------------
	public void setDevice(Ph2DevicesEnyity device)
		{
		this.device = device;
		}
	// --------------------------------------------------
	/**
	 * 最後にデータがロードされた時刻を設定する
	 */
	// --------------------------------------------------
	public void setInitialStartDate()
		{
		this.lastHadledDate = this.ph2RelBaseDataMapper.selectLatest(device.getId());
		}

	// --------------------------------------------------
	/**
	 * センサーデータを設定する
	 * @param ph2JoinedMapper マッパー
	 */
	// --------------------------------------------------
	public void setSensors(Ph2JoinedMapper ph2JoinedMapper)
		{
		this.sensors = ph2JoinedMapper.getSensorData(device.getId());
		}

	// --------------------------------------------------
	/**
	 * ロード可能かどうかを返却する
	 */
	// --------------------------------------------------
	public boolean isLoadable()
		{
		return (null != this.device) && (this.sensors.size() > 0);
		}

	// --------------------------------------------------
	/**
	 * デバイスIDを取得する。
	 */
	// --------------------------------------------------
	public Long getDeviceId()
		{
		return this.device.getId();
		}

	// --------------------------------------------------
	/**
	 * ベースデータの初期日を取得する。
	 * @return ベースデータの初期日
	 */
	// --------------------------------------------------
	public Date getOldestBaseDate()
		{
		return this.ph2RelBaseDataMapper.selectOldDate(getDeviceId());
		}

	// --------------------------------------------------
	/**
	 * ベースデータの最終更新日を取得する。
	 * @return ベースデータの最終更新日
	 */
	// --------------------------------------------------
	public Date getLatestBaseDate()
		{
		return this.ph2RelBaseDataMapper.selectLatest(getDeviceId());
		}
	}
