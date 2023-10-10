package com.logpose.ph2.batch.service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.logpose.ph2.batch.alg.DeviceDayAlgorithm;
import com.logpose.ph2.common.dao.db.entity.Ph2DeviceDayEntity;
import com.logpose.ph2.common.dao.db.entity.Ph2DeviceDayEntityExample;
import com.logpose.ph2.common.dao.db.entity.Ph2DevicesEnyity;
import com.logpose.ph2.common.dao.db.mappers.Ph2DeviceDayMapper;

@Component
public class S2DeviceDayService
	{
	// ===============================================
	// クラスメンバー
	// ===============================================
	@Autowired
	private Ph2DeviceDayMapper ph2DeviceDayMapper;
	@Autowired
	private DeviceDayAlgorithm deviceDayAlgorithm;

	// ===============================================
	// 公開関数群
	// ===============================================
	public List<Ph2DeviceDayEntity> initDeviceDay(Ph2DevicesEnyity device, Date startDate)
		{
		List<Ph2DeviceDayEntity> deviceDays = new ArrayList<>();
		Calendar cal = Calendar.getInstance();
		cal.setTime(startDate);
// * デバイスの基準日を得る
		Calendar baseDate = this.deviceDayAlgorithm.getBaseDate(device.getBaseDate());
		this.deviceDayAlgorithm.setTimeZero(baseDate);
// * デバイス基準日が年始かどうか確定する
		boolean isNotFirstDay = (baseDate.get(Calendar.MONTH) != 0)
				|| (baseDate.get(Calendar.DATE) != 1);
// * 開始日を設定する
		Calendar startCal = Calendar.getInstance();
		// 基準日
		startCal.setTime(baseDate.getTime()); // 2023-1-1
		startCal.set(Calendar.YEAR, cal.get(Calendar.YEAR));// 2022-1-1
		// 基準日が年始で無い場合
		if (isNotFirstDay)
			{
			if (cal.getTimeInMillis() < baseDate.getTimeInMillis())
				{
				startCal.set(Calendar.YEAR, startCal.get(Calendar.YEAR) - 1);
				}
			}
// * 終了日を設定する
		Calendar endCal = Calendar.getInstance();
		endCal.setTime(baseDate.getTime()); // 2023-1-1
		if (isNotFirstDay)
			{
			if (cal.getTimeInMillis() < baseDate.getTimeInMillis())
				{
				endCal.set(Calendar.YEAR, endCal.get(Calendar.YEAR) - 1);
				}
			}
		endCal.set(Calendar.YEAR, endCal.get(Calendar.YEAR) + 1);
		int index = 1;
// *デバイス・ディに変更が無いか調べる
		Ph2DeviceDayEntityExample exm = new Ph2DeviceDayEntityExample();
		exm.createCriteria().andDeviceIdEqualTo(device.getId()).andDateEqualTo(startCal.getTime());
		List<Ph2DeviceDayEntity> records = this.ph2DeviceDayMapper.selectByExample(exm);
		if (records.size() > 0)
			{
			Ph2DeviceDayEntity entity = records.get(0);
			if ((entity.getLapseDay() != 1) && (entity.getYear() != startCal.get(Calendar.YEAR)))
				{
				exm.createCriteria().andDeviceIdEqualTo(device.getId())
						.andYearGreaterThanOrEqualTo((short) startCal.get(Calendar.YEAR));
				this.ph2DeviceDayMapper.deleteByExample(exm);
				}
			}
		short year = (short) startCal.get(Calendar.YEAR);
		for (; startCal.getTimeInMillis() < endCal.getTimeInMillis(); startCal.add(Calendar.DATE,
				1))
			{
			// * 日付テーブルから該当デバイスレコードを取得する。
			exm = new Ph2DeviceDayEntityExample();
			exm.createCriteria().andDeviceIdEqualTo(device.getId())
					.andDateEqualTo(startCal.getTime());
			records = this.ph2DeviceDayMapper.selectByExample(exm);
			Ph2DeviceDayEntity entity;
			// * データが無い場合
			if (0 == records.size())
				{
			    entity = new Ph2DeviceDayEntity();
				entity.setDate(startCal.getTime());
				entity.setDeviceId(device.getId());
				entity.setHasReal(false);
				entity.setLapseDay((short) index++);
				entity.setYear((short) startCal.get(year));
				this.ph2DeviceDayMapper.insert(entity);
				}
			else
				{
				entity = records.get(0);
				}
			deviceDays.add(entity);
			// 月日が一巡した場合
			if((startCal.get(Calendar.MONTH)==baseDate.get(Calendar.MONTH))&&
					(startCal.get(Calendar.DATE)==baseDate.get(Calendar.DATE)) )
				{
				year++;
				}
			}
		return deviceDays;
		}
	}
