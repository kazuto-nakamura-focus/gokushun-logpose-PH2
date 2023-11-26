package com.logpose.ph2.api.service;

import java.text.ParseException;
import java.util.Date;

import com.logpose.ph2.api.dao.db.entity.Ph2RealFruitsDataEntity;
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
	 * @param deviceId - デバイスID
	 * @param year - 対象年度
	 * @return 着果実績値
	 * @throws ParseException
	 */
	// --------------------------------------------------
	public FruitValuesDTO getFruitValues(Long deviceId, Short year);

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
	// --------------------------------------------------
	/**
	 * 着果実績値取得
	 *
	 * @param deviceId
	 * @param date
	 * @param eventId
	 * @return Ph2RealFruitsDataEntity
	 */
	// --------------------------------------------------
	Ph2RealFruitsDataEntity getRealFruitsData(Long deviceId, Date date, short eventId);
	}
