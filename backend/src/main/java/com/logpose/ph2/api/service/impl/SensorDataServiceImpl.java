package com.logpose.ph2.api.service.impl;

import java.text.ParseException;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.logpose.ph2.api.dao.db.entity.joined.SensorItemDTO;
import com.logpose.ph2.api.domain.SensorDataDomain;
import com.logpose.ph2.api.dto.sensorData.SenseorDataDTO;
import com.logpose.ph2.api.service.SensorDataService;

/**
 * グラフページに対応するサービスの集まり
 *
 */
@Service
public class SensorDataServiceImpl implements SensorDataService
	{
	// ===============================================
	// クラスメンバー
	// ===============================================
	@Autowired
	private SensorDataDomain sensorDomain;
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
	@Override
	public List<SensorItemDTO> getSensorItemsByDevice(Long deviceId)
		{
		return this.sensorDomain.getSensorItemsByDevice(deviceId);
		}
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
	@Override
	public SenseorDataDTO getSensorGraphDataByInterval(Long sensorId, Date startDate, Date endDate, Long interval) throws ParseException
		{
		return this.sensorDomain.getSensorGraphDataByInterval(sensorId, startDate, endDate, interval);
		}
	}
