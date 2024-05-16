package com.logpose.ph2.api.domain;

import java.sql.Timestamp;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.logpose.ph2.api.algorythm.DateTimeUtility;
import com.logpose.ph2.api.dao.db.entity.Ph2ParamsetLeafCountEntity;
import com.logpose.ph2.api.dao.db.entity.Ph2RealFruitsDataEntity;
import com.logpose.ph2.api.dao.db.entity.Ph2RealFruitsDataEntityExample;
import com.logpose.ph2.api.dao.db.entity.joined.FruitModelDataEntity;
import com.logpose.ph2.api.dao.db.mappers.Ph2ParamsetLeafCountMapper;
import com.logpose.ph2.api.dao.db.mappers.Ph2RealFruitsDataMapper;
import com.logpose.ph2.api.dao.db.mappers.joined.Ph2FruitsModelMapper;
import com.logpose.ph2.api.dto.FruitValuesDTO;
import com.logpose.ph2.api.dto.bearing.RealFruitsValueDTO;

@Component
public class FruitDomain
	{
	// ===============================================
	// クラスメンバー
	// ===============================================
	@Autowired
	private Ph2RealFruitsDataMapper ph2RealFruitsDataMapper;

	@Autowired
	private Ph2FruitsModelMapper ph2FruitsModelMapper;

	@Autowired
	private Ph2ParamsetLeafCountMapper ph2ParamsetLeafCountMapper;

	// ===============================================
	// パブリック関数群
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
	public List<RealFruitsValueDTO> getRealFruitsData(Long deviceId, short year) throws ParseException
		{
		Ph2RealFruitsDataEntityExample exm = new Ph2RealFruitsDataEntityExample();
		exm.createCriteria().andDeviceIdEqualTo(deviceId).andYearEqualTo(year);
		List<Ph2RealFruitsDataEntity> records =  ph2RealFruitsDataMapper.selectByExample(exm);
		List<RealFruitsValueDTO> result = new ArrayList<>();
		for(final Ph2RealFruitsDataEntity entity : records)
			{
			RealFruitsValueDTO data = new RealFruitsValueDTO();
			data.setAverage(entity.getAverage());
			data.setCount(entity.getCount());
			data.setEventId(entity.getEventId());
			data.setTargetDate(DateTimeUtility.getStringFromDate(entity.getTargetDate()));
			result.add(data);
			}
		return result;
		}

	// --------------------------------------------------
	/**
	 * 着果実績値取得
	 *
	 * @param deviceId デバイスID
	 * @param year 対象年度
	 * @return 着果実績値
	 */
	// --------------------------------------------------
	public FruitValuesDTO getFruitData(Long deviceId, Short year)
		{
		FruitValuesDTO return_value = new FruitValuesDTO();
		// * 対象日付の設定
		Calendar cal = Calendar.getInstance();
		// * 年度が同じ場合
		if (cal.get(Calendar.YEAR) != year)
			{
			cal.add(Calendar.YEAR, 1);
			cal.set(Calendar.DATE, 1);
			cal.add(Calendar.DATE, -1);
			}

		// * データの取得
		List<FruitModelDataEntity> data = this.ph2FruitsModelMapper.getModelData(deviceId,
				cal.getTime());
		if (0 == data.size())
			return return_value;
		FruitModelDataEntity source = data.get(0);

		// * パラメータの取得
		List<Ph2ParamsetLeafCountEntity> params = this.ph2ParamsetLeafCountMapper
				.selectByDefautFlag(deviceId, year);
		if (0 == params.size())
			return return_value;
		Ph2ParamsetLeafCountEntity param = params.get(0);

		// * 着果負担の算出
		double lar = param.getValueC() * source.getTm() + param.getValueD();
		double burden = 0;
		if ((source.getFruitsAverage() != null) && (source.getRealArea() != null)
				&& (source.getLeafCount() != null))
			burden = source.getFruitsAverage()
					/ (lar * (source.getRealArea() / source.getLeafCount()));

		// * '積算推定樹冠光合成量あたりの着果量（g/mol）の算出
		// * 積算推定樹冠光合成量あたりの着果量 = 平均房重 * 実測着果数 / 積算推定樹冠光合成量
		double amount = 0;
		if ((source.getFruitsAverage() != null) && (source.getFruitsCount() != null)
				&& (source.getFruitsCount() != null))
			amount = source.getFruitsAverage() * source.getFruitsCount()
					/ source.getCrownLeafArea();

		// * 実測着果数/樹冠葉面積の算出
		double count = 0;
		if ((source.getFruitsCount() != null) && (source.getCrownLeafArea() != null))
			count = source.getFruitsCount() / source.getCrownLeafArea();

		return_value.setAmount(Double.isNaN(amount)?0:amount);
		return_value.setBurden(Double.isNaN(burden)?0:burden);
		return_value.setCount(Double.isNaN(count)?0:count);

		return return_value;
		}

	// --------------------------------------------------
	/**
	 * 着果実績値更新
	 *
	 * @param dto FruitAmountDTO
	 */
	// --------------------------------------------------
	public void setFruitValue(Ph2RealFruitsDataEntity entity)
		{
		entity.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
		// * 既存のイベントのものがあるか検索
		// *
		// 条件設定
		// *
		Ph2RealFruitsDataEntityExample exm = new Ph2RealFruitsDataEntityExample();
		exm.createCriteria().andDeviceIdEqualTo(entity.getDeviceId())
				.andEventIdEqualTo(entity.getEventId())
				.andTargetDateEqualTo(entity.getTargetDate());
		// *
		// 検索
		// *
		List<Ph2RealFruitsDataEntity> records = this.ph2RealFruitsDataMapper.selectByExample(exm);
		// *
		// すでにある場合
		// *
		if (records.size() > 0)
			{
			entity.setCreatedAt(records.get(0).getCreatedAt());
			this.ph2RealFruitsDataMapper.updateByExample(entity, exm);
			}
		// *
		// 新規の場合
		// *
		else
			{
			entity.setCreatedAt(new Timestamp(System.currentTimeMillis()));
			this.ph2RealFruitsDataMapper.insert(entity);
			}
		}
	}
