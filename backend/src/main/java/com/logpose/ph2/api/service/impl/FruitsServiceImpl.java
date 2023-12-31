package com.logpose.ph2.api.service.impl;

import java.text.ParseException;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.logpose.ph2.api.dao.db.entity.Ph2RealFruitsDataEntity;
import com.logpose.ph2.api.domain.FruitDomain;
import com.logpose.ph2.api.dto.FruitValuesByDevice;
import com.logpose.ph2.api.dto.FruitValuesDTO;
import com.logpose.ph2.api.service.FruitsService;
import com.logpose.ph2.api.utility.DateTimeUtility;

/**
 * 着果量着果負担のサービスインターフェスクラス
 *
 */
@Service
public class FruitsServiceImpl implements FruitsService
	{
	// ===============================================
	// クラスメンバー
	// ===============================================
	@Autowired
	private FruitDomain fruitDomain;

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
	 */
	// --------------------------------------------------
	@Override
	@Transactional(readOnly = true)
	public Ph2RealFruitsDataEntity getRealFruitsData(Long deviceId, Date date, short eventId)
		{
		return this.fruitDomain.getRealFruitsData(deviceId, date, eventId);
		}

	// --------------------------------------------------
	/**
	 * 圃場着果量着果負担詳細取得処理
	 *
	 * @param deviceId-デバイスID
	 * @param year - 年度
	 * @return 着果実績値
	 * @throws ParseException
	 */
	// --------------------------------------------------
	@Override
	@Transactional(readOnly = true)
	public FruitValuesDTO getFruitValues(Long deviceId, Short year)
		{
		return this.fruitDomain.getFruitData(deviceId, year);
		}

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
	@Override
	@Transactional(rollbackFor = Exception.class)
	public void setFruitValues(FruitValuesByDevice dto) throws ParseException
		{
		Ph2RealFruitsDataEntity entity = new Ph2RealFruitsDataEntity();
		entity.setAverage(dto.getWeight());
		entity.setCount(dto.getCount());
		entity.setDeviceId(dto.getDeviceId());
		entity.setEventId(dto.getEventId());
		entity.setTargetDate(DateTimeUtility.getDateFromString(dto.getDate()));
		entity.setYear(dto.getYear());

		this.fruitDomain.setFruitValue(entity);
		}

	}
