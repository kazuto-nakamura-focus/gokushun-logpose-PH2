package com.logpose.ph2.api.domain;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.logpose.ph2.api.algorythm.DateTimeUtility;
import com.logpose.ph2.api.dao.db.entity.Ph2DevicesEntity;
import com.logpose.ph2.api.dao.db.entity.Ph2DevicesEntityExample;
import com.logpose.ph2.api.dao.db.mappers.Ph2DevicesMapper;
import com.logpose.ph2.api.dao.db.mappers.joined.Ph2FieldDeviceJoinMapper;
import com.logpose.ph2.api.dto.DeviceInfoDTO;
import com.logpose.ph2.api.dto.device.DeviceDetailDTO;
import com.logpose.ph2.api.dto.device.DeviceTermDTO;
import com.logpose.ph2.api.dto.device.DeviceUpdateDTO;

/**
 * デバイス処理を行う
 */

@Component
public class DeviceDomain
	{
	// ===============================================
	// クラスメンバー
	// ===============================================
	@Autowired
	private Ph2FieldDeviceJoinMapper fieldDeviceJoinMapper;
	@Autowired
	private Ph2DevicesMapper devicesMapper;

	// ===============================================
	// パブリック関数群
	// ===============================================
	// --------------------------------------------------
	/**
	 * デバイス一覧取得
	 *
	 * @return List<DeviceInfoDTO>
	 */
	// --------------------------------------------------
	public List<DeviceInfoDTO> getDeviceList()
		{
		return this.fieldDeviceJoinMapper.selectDeviceList();
		}

	// --------------------------------------------------
	/**
	 * デバイスの詳細取得
	 *
	 *@param deviceId デバイスID
	 */
	// --------------------------------------------------
	public DeviceDetailDTO getDetail(Long deviceId)
		{
		return this.fieldDeviceJoinMapper.selectDeviceDetail(deviceId);
		}

	// --------------------------------------------------
	/**
	 * デバイスの追加
	 *
	 *@param dto DeviceUpdateDTO
	 * @throws ParseException
	 */
	// --------------------------------------------------
	public Ph2DevicesEntity add(DeviceUpdateDTO dto) throws ParseException
		{
// * 追加するデバイス・エンティティの作成
		Ph2DevicesEntity target = new Ph2DevicesEntity();
		this.setEntity(target, dto);
		target.setCreatedAt(new Timestamp(System.currentTimeMillis()));
// * DBへの追加とIDの取得
		Long id = this.devicesMapper.insert(target);
		target.setId(id);
		return target;
		}

	// --------------------------------------------------
	/**
	 * デバイスの更新
	 *
	 *@param dto DeviceUpdateDTO
	 *@throws ParseException
	 */
	// --------------------------------------------------
	public void update(DeviceUpdateDTO dto) throws ParseException
		{
// * 対象となるデバイスをテーブルから取得する
		Ph2DevicesEntity target = this.devicesMapper.selectByPrimaryKey(dto.getId());
// * 更新するデバイス・エンティティの作成
		this.setEntity(target, dto);
// * DBへの更新
		this.devicesMapper.updateByPrimaryKey(target);
		}

	// --------------------------------------------------
	/**
	 * デバイスの削除
	 *
	 *@param deviceId デバイスID
	 */
	// --------------------------------------------------
	public void deleteByDeviceId(Long deviceId)
		{
// * デバイスの削除
		this.devicesMapper.deleteByPrimaryKey(deviceId);
// * このデバイスを参照しているデバイスの引継ぎIDをNULLにする
		Ph2DevicesEntityExample devExm = new Ph2DevicesEntityExample();
		devExm.createCriteria().andPreviousDeviceIdEqualTo(deviceId);
		List<Ph2DevicesEntity> list = this.devicesMapper.selectByExample(devExm);
		for(Ph2DevicesEntity device : list)
			{
			device.setPreviousDeviceId(null);
			this.devicesMapper.updateByPrimaryKey(device);
			}
		}

	// --------------------------------------------------
	/**
	 * 圃場の全デバイスの削除
	 *
	 *@param fieldId フィールドID
	 */
	// --------------------------------------------------
	public void deleteByFieldId(Long fieldId)
		{
// *  圃場の全デバイスの削除
		Ph2DevicesEntityExample devExm = new Ph2DevicesEntityExample();
		devExm.createCriteria().andFieldIdEqualTo(fieldId);
		this.devicesMapper.deleteByExample(devExm);
		}
	// --------------------------------------------------
	/**
	 * 参照元デバイスの終了日設定
	 *
	 *@param fieldId フィールドID
	 */
	// --------------------------------------------------	
	public void setPreviousDevice(Ph2DevicesEntity target)
		{
		if(null == target.getPreviousDeviceId()) return;
		Ph2DevicesEntity prev = this.devicesMapper.selectByPrimaryKey(target.getPreviousDeviceId());
		if(null == prev)
			{
			target.setPreviousDeviceId(null);
			return;
			}
		prev.setOpEnd(target.getOpStart());
		this.devicesMapper.updateByPrimaryKey(prev);
		}
	// --------------------------------------------------
	/**
	 * デバイスの指定年度の開始日・終了日を設定する
	 *
	 *@param deviceId
	 *@param year
	 * @throws ParseException 
	 */
	// --------------------------------------------------
	public DeviceTermDTO getTerm(Long deviceId, Short year) throws ParseException
		{
		DeviceTermDTO term = new DeviceTermDTO();
		Ph2DevicesEntity device = this.devicesMapper.selectByPrimaryKey(deviceId);
		Calendar baseDate = Calendar.getInstance();
// * 基準日
		baseDate.setTime(device.getBaseDate());
// * 開始日
		baseDate.set(Calendar.YEAR, year);
		term.setStartDate(DateTimeUtility.getStringFromDate(baseDate.getTime()));
// * 終了日
		baseDate.add(Calendar.YEAR, 1);
		baseDate.add(Calendar.DATE, -1);
		term.setEndDate(DateTimeUtility.getStringFromDate(baseDate.getTime()));
		
		return term;
		}
	
	// ===============================================
	// 保護関数群
	// ===============================================
	/** --------------------------------------------------
	 * エンティティにDTOのデータを設定する
	 *
	 *@param target Ph2DevicesEntity
	 *@param dto DeviceUpdateDTO
	 ------------------------------------------------------ 
	 * @throws ParseException */
	private void setEntity(Ph2DevicesEntity target, DeviceUpdateDTO dto) throws ParseException
		{
// * リクエストの基準日をDate型に変換する
		// 2020の部分は任意
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
		Date baseDate = dateFormat.parse("2020/" + dto.getBaseDateShort());
		target.setName(dto.getName());	// デバイス名
		target.setFieldId(dto.getFieldId()); // 連携する圃場のID
		target.setSigfoxDeviceId(dto.getSigFoxDeviceId());
		target.setBaseDate(baseDate);
		target.setBrand(dto.getBrand());
		target.setTz(dto.getTimeZone());
		target.setPreviousDeviceId(dto.getPrevDeviceId());
		if((null != dto.getOpEndShort())&&(dto.getOpEndShort().trim().length() > 0))
			{
			Date date = dateFormat.parse(dto.getOpEndShort());
			Calendar cal = Calendar.getInstance();
			cal.setTime(date);
			cal.add(Calendar.DATE, 1);
			target.setOpEnd(cal.getTime());
			}
		if((null != dto.getOpStartShort())&&(dto.getOpStartShort().trim().length() > 0))
			{
			target.setOpStart(dateFormat.parse(dto.getOpStartShort()));
			}
		target.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
		}

	
	}
