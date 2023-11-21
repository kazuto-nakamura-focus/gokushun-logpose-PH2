package com.logpose.ph2.api.domain;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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
import com.logpose.ph2.api.dto.dashboard.DashboardDisplayOrder;
import com.logpose.ph2.api.dto.dashboard.DashboardSet;
import com.logpose.ph2.api.dto.dashboard.DashboardTarget;

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
	public List<DashboardTarget> getFieldData()
		{
		List<DashboardTarget> result = new ArrayList<>();
		// * 圃場リストの取得
		Ph2FieldsEntityExample exm = new Ph2FieldsEntityExample();
		List<Ph2FieldsEntity> fields = this.ph2FieldsMapper.selectByExample(exm);
		
		Map<Long, DashboardTarget> map = new LinkedHashMap<>();
		// * 圃場データの設定
		for (final Ph2FieldsEntity item : fields)
			{
			DashboardTarget as_value = new DashboardTarget();
			as_value.setId(item.getId());
			as_value.setName(item.getName());
			map.put(item.getId(), as_value);
			}
		// * デバイスデータの設定
		List<DashBoardDevicesDTO> devices = this.dashboardDomainMapper.selectDisplayData();
		for (final DashBoardDevicesDTO item : devices)
			{
		// * デバイスデータにIsDislayが未設定ならば設定する。
			if(null == item.getIsDisplay())
				{
				Ph2DashBoardDisplayEntity entity = new Ph2DashBoardDisplayEntity();
				entity.setDeviceId(item.getDeviceId());
				entity.setIsDisplay(true);
				this.ph2DashBoardDisplayMapper.insert(entity);
				item.setIsDisplay(true);
				}
			DashboardTarget as_value = map.get(item.getFieldId());
			as_value.getDevices().add(item);
			}
		map.forEach((key, value) -> result.add(value));
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
		if(0 == this.ph2DashBoardDisplayMapper.updateByPrimaryKey(dto) )
			{
			this.ph2DashBoardDisplayMapper.insert(dto);
			}
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
		
		for(final DashboardDisplayOrder item : dto.getSensors())
			{
			Ph2DashBoardSensorsEntity entity = new Ph2DashBoardSensorsEntity();
			entity.setDeviceId(dto.getDeviceId());
			entity.setDisplayNo(item.getDisplayNo());
			entity.setSensorId(item.getSensorId());
			this.ph2DashBoardSensorsMapper.insert(entity);
			}
		}
	}
