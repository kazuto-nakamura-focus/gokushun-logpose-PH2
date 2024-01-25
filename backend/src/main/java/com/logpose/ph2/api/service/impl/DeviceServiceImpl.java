package com.logpose.ph2.api.service.impl;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
	private DeviceDomain deviceDomain;
	@Autowired
	private SensorDomain sensorDomain;
	@Autowired
	private MasterDomain masterDomain;

	// ===============================================
	// 公開関数
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

	// ###############################################
	/**
	 * デバイス一覧取得
	 *
	 * @return List<DeviceInfoDTO>
	 */
	// ###############################################
	@Override
	@Transactional(rollbackFor = Exception.class, transactionManager = "txManager2")
	public List<DeviceInfoDTO> list()
		{
		return this.deviceDomain.getDeviceList();
		}

	// ###############################################
	/**
	 * デバイス削除
	 *
	 * @param deviceId デバイスID
	 */
	// ###############################################
	@Override
	@Transactional(rollbackFor = Exception.class)
	public void delete(Long deviceId)
		{
		// * デバイスIDに紐づくセンサーの削除
		this.sensorDomain.delete(deviceId);
		// * デバイスの削除
		this.deviceDomain.deleteByDeviceId(deviceId);
		}
	// ###############################################
	/**
	 * デバイス情報詳細取得
	 *
	 * @param deviceId デバイスID
	 * @return 
	 * @return DeviceDetailDTO
	 */
	// ###############################################

	/** --------------------------------------------------
	 * デバイスの追加を行う。
	 *
	 *@param dto DeviceUpdateDTO
	 *@throws ParseException */
	// ------------------------------------------------------
	@Override
	@Transactional(rollbackFor = Exception.class)
	public void addInfo(DeviceUpdateDTO dto) throws ParseException
		{
// * デバイスの追加
		Long deviceId = this.deviceDomain.add(dto);
// * センサーの追加
		if( 0 < dto.getSensorItems().size())
			{
			this.sensorDomain.add(deviceId, dto.getSensorItems());
			}
		}

	/** --------------------------------------------------
	 * デバイスの更新
	 *
	 *@param dto DeviceUpdateDTO
	 ------------------------------------------------------ */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public void updateInfo(DeviceUpdateDTO dto) throws ParseException
		{
		this.deviceDomain.update(dto);
		this.sensorDomain.add(dto.getId(), dto.getSensorItems());
		}

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
		List<SensorUnitReference> sensors = this.sensorDomain.getSensors(deviceId);
		result.setSensorItems(sensors);
		return result;
		}

	}
