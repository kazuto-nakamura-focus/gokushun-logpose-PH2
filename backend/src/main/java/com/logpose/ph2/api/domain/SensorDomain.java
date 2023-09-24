package com.logpose.ph2.api.domain;

import java.sql.Timestamp;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.logpose.ph2.api.dao.db.entity.Ph2SensorsEntity;
import com.logpose.ph2.api.dao.db.entity.Ph2SensorsEntityExample;
import com.logpose.ph2.api.dao.db.mappers.Ph2SensorsMapper;
import com.logpose.ph2.api.dao.db.mappers.joined.SensorJoinMapper;
import com.logpose.ph2.api.dto.senseor.SensorUnitReference;
import com.logpose.ph2.api.dto.senseor.SensorUnitUpdate;


@Component
public class SensorDomain
	{
	// ===============================================
	// クラスメンバー
	// ===============================================
	@Autowired
	private Ph2SensorsMapper ph2SensorsMapper;
	@Autowired
	private SensorJoinMapper sensorJoinMapper;
	// ###############################################
	/**
	 * センサ―削除
	 *
	 *@param deviceId デバイスID
	 */
	// ###############################################
	public void delete(Long deviceId)
		{
		Ph2SensorsEntityExample exm  = new Ph2SensorsEntityExample();
		exm.createCriteria().andDeviceIdEqualTo(deviceId);
		this.ph2SensorsMapper.deleteByExample(exm);
		}
	// ###############################################
	/**
	 * センサ―一覧取得
	 *
	 *@param deviceId デバイスID
	 */
	// ###############################################
	public List<SensorUnitReference> getSensors(Long deviceId)
		{
		return this.sensorJoinMapper.selectDetail(deviceId);
		}
	/** --------------------------------------------------
	 * センサー情報の追加・更新・削除を行う
	 *
	 * @return DeviceMastersDTO
	 ------------------------------------------------------ */
	public void add(Long deviceId, List<SensorUnitUpdate> sensorList)
		{
		// * デバイスIDを基にセンサー情報のリストを取得する
		Ph2SensorsEntityExample exm = new Ph2SensorsEntityExample();
		exm.createCriteria().andDeviceIdEqualTo(deviceId);
		List<Ph2SensorsEntity> entities = this.ph2SensorsMapper.selectByExample(exm);
		// * 取得したエンティティの更新日付をNULLにする
		for(Ph2SensorsEntity old : entities)
			{
			old.setUpdatedAt(null);
			}
		for(SensorUnitUpdate updateInfo : sensorList)
			{
			// * 新規に追加する場合、DTOにセンサーIDは無い
			if(null == updateInfo.getId())
				{
				Ph2SensorsEntity sensor = new Ph2SensorsEntity();
				this.setEntity(sensor, updateInfo);
				sensor.setDeviceId(deviceId);
				sensor.setCreatedAt(new Timestamp(System.currentTimeMillis()));
				sensor.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
				this.ph2SensorsMapper.insert(sensor);
				continue;
				}
			// * 更新の場合
			for(Ph2SensorsEntity old : entities)
				{
				// * 同一のセンサーIDの場合、更新する
				if(updateInfo.getId().longValue()==old.getId().longValue())
					{
					this.setEntity(old, updateInfo);
					old.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
					this.ph2SensorsMapper.updateByPrimaryKey(old);
					break;
					}
				}
			}
		// * 更新されなかったデータを削除する
		for(Ph2SensorsEntity old : entities)
			{
			if(null == old.getUpdatedAt())
				{
				this.ph2SensorsMapper.deleteByPrimaryKey(old.getId());
				}
			}
		}
	/** --------------------------------------------------
	 * センサー情報の設定を行う
	 *
	 * @return DeviceMastersDTO
	 ------------------------------------------------------ */
	private void setEntity(Ph2SensorsEntity entity, SensorUnitUpdate dto)
		{
		entity.setName(dto.getName());
		entity.setSensorModelId(dto.getModelId());
		entity.setChannel(dto.getChannel());
		entity.setSensorContentId(dto.getDisplayId());
		entity.setSensorSizeId(dto.getSizeId());
		entity.setKst(dto.getKst());
		entity.setSm(dto.getStemDiameter());
		// * 現在未使用だがテーブルでは必須のため
		entity.setPlant("");
		}
	}
