package com.logpose.ph2.api.bulk.service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.logpose.ph2.api.algorythm.DeviceDayAlgorithm;
import com.logpose.ph2.api.dao.db.entity.Ph2DeviceDayEntity;
import com.logpose.ph2.api.dao.db.entity.Ph2DeviceDayEntityExample;
import com.logpose.ph2.api.dao.db.entity.Ph2DevicesEnyity;
import com.logpose.ph2.api.dao.db.entity.Ph2ModelDataEntity;
import com.logpose.ph2.api.dao.db.mappers.Ph2DeviceDayMapper;
import com.logpose.ph2.api.dao.db.mappers.Ph2ModelDataMapper;

@Component
public class S2DeviceDayService
	{
	// ===============================================
	// クラスメンバー
	// ===============================================
	private static Logger LOG = LogManager.getLogger(S2DeviceDayService.class);
	@Autowired
	private Ph2DeviceDayMapper ph2DeviceDayMapper;
	@Autowired
	private Ph2ModelDataMapper ph2ModelDataMapper;
	@Autowired
	private DeviceDayAlgorithm deviceDayAlgorithm;

	// ===============================================
	// 公開関数群
	// ===============================================
	@Transactional(rollbackFor = Exception.class)
	public List<Ph2DeviceDayEntity> initDeviceDay(Ph2DevicesEnyity device, Date startDate)
		{
		List<Ph2DeviceDayEntity> deviceDays = new ArrayList<>();
		Calendar star_date_cal = Calendar.getInstance();
		star_date_cal.setTime(startDate);
		this.deviceDayAlgorithm.setTimeZero(star_date_cal);
// * デバイスの基準日を得る
		Calendar baseDate = this.deviceDayAlgorithm.getBaseDate(device.getBaseDate());
		this.deviceDayAlgorithm.setTimeZero(baseDate);
// * デバイスディの開始日
		baseDate.set(Calendar.YEAR, star_date_cal.get(Calendar.YEAR));
		LOG.info("デバイスの基準日開始日:"+ device.getId() + ":" + baseDate.getTime());
		if(star_date_cal.getTimeInMillis() < baseDate.getTimeInMillis())
			{
			star_date_cal.add(Calendar.YEAR, -1);
			}
		star_date_cal.set(Calendar.MONTH, baseDate.get(Calendar.MONTH));
		star_date_cal.set(Calendar.DATE, baseDate.get(Calendar.DATE));
		LOG.info("デバイスの基準日調整後開始日:"+ device.getId() + ":" + star_date_cal.getTime());
// * デバイスディの終了日
		Calendar end_date_cal = Calendar.getInstance();
		end_date_cal.set(Calendar.MONTH, baseDate.get(Calendar.MONTH));
		end_date_cal.set(Calendar.DATE, baseDate.get(Calendar.DATE));
		if(end_date_cal.getTimeInMillis() <= Calendar.getInstance().getTimeInMillis())
			{
			end_date_cal.add(Calendar.YEAR, 1);
			}
		this.deviceDayAlgorithm.setTimeZero(end_date_cal);
		LOG.info("デバイスの基準日最終終了日の翌日:"+ device.getId() + ":" + end_date_cal.getTime());
// *デバイス・ディに変更が無いか調べる
		// * 同一デバイスの初日を取得
		Ph2DeviceDayEntityExample exm = new Ph2DeviceDayEntityExample();
		exm.createCriteria().andDeviceIdEqualTo(device.getId()).
			andYearEqualTo((short) star_date_cal.get(Calendar.YEAR)).andLapseDayEqualTo((short) 1);
		List<Ph2DeviceDayEntity> records = this.ph2DeviceDayMapper.selectByExample(exm);
		//* 既にデータがある場合
		if (records.size() > 0)
			{
			Ph2DeviceDayEntity entity = records.get(0);
			//* 日にちが違っている場合は削除
			if (entity.getDate().getTime() != star_date_cal.getTime().getTime())
				{
				exm.createCriteria().andDeviceIdEqualTo(device.getId())
						.andYearGreaterThanOrEqualTo((short) star_date_cal.get(Calendar.YEAR));
				this.ph2DeviceDayMapper.deleteByExample(exm);
				}
			}
		int index = 1;
		for (; star_date_cal.getTimeInMillis() < end_date_cal.getTimeInMillis(); star_date_cal.add(Calendar.DATE,
				1))
			{
			// 月日が一巡した場合
			if((star_date_cal.get(Calendar.MONTH)==baseDate.get(Calendar.MONTH))&&
					(star_date_cal.get(Calendar.DATE)==baseDate.get(Calendar.DATE)) )
				{
				index = 1;
				}
			// * 日付テーブルから該当デバイスレコードを取得する。
			exm = new Ph2DeviceDayEntityExample();
			exm.createCriteria().andDeviceIdEqualTo(device.getId())
					.andDateEqualTo(star_date_cal.getTime());
			records = this.ph2DeviceDayMapper.selectByExample(exm);
			Ph2DeviceDayEntity entity;
			// * データが無い場合
			if (0 == records.size())
				{
			    entity = new Ph2DeviceDayEntity();
				entity.setDate(star_date_cal.getTime());
				entity.setDeviceId(device.getId());
				entity.setHasReal(false);
				entity.setLapseDay((short) index);
				entity.setYear((short) star_date_cal.get(Calendar.YEAR));
				long id = this.ph2DeviceDayMapper.insert(entity);
				entity.setId(id);
// * データモデルの準備
				Ph2ModelDataEntity model = new Ph2ModelDataEntity();
				model.setDayId(id);
				this.ph2ModelDataMapper.insert(model);
				}
			else
				{
				entity = records.get(0);
				}
			index++;
			deviceDays.add(entity);
			}
		return deviceDays;
		}
	}
