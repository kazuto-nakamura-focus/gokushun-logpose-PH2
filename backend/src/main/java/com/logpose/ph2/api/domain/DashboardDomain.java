package com.logpose.ph2.api.domain;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.logpose.ph2.api.dao.db.entity.Ph2DashBoardDisplayEntity;
import com.logpose.ph2.api.dao.db.entity.Ph2DashBoardSensorsEntity;
import com.logpose.ph2.api.dao.db.entity.Ph2DashBoardSensorsEntityExample;
import com.logpose.ph2.api.dao.db.entity.Ph2FieldsEntity;
import com.logpose.ph2.api.dao.db.entity.Ph2FieldsEntityExample;
import com.logpose.ph2.api.dao.db.mappers.Ph2DashBoardDisplayMapper;
import com.logpose.ph2.api.dao.db.mappers.Ph2DashBoardSensorsMapper;
import com.logpose.ph2.api.dao.db.mappers.Ph2FieldsMapper;
import com.logpose.ph2.api.dao.db.mappers.joined.DashboardDomainMapper;
import com.logpose.ph2.api.dto.dashboard.DashBoardDevicesDTO;
import com.logpose.ph2.api.dto.dashboard.DashBoardSensorsDTO;
import com.logpose.ph2.api.dto.dashboard.DashboardSet;
import com.logpose.ph2.api.dto.dashboard.DashboardTarget;
import com.logpose.ph2.api.dto.element.Label;

@Component
public class DashboardDomain
	{
	// ===============================================
	// クラスメンバー
	// ===============================================
	@Autowired
	private DashboardDomainMapper dashboardDomainMapper;
	@Autowired
	private Ph2FieldsMapper ph2FieldsMapper;
	@Autowired
	private Ph2DashBoardDisplayMapper ph2DashBoardDisplayMapper;
	@Autowired
	private Ph2DashBoardSensorsMapper ph2DashBoardSensorsMapper;
	
	// ===============================================
	// パブリック関数
	// ===============================================
	// --------------------------------------------------
	/**
	 * 設定対象となるデバイスリストを取得する
	 */
	// --------------------------------------------------
	public DashboardTarget getFieldData()
		{
		DashboardTarget result = new DashboardTarget();
		// * 圃場リストの取得
		Ph2FieldsEntityExample exm = new Ph2FieldsEntityExample();
		List<Ph2FieldsEntity> fields = this.ph2FieldsMapper.selectByExample(exm);
		// * 圃場データの設定
		for (final Ph2FieldsEntity item : fields)
			{
			Label as_field = new Label();
			as_field.setId(item.getId());
			as_field.setName(item.getName());
			result.getFields().add(as_field);
			}
		// * デバイスデータの設定
		List<DashBoardDevicesDTO> devices = this.dashboardDomainMapper.selectDisplayData();
		result.setDevices(devices);
		return result;
		}

	// --------------------------------------------------
	/**
	 * 指定されたデバイスデータのセンサー情報を取得する
	 * @param deviceId
	 */
	// --------------------------------------------------
	public List<DashBoardSensorsDTO> getSensorList(Long deviceId)
		{
		return this.dashboardDomainMapper.selectSensorSettings(deviceId);
		}
	// --------------------------------------------------
	/**
	 * デバイスの表示・非表示を設定する
	 * @param dto
	 */
	// --------------------------------------------------
	public void updateDisplay(Ph2DashBoardDisplayEntity dto)
		{
		Ph2DashBoardDisplayEntity dashboardDisplay = new Ph2DashBoardDisplayEntity();
		dashboardDisplay.setDeviceId(dto.getDeviceId());
		dashboardDisplay.setIsDisplay(dto.getIsDisplay());
		this.ph2DashBoardDisplayMapper.updateByPrimaryKey(dashboardDisplay);
		}
	
	// --------------------------------------------------
	/**
	 * センサー情報を更新する
	 * @param dto
	 */
	// --------------------------------------------------
	public void updateSettings(DashboardSet dto)
		{
		Ph2DashBoardSensorsEntityExample exm = new Ph2DashBoardSensorsEntityExample();
		exm.createCriteria().andDeviceIdEqualTo(dto.getDeviceId());
		this.ph2DashBoardSensorsMapper.deleteByExample(exm);
		
		for(final DashBoardSensorsDTO item : dto.getDevices())
			{
			Ph2DashBoardSensorsEntity entity = new Ph2DashBoardSensorsEntity();
			entity.setDeviceId(item.getDeviceId());
			entity.setDisplayNo(item.getDisplayNo());
			entity.setSensorId(item.getSensorId());
			this.ph2DashBoardSensorsMapper.insert(entity);
			}
		}
	}
