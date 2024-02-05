package com.logpose.ph2.api.bulk.service;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.logpose.ph2.api.algorythm.DeviceDayAlgorithm;
import com.logpose.ph2.api.bulk.vo.LoadCoordinator;
import com.logpose.ph2.api.dao.db.cache.DeviceDayCacher;
import com.logpose.ph2.api.dao.db.entity.Ph2DeviceDayEntity;
import com.logpose.ph2.api.dao.db.entity.Ph2DeviceDayEntityExample;
import com.logpose.ph2.api.dao.db.entity.Ph2DeviceDayEntityExample.Criteria;
import com.logpose.ph2.api.dao.db.mappers.Ph2DeviceDayMapper;
import com.logpose.ph2.api.dao.db.mappers.Ph2ModelDataMapper;

@Component
public class S4DeviceDayService
	{
	// ===============================================
	// クラスメンバー
	// ===============================================
	private static Logger LOG = LogManager.getLogger(S4DeviceDayService.class);
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
	public List<Ph2DeviceDayEntity> initDeviceDay(LoadCoordinator ldc)
		{
		this.initDeviceDayTable(ldc);
// * 日付の指定が無い場合
		Ph2DeviceDayEntityExample exm = new Ph2DeviceDayEntityExample();
		Criteria criteria = exm.createCriteria().andDeviceIdEqualTo(ldc.getDeviceId());
		if(null != ldc.getLastHadledDate() )
			{
			Date latestTrueDate = this.ph2DeviceDayMapper.selectMaxTrueDate(ldc.getDeviceId());
			if(null != latestTrueDate) criteria.andDateGreaterThan(latestTrueDate);
			}
		exm.setOrderByClause("date  asc");
		return this.ph2DeviceDayMapper.selectByExample(exm);
		}
	
	/**
	 * RelBaseDataの更新最終日がDeviceDayにあるかどうかチェックする
	 * @return 
	 * @return
	 */
	public void initDeviceDayTable(LoadCoordinator ldc)
		{
// * RelBaseDataの更新最終日がDeviceDayにあるかどうかチェックする
		final Date latest = ldc.getLatestBaseDate();
// * 存在すれば、DeviceDayの作成は行わない
		if ((null != latest) && this.checkDeviceDay(ldc.getDeviceId(), latest)) return;
// * デバイスの基準日を得る
		final Calendar baseDate = this.deviceDayAlgorithm
				.getBaseDate(ldc.getDevice().getBaseDate());
		this.deviceDayAlgorithm.setTimeZero(baseDate);

		Date oldestDate = ldc.getOldestBaseDate();
		if (null == oldestDate) return;

		Calendar cal = Calendar.getInstance();
		Calendar today = Calendar.getInstance();
		while (true)
			{
// * 既にデバイスディがあるなら未処理
			if (!this.checkDeviceDay(ldc.getDeviceId(), oldestDate)) break;
// * 既に今日の日付けに達しているなら終了
			if (oldestDate.getTime() == today.getTime().getTime()) return;
// * 一年後
			cal.setTime(oldestDate);
			cal.add(Calendar.YEAR, 1);
			oldestDate = cal.getTime();
// * 今日の日付を超えていたら終了
			if (oldestDate.getTime() > today.getTime().getTime())
				{
				oldestDate = today.getTime();
				}
			}
		Date startDate = oldestDate;
		Calendar star_date_cal = Calendar.getInstance();
		star_date_cal.setTime(startDate);
		this.deviceDayAlgorithm.setTimeZero(star_date_cal);

// * デバイスディの開始日
		baseDate.set(Calendar.YEAR, star_date_cal.get(Calendar.YEAR));
		LOG.info("デバイスの基準日開始日:" + ldc.getDeviceId() + ":" + baseDate.getTime());
		if (star_date_cal.getTimeInMillis() < baseDate.getTimeInMillis())
			{
			star_date_cal.add(Calendar.YEAR, -1);
			}
		star_date_cal.set(Calendar.MONTH, baseDate.get(Calendar.MONTH));
		star_date_cal.set(Calendar.DATE, baseDate.get(Calendar.DATE));
		LOG.info("デバイスの基準日調整後開始日:" + ldc.getDeviceId() + ":" + star_date_cal.getTime());
// * デバイスディの終了日
		Calendar end_date_cal = Calendar.getInstance();
		end_date_cal.set(Calendar.MONTH, baseDate.get(Calendar.MONTH));
		end_date_cal.set(Calendar.DATE, baseDate.get(Calendar.DATE));
		if (end_date_cal.getTimeInMillis() <= Calendar.getInstance().getTimeInMillis())
			{
			end_date_cal.add(Calendar.YEAR, 1);
			}
		this.deviceDayAlgorithm.setTimeZero(end_date_cal);
		LOG.info("デバイスの基準日最終終了日の翌日:" + ldc.getDeviceId() + ":" + end_date_cal.getTime());
// *デバイス・ディに変更が無いか調べる
// * 同一デバイスの初日を取得
		Ph2DeviceDayEntityExample exm = new Ph2DeviceDayEntityExample();
		exm.createCriteria().andDeviceIdEqualTo(ldc.getDeviceId())
				.andYearEqualTo((short) star_date_cal.get(Calendar.YEAR))
				.andLapseDayEqualTo((short) 1);
		List<Ph2DeviceDayEntity> records = this.ph2DeviceDayMapper.selectByExample(exm);
		// * 既にデータがある場合
		if (records.size() > 0)
			{
			Ph2DeviceDayEntity entity = records.get(0);
			// * 日にちが違っている場合は削除
			if (entity.getDate().getTime() != star_date_cal.getTime().getTime())
				{
				exm.createCriteria().andDeviceIdEqualTo(ldc.getDeviceId())
						.andYearGreaterThanOrEqualTo((short) star_date_cal.get(Calendar.YEAR));
				this.ph2DeviceDayMapper.deleteByExample(exm);
				}
			}
		startDate = star_date_cal.getTime();
		Date endDate = end_date_cal.getTime();
		int index = 1;
		Long maxId = this.ph2DeviceDayMapper.selectMaxId();
		if (null == maxId) maxId = Long.valueOf(1);
		DeviceDayCacher cacher = new DeviceDayCacher(maxId, ph2DeviceDayMapper, ph2ModelDataMapper);
		for (; star_date_cal.getTimeInMillis() < end_date_cal.getTimeInMillis(); star_date_cal.add(
				Calendar.DATE,
				1))
			{
			// 月日が一巡した場合
			if ((star_date_cal.get(Calendar.MONTH) == baseDate.get(Calendar.MONTH)) &&
					(star_date_cal.get(Calendar.DATE) == baseDate.get(Calendar.DATE)))
				{
				index = 1;
				}
			// * 日付テーブルから該当デバイスレコードを取得する。
			exm = new Ph2DeviceDayEntityExample();
			exm.createCriteria().andDeviceIdEqualTo(ldc.getDeviceId())
					.andDateEqualTo(star_date_cal.getTime());
			records = this.ph2DeviceDayMapper.selectByExample(exm);
			Ph2DeviceDayEntity entity;
			// * データが無い場合
			if (0 == records.size())
				{
				entity = new Ph2DeviceDayEntity();
				entity.setDate(star_date_cal.getTime());
				entity.setDeviceId(ldc.getDeviceId());
				entity.setHasReal(false);
				entity.setLapseDay((short) index);
				entity.setYear((short) star_date_cal.get(Calendar.YEAR));
				cacher.addDeviceDayData(entity);
				}
			index++;
			}
		cacher.flush();
		}

	// ===============================================
	// 保護関数群
	// ===============================================
	// --------------------------------------------------
	/**
	 * RelBaseDataの更新最終日がDeviceDayにあるかどうかチェックする
	 * @return RelBaseDataの更新最終日がDeviceDayにあればtrueを返す。
	 */
	// --------------------------------------------------
	private boolean checkDeviceDay(Long deviceId, Date date)
		{
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		this.deviceDayAlgorithm.setTimeZero(cal);
		date = cal.getTime();

		Ph2DeviceDayEntityExample exm = new Ph2DeviceDayEntityExample();
		exm.createCriteria().andDeviceIdEqualTo(deviceId).andDateEqualTo(date);
		List<Ph2DeviceDayEntity> records = this.ph2DeviceDayMapper.selectByExample(exm);
		return (records.size() > 0);
		}
	}
