package com.logpose.ph2.api.bulk.domain;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.logpose.ph2.batch.dao.db.entity.Ph2DeviceDayEntity;
import com.logpose.ph2.batch.dao.db.entity.Ph2DeviceDayEntityExample;
import com.logpose.ph2.batch.dao.db.mappers.Ph2DeviceDayMapper;
import com.logpose.ph2.batch.domain.YearDateModel;

@Component
public class DeviceDayDomain
	{
	// ===============================================
	// クラスメンバー
	// ===============================================
	@Autowired
	private Ph2DeviceDayMapper ph2DeviceDayMapper;

	// ===============================================
	// 公開関数群
	// ===============================================
	// --------------------------------------------------
	/**
	 * 指定されたデバイスに対して、指定日からの日付テーブルの初期化を行う。
	 *
	 * @param deviceId デバイスID
	 * @param yearDateModel デバイスの基準日
	 * @param date データの更新開始日
	 */
	// --------------------------------------------------
	public void initDeviceDay(Long deviceId, YearDateModel yearDateModel, Date beginDate)
		{
		// * 日付から年度を抽出
		Calendar cal = Calendar.getInstance();
		// * 現在の年
		short currentYear = (short) cal.get(Calendar.YEAR);
		// * 更新日の年
		cal.setTime(beginDate);
		short startYear = (short) cal.get(Calendar.YEAR);
		// * 更新日から現在の年まで日付テーブルの初期化を行う。
		for (short i = startYear; i < currentYear + 1; i++)
			{
			// * 日付テーブルから該当デバイスレコードを取得する。
			Ph2DeviceDayEntityExample exm = new Ph2DeviceDayEntityExample();
			exm.createCriteria().andDeviceIdEqualTo(deviceId).andYearEqualTo((short) i);
			List<Ph2DeviceDayEntity> records = this.ph2DeviceDayMapper.selectByExample(exm);
			// * もしも該当デバイスに対して、日付テーブルが存在しない場合
			if (0 == records.size())
				{
				Calendar beginDay = Calendar.getInstance();
				// * 年度の設定
				beginDay.set(i, yearDateModel.getBaseMonth(),
						yearDateModel.getBaseDate(), 0, 0, 0);
				beginDay.set(Calendar.MILLISECOND, 0);
				//* 最終日
				Calendar endDay = Calendar.getInstance();
				endDay.set(i+1, yearDateModel.getBaseMonth(),
						yearDateModel.getBaseDate(), 0, 0, 0);
				endDay.set(Calendar.MILLISECOND, 0);
				short count  =0;
				while(beginDay.getTime().getTime()<endDay.getTime().getTime())
					{
					Ph2DeviceDayEntity entity = new Ph2DeviceDayEntity();
					entity.setDate(beginDay.getTime());
					entity.setDeviceId(deviceId);
					entity.setHasReal(false);
					entity.setLapseDay(count);
					entity.setYear(i);
					this.ph2DeviceDayMapper.insert(entity);
					count++;
					beginDay.add(Calendar.DATE, 1);
					}
				}
			else
				{
				Ph2DeviceDayEntity entity = new Ph2DeviceDayEntity();
				entity.setHasReal(false);
				this.ph2DeviceDayMapper.updateByExampleSelective(entity, exm);
				}
			}
		}
	}
