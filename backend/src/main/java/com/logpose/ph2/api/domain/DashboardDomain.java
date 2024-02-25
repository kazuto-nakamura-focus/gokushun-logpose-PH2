package com.logpose.ph2.api.domain;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.logpose.ph2.api.dao.db.entity.Ph2DashBoardDisplayEntity;
import com.logpose.ph2.api.dao.db.entity.Ph2DashBoardSensorsEntity;
import com.logpose.ph2.api.dao.db.entity.Ph2FieldsEntity;
import com.logpose.ph2.api.dao.db.entity.Ph2FieldsEntityExample;
import com.logpose.ph2.api.dao.db.entity.Ph2SensorsEntity;
import com.logpose.ph2.api.dao.db.mappers.Ph2DashBoardDisplayMapper;
import com.logpose.ph2.api.dao.db.mappers.Ph2DashBoardSensorsMapper;
import com.logpose.ph2.api.dao.db.mappers.Ph2FieldsMapper;
import com.logpose.ph2.api.dao.db.mappers.joined.DashboardDomainMapper;
import com.logpose.ph2.api.dto.dashboard.DashBoardDevicesDTO;
import com.logpose.ph2.api.dto.dashboard.DashBoardSensorsDTO;
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
	// パブリック関数群
	// ===============================================
	// --------------------------------------------------
	/**
	 *  設定対象となるデバイスリストを取得する
	 *  @return 設定対象となるデバイスリスト
	 */
	// --------------------------------------------------
	public List<DashboardTarget> getFieldData()
		{
		List<DashboardTarget> result = new ArrayList<>();

// * 圃場リストの取得。
		Ph2FieldsEntityExample exm = new Ph2FieldsEntityExample();
		List<Ph2FieldsEntity> fields = this.ph2FieldsMapper.selectByExample(exm);

// * 各圃場に対応するダッシュボードターゲットのマップを作成する。
		Map<Long, DashboardTarget> map = new LinkedHashMap<>();

// * ダッシュボードターゲットのオブジェクトを各圃場毎に作成し、マップに入れる。
		for (final Ph2FieldsEntity item : fields)
			{
			DashboardTarget as_value = new DashboardTarget();
			as_value.setId(item.getId());
			as_value.setName(item.getName());
			map.put(item.getId(), as_value);
			}

// * ダッシュボードのデバイスデータのリストを取得する。
		List<DashBoardDevicesDTO> devices = this.dashboardDomainMapper.selectDisplayData();

// * 各ダッシュボードのデバイスデータに対して、ダッシュボード表示内容を設定する
		for (final DashBoardDevicesDTO item : devices)
			{
			DashboardTarget as_value = map.get(item.getFieldId());
			as_value.getDevices().add(item);
			}
		map.forEach((key, value) -> result.add(value));
		return result;
		}

	// --------------------------------------------------
	/**
	 * 指定されたデバイスデータのセンサー情報の表示順を取得する。
	 * @param deviceId デバイスID
	 * @@param sensors センサー情報リスト
	 */
	// --------------------------------------------------
	public List<Ph2DashBoardSensorsEntity> getSensorList(Long deviceId, List<Ph2SensorsEntity> sensors)
		{
// * ダッシュボードセンサー表示順テーブルからデータを取得する
		List<Ph2DashBoardSensorsEntity> resultData = this.ph2DashBoardSensorsMapper
				.selectSensorSettingsByDeviceId(deviceId);
// * 未設定の場合、センサー情報のコンテンツIdからセンサー情報の表示順を設定する
		if (resultData.size() == 0)
			{
			for (final Ph2SensorsEntity entity : sensors)
				{
				Ph2DashBoardSensorsEntity item = new Ph2DashBoardSensorsEntity();
				item.setDisplayNo(entity.getSensorContentId());
				item.setSensorId(entity.getId());
				resultData.add(item);
				}
			}
		return resultData;
		}

	// --------------------------------------------------
	/**
	 * デバイスの表示・非表示を設定する
	 * @param dto
	 */
	// --------------------------------------------------
	public void updateDisplay(Ph2DashBoardDisplayEntity dto)
		{
		if (0 == this.ph2DashBoardDisplayMapper.updateByPrimaryKey(dto))
			{
			this.ph2DashBoardDisplayMapper.insert(dto);
			}
		}

	// --------------------------------------------------
	/**
	 * ダッシュボード表示にデバイスを追加する
	 * @param deviceId 追加するデバイスID
	 */
	// --------------------------------------------------
	public void addDevice(Long deviceId)
		{
// * 追加するダッシュボード表示情報の設定
		Ph2DashBoardDisplayEntity entity = new Ph2DashBoardDisplayEntity();
		entity.setDeviceId(deviceId);
		entity.setIsDisplay(true);

// * ダッシュボード表示情報テーブルに追加
		this.ph2DashBoardDisplayMapper.insert(entity);
		}

	// --------------------------------------------------
	/**
	 * センサー表示情報を更新する
	 * @param displays 追加するセンサー表示情報のリスト
	 */
	// --------------------------------------------------
	public void updateSettings(DashBoardSensorsDTO dashBoardSensors)
		{
		this.ph2DashBoardSensorsMapper.deleteByDeviceId(dashBoardSensors.getDeviceId());

		for (final Ph2DashBoardSensorsEntity item : dashBoardSensors.getSensors())
			{
			if (null != item.getSensorId())
				{
				Ph2DashBoardSensorsEntity entity = new Ph2DashBoardSensorsEntity();
				entity.setDisplayNo(item.getDisplayNo());
				entity.setSensorId(item.getSensorId());
				this.ph2DashBoardSensorsMapper.insert(entity);
				}
			}
		}
	}
