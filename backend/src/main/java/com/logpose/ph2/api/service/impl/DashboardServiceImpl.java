package com.logpose.ph2.api.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.logpose.ph2.api.dao.db.entity.Ph2DashBoardDisplayEntity;
import com.logpose.ph2.api.dao.db.entity.Ph2DashBoardSensorsEntity;
import com.logpose.ph2.api.dao.db.entity.Ph2SensorsEntity;
import com.logpose.ph2.api.dao.db.entity.Ph2SensorsEntityExample;
import com.logpose.ph2.api.dao.db.mappers.Ph2SensorsMapper;
import com.logpose.ph2.api.domain.DashboardDomain;
import com.logpose.ph2.api.dto.dashboard.DashBoardSensorsDTO;
import com.logpose.ph2.api.dto.dashboard.DashboardSensorMenuDTO;
import com.logpose.ph2.api.dto.dashboard.DashboardTarget;
import com.logpose.ph2.api.dto.element.Label;
import com.logpose.ph2.api.service.DashboardService;

/**
 * ダッシュボードに関する参照・更新サービス
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
	@Autowired
	private Ph2SensorsMapper ph2SensorsMapper;
	// ===============================================
	// 公開関数群
	// ===============================================
	// --------------------------------------------------
	/**
	 * 設定対象となるデバイスリストを取得する
	 * @return 設定対象となるデバイスリスト
	 */
	// -------------------------------------------------
	@Override
	@Transactional(readOnly = true)
	public List<DashboardTarget> getFieldData()
		{
		return this.dashboardDomain.getFieldData();
		}
	// --------------------------------------------------
	/**
	 * 指定されたデバイスデータのセンサー情報のリストを取得する
	 * @param deviceId デバイスID
	 * @return 指定されたデバイスデータのセンサー情報のリスト 
	 */
	// --------------------------------------------------
	@Override
	@Transactional(readOnly = true)
	public DashboardSensorMenuDTO getSensorList(Long deviceId)
		{
		DashboardSensorMenuDTO result = new DashboardSensorMenuDTO();
		
// * メニューの対象項目をコンテンツID順に取得する。
		Ph2SensorsEntityExample exm = new Ph2SensorsEntityExample();
		exm.createCriteria().andDeviceIdEqualTo(deviceId);
		exm.setOrderByClause("sensor_content_id");
		List<Ph2SensorsEntity> sensors = this.ph2SensorsMapper.selectByExample(exm);
// * メニューの作成
		List<Label> menu = new ArrayList<>();
		for(Ph2SensorsEntity sensor : sensors)
			{
			Label label = new Label();
			label.setId(sensor.getId());
			label.setName(sensor.getName());
			menu.add(label);
			}
		result.setMenu(menu);
// * センサーの表示順序を得る
		List<Ph2DashBoardSensorsEntity> displayOrders =//
				this.dashboardDomain.getSensorList(deviceId, sensors);
		result.setDisplays(displayOrders);
		return result;
		}
	// --------------------------------------------------
	/**
	 * デバイス情報を更新する
	 * @param dashBoardDisplayEntity 追加するデバイス情報
	 */
	// --------------------------------------------------
	@Override
	@Transactional(rollbackFor = Exception.class)
	public void updateDisplay(Ph2DashBoardDisplayEntity dashBoardDisplayEntity)
		{
		this.dashboardDomain.updateDisplay(dashBoardDisplayEntity);
		}
	// --------------------------------------------------
	/**
	 * センサー情報を更新する
	 * @param dashboardSet 追加するセンサー情報
	 */
	// --------------------------------------------------
	@Override
	@Transactional(rollbackFor = Exception.class)
	public void updateSettings(DashBoardSensorsDTO dashBoardSensors)
		{
		this.dashboardDomain.updateSettings(dashBoardSensors);
		}
	}
