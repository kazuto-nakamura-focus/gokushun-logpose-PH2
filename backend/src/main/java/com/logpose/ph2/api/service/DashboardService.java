package com.logpose.ph2.api.service;

import java.util.List;

import com.logpose.ph2.api.dao.db.entity.Ph2DashBoardDisplayEntity;
import com.logpose.ph2.api.dto.dashboard.DashBoardSensorsDTO;
import com.logpose.ph2.api.dto.dashboard.DashboardSet;
import com.logpose.ph2.api.dto.dashboard.DashboardTarget;

/**
 * ダッシュボードに関する参照・更新サービス
 *
 */
public interface DashboardService
	{
	// ===============================================
	// パブリック関数
	// ===============================================
	// --------------------------------------------------
	/**
	 * 設定対象となるデバイスリストを取得する
	 */
	// -------------------------------------------------
	public DashboardTarget getFieldData();
	
	// --------------------------------------------------
	/**
	 * 指定されたデバイスデータのセンサー情報を取得する
	 * @param deviceId
	 */
	// --------------------------------------------------
	public List<DashBoardSensorsDTO> getSensorList(Long deviceId);

	// --------------------------------------------------
	/**
	 * センサー情報を更新する
	 * @param dto
	 */
	// --------------------------------------------------
	public void updateSettings(DashboardSet dto);

	// --------------------------------------------------
	/**
	 * デバイス情報を更新する
	 * @param dto
	 */
	// --------------------------------------------------
	void updateDisplay(Ph2DashBoardDisplayEntity dto);
	}
