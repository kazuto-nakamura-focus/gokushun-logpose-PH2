package com.logpose.ph2.api.service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.logpose.ph2.api.bulk.domain.DeviceStatusDomain;
import com.logpose.ph2.api.dao.db.entity.Ph2DevicesEntity;
import com.logpose.ph2.api.dao.db.entity.Ph2DevicesEntityExample;
import com.logpose.ph2.api.dao.db.entity.Ph2FieldsEntity;
import com.logpose.ph2.api.dao.db.entity.Ph2FieldsEntityExample;
import com.logpose.ph2.api.dao.db.entity.Ph2WeatherDailyMasterEntityExample;
import com.logpose.ph2.api.dao.db.mappers.Ph2DevicesMapper;
import com.logpose.ph2.api.dao.db.mappers.Ph2FieldsMapper;
import com.logpose.ph2.api.dao.db.mappers.Ph2RelFieldDeviceMapper;
import com.logpose.ph2.api.dao.db.mappers.Ph2WeatherDailyMasterMapper;
import com.logpose.ph2.api.dao.db.mappers.joined.Ph2FieldDeviceJoinMapper;
import com.logpose.ph2.api.domain.DeviceDomain;
import com.logpose.ph2.api.dto.FieldDetailDTO;
import com.logpose.ph2.api.dto.FieldInfoDTO;
import com.logpose.ph2.api.dto.element.DeviceInfo;

/**
 * 圃場に関する参照・更新サービス
 *
 */
@Service
public class FIeldService
	{
	// ===============================================
	// クラスメンバー
	// ===============================================
	@Autowired
	private Ph2FieldsMapper ph2FieldsMapper;
	@Autowired
	private Ph2DevicesMapper ph2DevicesMapper;
	@Autowired
	private Ph2FieldDeviceJoinMapper ph2FieldDeviceSetJoinMapper;
	@Autowired
	private DeviceDomain deviceDomain;
	@Autowired
	private DeviceStatusDomain deviceStatusDomain;
	@Autowired
	private Ph2RelFieldDeviceMapper ph2RelFieldDeviceMapper;
	@Autowired
	private Ph2WeatherDailyMasterMapper ph2WeatherDailyMasterMapper;

	// ===============================================
	// パブリック関数
	// ===============================================
	// ###############################################
	/**
	 * 圃場一覧取得
	 *
	 * @return List<FieldInfoDTO>
	 */
	// ###############################################
	@SuppressWarnings("deprecation")
	public List<FieldInfoDTO> list()
		{
		List<FieldInfoDTO> result = new ArrayList<>();
		Ph2FieldsEntityExample field = new Ph2FieldsEntityExample();
		field.setOrderByClause("name asc");
		List<Ph2FieldsEntity> fieldRecords = this.ph2FieldsMapper.selectByExample(field);
		for (Ph2FieldsEntity item : fieldRecords)
			{
			FieldInfoDTO info = new FieldInfoDTO();
			info.setContructor(item.getClient());
			info.setId(item.getId());
			info.setName(item.getName());
			info.setLocation(item.getLocation());
			info.setLatitude(item.getLatitude());
			info.setRegisteredDate(item.getCreatedAt().toLocaleString());
			result.add(info);
			}
		return result;
		}

	// ###############################################
	/**
	 * 圃場削除
	 *
	 * @param fieldId 圃場ID
	 */
	// ###############################################
	public void delete(Long fieldId)
		{
		this.ph2FieldsMapper.deleteByPrimaryKey(fieldId);
		this.deviceDomain.deleteByFieldId(fieldId);
		}

	// ###############################################
	/**
	 * 圃場情報詳細取得
	 *
	 * @param fieldId 圃場ID
	 * @return FieldDetailDTO
	 */
	// ###############################################
	public FieldDetailDTO getInfo(Long fieldId)
		{
		// * 圃場情報の取得
		Ph2FieldsEntity field = this.ph2FieldsMapper.selectByPrimaryKey(fieldId);
		// * デバイス情報の取得
		List<DeviceInfo> devices = this.ph2FieldDeviceSetJoinMapper.selectDeviceInfoByField(fieldId);
		// * 返却データの作成
		FieldDetailDTO dto = new FieldDetailDTO();
		dto.setContructor(field.getClient());
		dto.setDeviceList(devices);
		dto.setId(field.getId());
		dto.setLocation(field.getLocation());
		dto.setName(field.getName());
		dto.setLatitude(field.getLatitude());
		dto.setLongitude(field.getLongitude());
		dto.setRegisteredDate(field.getCreatedAt().toLocaleString());
		return dto;
		}

	// ###############################################
	/**
	 * 圃場情報追加
	 *
	 * @param dto FieldInfoDTO
	 */
	// ###############################################
	public Long addInfo(FieldInfoDTO dto)
		{
		// * 圃場情報の作成
		Ph2FieldsEntity field = new Ph2FieldsEntity();
		field.setClient(dto.getContructor());
		field.setLatitude(dto.getLatitude());
		field.setLocation(dto.getLocation());
		field.setLongitude(dto.getLongitude());
		field.setName(dto.getName());
		field.setCommenceDate(dto.getContructor());
		field.setCreatedAt(new Timestamp(System.currentTimeMillis()));
		field.setUpdatedAt(new Timestamp(System.currentTimeMillis()));

		return this.ph2FieldsMapper.insert(field);
		}

	// ###############################################
	/**
	 * 圃場情報更新
	 *
	 * @param dto FieldInfoDTO
	 */
	// ###############################################
	@Transactional(rollbackFor = Exception.class)
	public void updateInfo(FieldInfoDTO dto)
		{
// * 圃場情報の取得
		Ph2FieldsEntity field = this.ph2FieldsMapper.selectByPrimaryKey(dto.getId());
// * デバイスのロックリスト
		List<Long> locks = null;
		List<Ph2DevicesEntity> devices = null;
// * 緯度経度に変更がある場合
		if ((dto.getLatitude().doubleValue() != field.getLatitude().doubleValue()) ||
				(dto.getLongitude().doubleValue() != field.getLongitude().doubleValue()))
			{
// * 圃場に付属するデバイスリストを得る
			Ph2DevicesEntityExample exm = new Ph2DevicesEntityExample();
			exm.createCriteria().andFieldIdEqualTo(dto.getId());
			devices = this.ph2DevicesMapper.selectByExample(exm);
// * デバイスにロックを実施する
			locks = this.deviceStatusDomain.lockDevices(devices);
			}
		try
			{
// * ロックがある場合（緯度経度に変更がある場合）、WheatherMasterを削除し、全データのアップロードを指定する
			if (null != locks)
				{
				for (Ph2DevicesEntity device : devices)
					{
					Ph2WeatherDailyMasterEntityExample wexm = new Ph2WeatherDailyMasterEntityExample();
					wexm.createCriteria().andDeviceIdEqualTo(device.getId());
					this.ph2WeatherDailyMasterMapper.deleteByExample(wexm);
					this.deviceStatusDomain.setAllStatusToAllLoaded();
					}
				}
			// * 圃場情報の更新
			field.setClient(dto.getContructor());
			field.setLatitude(dto.getLatitude());
			field.setLocation(dto.getLocation());
			field.setLongitude(dto.getLongitude());
			field.setName(dto.getName());
			field.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
			this.ph2FieldsMapper.updateByPrimaryKey(field);
			}
		catch (Exception e)
			{
			throw e;
			}
		finally
			{
			if (null != locks)
				this.deviceStatusDomain.unLockDevices(locks);
			}
		}

	}
