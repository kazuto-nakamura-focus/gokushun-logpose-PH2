package com.logpose.ph2.api.service;

import java.text.ParseException;
import java.util.List;

import com.logpose.ph2.api.dto.DeviceInfoDTO;
import com.logpose.ph2.api.dto.device.DeviceDetailDTO;
import com.logpose.ph2.api.dto.device.DeviceMastersDTO;
import com.logpose.ph2.api.dto.device.DeviceUpdateDTO;

/**
 * デバイスに関する参照・更新サービス
 *
 */
public interface DeviceService
	{
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
	public List<DeviceInfoDTO> list();

	// --------------------------------------------------
	/**
	 * デバイス削除
	 *
	 * @param deviceId
	 *            デバイスID
	 */
	// --------------------------------------------------
	public void delete(Long deviceId);

	// --------------------------------------------------
	/**
	 * デバイス情報詳細取得
	 *
	 * @param deviceId
	 *            デバイスID
	 * @return DeviceDetailDTO
	 */
	// --------------------------------------------------
	public DeviceDetailDTO getDetail(Long deviceId);
	// --------------------------------------------------
	/**
	 * デバイス情報追加
	 *
	 * @param dto
	 *            DeviceDetailDTO
	 * @throws ParseException 
	 */
	// --------------------------------------------------
	public void addInfo(DeviceUpdateDTO dto) throws ParseException;

	// --------------------------------------------------
	/**
	 * デバイス情報更新
	 *
	 * @param dto
	 *            DeviceDetailDTO
	 * @return ResponseDTO (null)
	 * @throws ParseException 
	 */
	// --------------------------------------------------
	public void updateInfo(DeviceUpdateDTO dto) throws ParseException;

	/** --------------------------------------------------
	 * デバイス関連のマスター情報を取得する。
	 *
	 * @return DeviceMastersDTO
	 ------------------------------------------------------ */
	public DeviceMastersDTO getMasters();

	// --------------------------------------------------
	/**
	 * デバイス情報の引継ぎ
	 *
	 * @param deviceId 引継ぎ先デバイスID
	 */
	// --------------------------------------------------
	void transitParameters(Long deviceId);
	}
