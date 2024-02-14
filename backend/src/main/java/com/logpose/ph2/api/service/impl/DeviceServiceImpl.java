package com.logpose.ph2.api.service.impl;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.logpose.ph2.api.bulk.domain.DeviceStatusDomain;
import com.logpose.ph2.api.dao.db.entity.Ph2DevicesEnyity;
import com.logpose.ph2.api.dao.db.mappers.Ph2DevicesMapper;
import com.logpose.ph2.api.domain.DeviceDomain;
import com.logpose.ph2.api.domain.MasterDomain;
import com.logpose.ph2.api.domain.SensorDomain;
import com.logpose.ph2.api.dto.DeviceInfoDTO;
import com.logpose.ph2.api.dto.device.DeviceDetailDTO;
import com.logpose.ph2.api.dto.device.DeviceMastersDTO;
import com.logpose.ph2.api.dto.device.DeviceUpdateDTO;
import com.logpose.ph2.api.dto.senseor.SensorUnitReference;
import com.logpose.ph2.api.service.DeviceService;

/**
 * デバイスに関する参照・更新サービス
 *
 */
@Service
public class DeviceServiceImpl implements DeviceService
	{
	// ===============================================
	// クラスメンバー
	// ===============================================
	@Autowired
	private Ph2DevicesMapper devicesMapper;
	@Autowired
	private DeviceDomain deviceDomain;
	@Autowired
	private SensorDomain sensorDomain;
	@Autowired
	private MasterDomain masterDomain;
	@Autowired
	private DeviceStatusDomain deviceStatusDomain;

	// ===============================================
	// パブリック関数群
	// ===============================================
	/** --------------------------------------------------
	 * デバイス関連のマスター情報を取得する。
	 *
	 * @return DeviceMastersDTO
	 ------------------------------------------------------ */
	@Override
	public DeviceMastersDTO getMasters()
		{
		return this.masterDomain.getMasters();
		}

	// --------------------------------------------------
	/**
	 * デバイス一覧取得
	 *
	 * @return List<DeviceInfoDTO>
	 */
	// --------------------------------------------------
	@Override
	@Transactional(readOnly = true)
	public List<DeviceInfoDTO> list()
		{
		return this.deviceDomain.getDeviceList();
		}

	// --------------------------------------------------
	/**
	 * デバイス情報詳細取得
	 *
	 * @param deviceId デバイスID
	 * @return DeviceDetailDTO
	 */
	// --------------------------------------------------
	@Override
	public DeviceDetailDTO getDetail(Long deviceId)
		{
		DeviceDetailDTO result = this.deviceDomain.getDetail(deviceId);
		// * 基準日を表示用に修正する
		Date baseDate = result.getBaseDate();
		// * 未指定の場合
		if (null == baseDate)
			{
			result.setBaseDateShort("01/01");
			}
		else
			{
			SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd");
			result.setBaseDateShort(dateFormat.format(baseDate));
			}
		if(null != result.getOpStart())
			{
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
			result.setOpStartShort(dateFormat.format(result.getOpStart()));
			}
		if(null != result.getOpEnd())
			{
			Calendar cal = Calendar.getInstance();
			cal.setTime(result.getOpEnd());
			cal.add(Calendar.DATE, -1);
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
			result.setOpEndShort(dateFormat.format(cal.getTime()));
			}
		List<SensorUnitReference> sensors = this.sensorDomain.getSensors(deviceId);
		result.setSensorItems(sensors);
		return result;
		}

	// --------------------------------------------------
	/**
	 * デバイス削除
	 * 
	 * @param deviceId 削除するデバイスID
	 */
	// --------------------------------------------------
	@Override
	@Transactional(rollbackFor = Exception.class)
	public void delete(Long deviceId)
		{
// * デバイス情報の取得
		Ph2DevicesEnyity device = this.devicesMapper.selectByPrimaryKey(deviceId);
// * 関連デバイスのロック
		List<Long> locks = this.deviceStatusDomain.lockDevices(device);
		try
			{
// * デバイスIDに紐づくセンサーの削除
			this.sensorDomain.delete(deviceId);
// * デバイスの削除
			this.deviceDomain.deleteByDeviceId(deviceId);
			}
		catch (Exception e)
			{
			throw e;
			}
		finally
			{
			this.deviceStatusDomain.unLockDevices(locks);
			}
		}

	// --------------------------------------------------
	/**
	 * デバイスの追加を行う。
	 *
	 *@param dto DeviceUpdateDTO
	 *@throws ParseException */
	// --------------------------------------------------
	@Override
	@Transactional(rollbackFor = Exception.class)
	public void addInfo(DeviceUpdateDTO dto) throws ParseException
		{
// * デバイスの追加
		Ph2DevicesEnyity device = this.deviceDomain.add(dto);
// * 関連デバイスのロック
		List<Long> locks = this.deviceStatusDomain.lockDevices(device);
		try
			{
// * 引継ぎ元データの設定（あれば）
			this.deviceDomain.setPreviousDevice(device);
// * センサーの追加
			if (0 < dto.getSensorItems().size())
				{
				this.sensorDomain.add(device.getId(), dto.getSensorItems());
				}
			}
		catch (Exception e)
			{
			throw e;
			}
		finally
			{
			this.deviceStatusDomain.unLockDevices(locks);
			}
		}

	// --------------------------------------------------
	/**
	 * デバイスの更新
	 *
	 *@param dto DeviceUpdateDTO
	 */
	// --------------------------------------------------
	@Override
	@Transactional(rollbackFor = Exception.class)
	public void updateInfo(DeviceUpdateDTO dto) throws ParseException
		{
// * デバイス情報の取得
		Ph2DevicesEnyity device = this.devicesMapper.selectByPrimaryKey(dto.getId());
// * 関連デバイスのロック
		List<Long> locks = this.deviceStatusDomain.lockDevices(device);
		try
			{
			this.deviceDomain.update(dto);
// * 引継ぎ元データの設定（あれば）
			this.deviceDomain.setPreviousDevice(device);
			this.sensorDomain.add(dto.getId(), dto.getSensorItems());
			}
		catch (Exception e)
			{
			throw e;
			}
		finally
			{
			this.deviceStatusDomain.unLockDevices(locks);
			}
		}
	}
