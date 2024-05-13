package com.logpose.ph2.api.service;

import java.text.ParseException;
import java.util.List;

import com.logpose.ph2.api.dto.FruitValuesByDevice;
import com.logpose.ph2.api.dto.FruitValuesDTO;
import com.logpose.ph2.api.dto.bearing.BearingDTO;
import com.logpose.ph2.api.dto.bearing.RealFruitsValueDTO;

/**
 * 着果量着果負担のサービスインターフェスクラス
 *
 */
public interface FruitsService
	{
	// ===============================================
	// パブリック関数(検索系)
	// ===============================================
	// --------------------------------------------------
	/**
	 * 着果実績値取得
	 *
	 * @param deviceId
	 * @param date
	 * @param eventId
	 * @return Ph2RealFruitsDataEntity
	 * @throws ParseException 
	 */
	// --------------------------------------------------
	List<RealFruitsValueDTO> getRealFruitsData(Long deviceId, Short year) throws ParseException;
	// --------------------------------------------------
	/**
	 * 圃場着果量着果負担詳細取得処理
	 *
	 * @param deviceId - デバイスID
	 * @param year - 対象年度
	 * @return 着果実績値
	 * @throws ParseException
	 */
	// --------------------------------------------------
	public FruitValuesDTO getFruitValues(Long deviceId, Short year);
	// --------------------------------------------------
	/**
	 * 圃場着果量着果負担詳細取得処理Ver2
	 *
	 * @param deviceId - デバイスID
	 * @param year - 対象年度
	 * @return 着果実績値
	 * @throws ParseException
	 */
	// --------------------------------------------------
	public BearingDTO getFruitValues2(Long deviceId, Short year) throws ParseException;
	
	// ===============================================
	// パブリック関数(更新系)
	// ===============================================
	// --------------------------------------------------
	/**
	 * 着果実績値更新
	 *
	 * @param dto FruitAmountDTO
	 * @throws ParseException
	 */
	// --------------------------------------------------
	public void setFruitValues(FruitValuesByDevice dto) throws ParseException;



	}
