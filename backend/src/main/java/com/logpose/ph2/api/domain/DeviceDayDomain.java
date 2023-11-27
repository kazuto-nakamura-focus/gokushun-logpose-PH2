package com.logpose.ph2.api.domain;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.logpose.ph2.api.dao.db.entity.Ph2DeviceDayEntity;
import com.logpose.ph2.api.dao.db.entity.Ph2DeviceDayEntityExample;
import com.logpose.ph2.api.dao.db.entity.Ph2ModelDataEntity;
import com.logpose.ph2.api.dao.db.entity.Ph2ModelDataEntityExample;
import com.logpose.ph2.api.dao.db.mappers.Ph2DeviceDayMapper;
import com.logpose.ph2.api.dao.db.mappers.Ph2ModelDataMapper;
import com.logpose.ph2.api.dto.DailyBaseDataDTO;

@Component
public class DeviceDayDomain
	{
	// ===============================================
	// クラスメンバー
	// ===============================================
	@Autowired
	private Ph2DeviceDayMapper ph2DeviceDayMapper;
	@Autowired
	private Ph2ModelDataMapper ph2ModelDataMapper;

	// ===============================================
	// 公開関数群
	// ===============================================
	public void exchangeDay(Long deviceId, Short year,
			List<DailyBaseDataDTO> src)
		{
		for (DailyBaseDataDTO item : src)
			{
			Ph2DeviceDayEntityExample exm = new Ph2DeviceDayEntityExample();
			exm.createCriteria().andDeviceIdEqualTo(deviceId)
					.andYearEqualTo(year)
					.andLapseDayEqualTo(item.getLapseDay());
			List<Ph2DeviceDayEntity> days = this.ph2DeviceDayMapper
					.selectByExample(exm);
			Ph2DeviceDayEntity day = days.get(0);
			item.setDate(day.getDate());
			item.setDayId(day.getId());
			}
		}

	/** --------------------------------------------------
	 * モデルテーブルの累積F値を更新する
	 * 
	 * @param deviceId デバイスID
	 * @param year 統計対象開始日
	 * @param data 値のリスト
	 * @return 統計終了日
	 ------------------------------------------------------ */
	public short updateModelData(short lapseDay, Long deviceId, short refyear, short targetyear,
			List<Double> data)
		{
		for (Double value : data)
			{
// * デバイスの日付ごとのデータからその年の年度の経過日の参照データを取得する
// * --- START ---
			Ph2DeviceDayEntityExample ddexm = new Ph2DeviceDayEntityExample();
			ddexm.createCriteria().andDeviceIdEqualTo(deviceId)
					.andYearEqualTo(refyear).andLapseDayEqualTo(lapseDay);
			List<Ph2DeviceDayEntity> days = this.ph2DeviceDayMapper
					.selectByExample(ddexm);
			if (days.size() == 0)
				{
				break;
				}
			Ph2DeviceDayEntity ddentity = days.get(0);
			// * ---END---
			// 該当データがない場合は終了
			if (days.size() == 0)
				break;
// * デバイス日付IDに対応する参照モデルデータを取得する
			Ph2ModelDataEntityExample exm = new Ph2ModelDataEntityExample();
			exm.createCriteria().andDayIdEqualTo(ddentity.getId());
			List<Ph2ModelDataEntity> srclist = this.ph2ModelDataMapper.selectByExample(exm);
// * 年度が違う場合、更新するモデルデータを取得する
			List<Ph2ModelDataEntity> dstlist = srclist;
			if (refyear != targetyear)
				{
				ddexm = new Ph2DeviceDayEntityExample();
				ddexm.createCriteria().andDeviceIdEqualTo(deviceId)
						.andYearEqualTo(targetyear).andLapseDayEqualTo(lapseDay);
				days = this.ph2DeviceDayMapper
						.selectByExample(ddexm);
				ddentity = days.get(0);
				// * デバイス日付IDに対応する参照モデルデータを取得する
				exm = new Ph2ModelDataEntityExample();
				exm.createCriteria().andDayIdEqualTo(ddentity.getId());
				dstlist = this.ph2ModelDataMapper.selectByExample(exm);
				}
			Ph2ModelDataEntity entity;
// * 既にモデルデータが存在する場合は値を更新する。
			if (dstlist.size() > 0)
				{
				entity = dstlist.get(0);
				entity.setfValue(value);
				this.ph2ModelDataMapper.updateByExample(entity, exm);
				}
// * モデルデータが存在しない場合は、モデルデータを生成し、テーブルに追加
			else
				{
				entity = new Ph2ModelDataEntity();
				entity.setCrownLeafArea(null);
				entity.setCulmitiveCnopyPs(null);
				entity.setDayId(ddentity.getId());
				entity.setLeafCount(null);
				entity.setfValue(value);
				this.ph2ModelDataMapper.insert(entity);
				}
			lapseDay++;
			}
		return lapseDay;
		}

	// --------------------------------------------------
	/**
	 * デバイスの最初の年度開始日を取得する
	 *
	 * @param deviceId  デバイスID
	 * @param year 対象年度
	 * @return Ph2DeviceDayEntity  デバイスの年度開始日情報
	 */
	// --------------------------------------------------
	public Ph2DeviceDayEntity getFirstDay(Long deviceId, Short year)
		{
		Ph2DeviceDayEntityExample exm = new Ph2DeviceDayEntityExample();
		exm.createCriteria().andDeviceIdEqualTo(deviceId).andYearEqualTo(year)
				.andLapseDayEqualTo((short) 1);
		return this.ph2DeviceDayMapper.selectByExample(exm).get(0);
		}

	// --------------------------------------------------
	/**
	 * 指定された日のデバイスの年度を取得する
	 *
	 * @param deviceId  デバイスID
	 * @param date 日付
	 * @return short  年度
	 */
	// --------------------------------------------------
	public short getYear(Long deviceId, Date date)
		{
		return this.ph2DeviceDayMapper.getYear(deviceId, date);
		}
	}
