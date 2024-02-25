package com.logpose.ph2.api.service;

import java.util.List;

import com.logpose.ph2.api.dao.db.entity.Ph2DashBoardDisplayEntity;
import com.logpose.ph2.api.dto.dashboard.DashBoardSensorsDTO;
import com.logpose.ph2.api.dto.dashboard.DashboardSensorMenuDTO;
import com.logpose.ph2.api.dto.dashboard.DashboardTarget;

/**
 * ダッシュボードに関する参照・更新サービス
 *
 */
public interface DashboardService
	{
	// ===============================================
	// 公開関数群
	// ===============================================
	// --------------------------------------------------
	/**
	 * 設定対象となるデバイスリストを取得する
	 * @return 設定対象となるデバイスリスト
	 */
	// -------------------------------------------------
	public List<DashboardTarget> getFieldData();

	// --------------------------------------------------
	/**
	 * 指定されたデバイスデータのセンサー情報のリストを取得する
	 * @param deviceId デバイスID
	 * @param 指定されたデバイスデータのセンサー情報のリスト 
	 */
	// --------------------------------------------------
	public DashboardSensorMenuDTO getSensorList(Long deviceId);

	// --------------------------------------------------
	/**
	 * デバイス情報を更新する
	 * @param DashboardSet
	 */
	// --------------------------------------------------
	void updateDisplay(Ph2DashBoardDisplayEntity DashboardSet);

	// --------------------------------------------------
	/**
	 * センサー情報を更新する
	 * @param DashboardSet
	 */
	// --------------------------------------------------
	public void updateSettings(DashBoardSensorsDTO dashBoardSensors);
	}
