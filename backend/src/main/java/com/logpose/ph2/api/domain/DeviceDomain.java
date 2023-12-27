package com.logpose.ph2.api.domain;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.logpose.ph2.api.dao.db.entity.Ph2DevicesEnyity;
import com.logpose.ph2.api.dao.db.entity.Ph2DevicesEnyityExample;
import com.logpose.ph2.api.dao.db.mappers.Ph2DevicesMapper;
import com.logpose.ph2.api.dao.db.mappers.joined.Ph2FieldDeviceJoinMapper;
import com.logpose.ph2.api.dto.DeviceInfoDTO;
import com.logpose.ph2.api.dto.device.DeviceDetailDTO;
import com.logpose.ph2.api.dto.device.DeviceUpdateDTO;


@Component
public class DeviceDomain
	{
	// ===============================================
	// クラスメンバー
	// ===============================================
	@Autowired
	private Ph2FieldDeviceJoinMapper ph2FieldDeviceJoinMapper;
	@Autowired
	private Ph2DevicesMapper  phDevicesMapper;

	// ###############################################
	/**
	 * デバイス一覧取得
	 *
	 * @return List<DeviceInfoDTO>
	 */
	// ###############################################
	public List<DeviceInfoDTO> getDeviceList()
		{
		return this.ph2FieldDeviceJoinMapper.selectDeviceList();
		}
	// ###############################################
	/**
	 * デバイスの詳細取得
	 *
	 *@param deviceId デバイスID
	 */
	// ###############################################
	public DeviceDetailDTO getDetail(Long deviceId)
		{
		return this.ph2FieldDeviceJoinMapper.selectDeviceDetail(deviceId);
		}
	/** --------------------------------------------------
	 * デバイスの追加
	 *
	 *@param dto DeviceUpdateDTO
	 ------------------------------------------------------ 
	 * @throws ParseException */
	public Long add(DeviceUpdateDTO dto) throws ParseException
		{
		Ph2DevicesEnyity target = new Ph2DevicesEnyity();
		this.setEntity(target, dto);
		target.setCreatedAt(new Timestamp(System.currentTimeMillis()));
		Long id = this.phDevicesMapper.insert(target);
		return id;
		}
	/** --------------------------------------------------
	 * デバイスの更新
	 *
	 *@param dto DeviceUpdateDTO
	 ------------------------------------------------------ 
	 * @throws ParseException */
	public void update(DeviceUpdateDTO dto) throws ParseException
		{
		//* 対象となるデバイスをテーブルから取得する
		Ph2DevicesEnyity target = this.phDevicesMapper.selectByPrimaryKey(dto.getId());
		//* 統計開始日またはタイムゾーンが変更された
		this.setEntity(target, dto);
		this.phDevicesMapper.updateByPrimaryKey(target);
		}
	
	// ###############################################
	/**
	 * デバイスの削除
	 *
	 *@param deviceId デバイスID
	 */
	// ###############################################
	public void deleteByDeviceId(Long deviceId)
		{
		//* デバイスの削除
		this.phDevicesMapper.deleteByPrimaryKey(deviceId);
		}
	// ###############################################
	/**
	 * デバイスの削除
	 *
	 *@param fieldId フィールドID
	 */
	// ###############################################]
	public void deleteByFieldId(Long fieldId)
		{
		//* デバイスの削除
		Ph2DevicesEnyityExample devExm = new Ph2DevicesEnyityExample();
		devExm.createCriteria().andFieldIdEqualTo(fieldId);
		this.phDevicesMapper.deleteByExample(devExm);
		}
	
	/** --------------------------------------------------
	 * エンティティにDTOのデータを設定する
	 *
	 *@param target Ph2DevicesEnyity
	 *@param dto DeviceUpdateDTO
	 ------------------------------------------------------ 
	 * @throws ParseException */
	private void setEntity(Ph2DevicesEnyity target, DeviceUpdateDTO dto) throws ParseException
		{
		//* 基準日をDate型に変換する
		Calendar cal = Calendar.getInstance();
		// * 2020の部分は任意
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
		Date baseDate = dateFormat.parse("2020/"+dto.getBaseDateShort());
		
		target.setName(dto.getName());	// デバイス名
		target.setFieldId(dto.getFieldId()); // 連携する圃場のID
		target.setSigfoxDeviceId(dto.getSigFoxDeviceId());
		target.setBaseDate(baseDate);
		target.setBrand(dto.getBrand());
		target.setTz(dto.getTimeZone());
		target.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
		}
	}
