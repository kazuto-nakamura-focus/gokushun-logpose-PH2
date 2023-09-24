package com.logpose.ph2.api.service;

import java.text.ParseException;

import com.logpose.ph2.api.dto.FruitValuesByDevice;
import com.logpose.ph2.api.dto.FruitValuesDTO;

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
	 * 圃場着果量着果負担詳細取得処理
	 *
	 * @param deviceId-デバイスID
	 * @return 着果実績値
	 * @throws ParseException
	 */
	// --------------------------------------------------
	public FruitValuesDTO getFruitValues(Long deviceId);

	// ===============================================
	// パブリック関数(更新系)
	// ===============================================
	// --------------------------------------------------
	/**
	 * 着果実績値更新
	 *
	 * @param dto
	 *            FruitAmountDTO
	 * @throws ParseException
	 */
	// --------------------------------------------------
	public void setFruitValues(FruitValuesByDevice dto) throws ParseException;
	}
