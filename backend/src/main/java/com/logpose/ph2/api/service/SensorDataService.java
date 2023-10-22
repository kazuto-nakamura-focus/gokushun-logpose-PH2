package com.logpose.ph2.api.service;

import java.text.ParseException;
import java.util.Date;
import java.util.List;

import com.logpose.ph2.api.dao.db.entity.joined.SensorItemDTO;
import com.logpose.ph2.api.dto.sensorData.SenseorDataDTO;

/**
 * グラフページに対応するサービスの集まり
 *
 */
public interface SensorDataService
	{
	// ===============================================
	// 公開関数
	// ===============================================
	// --------------------------------------------------
	/**
	 * デバイスに属するセンサーの一覧を取得する。
	 * コンテンツ名・センサー名とIDを返す。
	 * 	
	 * @param deviceId-デバイスID
	 * @return List<SensorItemDTO>
	 */
	// --------------------------------------------------
	public List<SensorItemDTO> getSensorItemsByDevice(Long deviceId);
	// --------------------------------------------------
	/**
	 * ある期間内のセンサーのデータを返す。
	 * 	
	 * @param sensorId - センサーID
	 * @param startDate - 取得期間の開始日
	 * @paraｍ endDate - 取得期間の終了日
	 * @param interval - 取得間隔(分)
	 * @return SenseorDataDTO
	 * @throws ParseException 
	 */
	// --------------------------------------------------
	SenseorDataDTO getSensorGraphDataByInterval(Long sensorId, Date startDate, Date endDate, Long interval)
			throws ParseException;
	}
