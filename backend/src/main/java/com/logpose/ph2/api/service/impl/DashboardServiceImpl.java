package com.logpose.ph2.api.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.logpose.ph2.api.dao.db.entity.Ph2DashBoardDisplayEntity;
import com.logpose.ph2.api.domain.DashboardDomain;
import com.logpose.ph2.api.dto.dashboard.DashBoardSensorsDTO;
import com.logpose.ph2.api.dto.dashboard.DashboardSet;
import com.logpose.ph2.api.dto.dashboard.DashboardTarget;
import com.logpose.ph2.api.service.DashboardService;

/**
 * グラフページに対応するサービスの集まり
 *
 */
@Service
public class DashboardServiceImpl implements DashboardService
	{
	// ===============================================
	// クラスメンバー
	// ===============================================
	@Autowired
	private DashboardDomain dashboardDomain;
	// ===============================================
	// 公開関数
	// ===============================================
	// --------------------------------------------------
	/**
	 * 設定対象となるデバイスリストを取得する
	 */
	// -------------------------------------------------
	@Override
	public List<DashboardTarget> getFieldData()
		{
		return this.dashboardDomain.getFieldData();
		}
	// --------------------------------------------------
	/**
	 * 指定されたデバイスデータのセンサー情報を取得する
	 * @param deviceId
	 */
	// --------------------------------------------------
	@Override
	public List<DashBoardSensorsDTO> getSensorList(Long deviceId)
		{
		return this.dashboardDomain.getSensorList(deviceId);
		}
	// --------------------------------------------------
	/**
	 * デバイス情報を更新する
	 * @param dto
	 */
	// --------------------------------------------------
	@Override
	public void updateDisplay(Ph2DashBoardDisplayEntity dto)
		{
		this.dashboardDomain.updateDisplay(dto);
		}
	// --------------------------------------------------
	/**
	 * センサー情報を更新する
	 * @param dto
	 */
	// --------------------------------------------------
	@Override
	public void updateSettings(DashboardSet dto)
		{
		this.dashboardDomain.updateSettings(dto);
		}
	}
