package com.logpose.ph2.api.algorythm;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.logpose.ph2.api.dao.db.entity.Ph2DeviceDayEntity;
import com.logpose.ph2.api.dao.db.entity.Ph2DeviceDayEntityExample;
import com.logpose.ph2.api.dao.db.mappers.Ph2DeviceDayMapper;
import com.logpose.ph2.api.dao.db.mappers.joined.GrowthDomainMapper;
import com.logpose.ph2.api.dto.DailyBaseDataDTO;

@Component
public class DailyDataAlgorythm
	{
	// ===============================================
	// クラスメンバー
	// ===============================================
	@Autowired
	private GrowthDomainMapper growthDomainMapper;
	@Autowired
	private Ph2DeviceDayMapper ph2DeviceDayMapper;

	// ===============================================
	// 公開関数
	// ===============================================
	// --------------------------------------------------
	/**
	 *  DailyBaseデータを日付順に取得する。
	 * 
	 * @param deviceId 対象となるデバイスID
	 * @param year 対象となる年度
	 * @return DailyBaseデータ
	 */
	// --------------------------------------------------
	public List<DailyBaseDataDTO> getDailyBaseData(Long deviceId, Short year)
		{
// * DailyBaseDataの実気温情報を取得
		List<DailyBaseDataDTO> dailyBaseData = this.growthDomainMapper
				.selectDailyata(deviceId, year, null, true);
// * 統計対象開始日からまだ存在していないDailyBaseDataの気温情報を一年前のものから取得
// ** 取得日の設定
		Calendar cal = Calendar.getInstance();
		Date startDate = null;
		if (0 < dailyBaseData.size())
			{
			cal.setTime(dailyBaseData.get(dailyBaseData.size() - 1).getDate());
			cal.add(Calendar.YEAR, -1);
			cal.add(Calendar.DATE, 1);
			startDate = cal.getTime();
// ** データの取得
			List<DailyBaseDataDTO> pastDayData = this.growthDomainMapper
					.selectDailyata(deviceId, (short) (year - 1), startDate,
							false);
// * 日付が連続している場合対象年度のデータとその昨年度のデータを合成
			if (null != startDate)
				{
				if (pastDayData.get(0).getDate().getTime() == startDate.getTime())
					{
					// * 日付を変更する
					this.exchangeDay(deviceId, year, pastDayData);
					dailyBaseData.addAll(pastDayData);
					}
				}
			}
// * データを返却
		return dailyBaseData;
		}

	// ===============================================
	// プライベート関数
	// ===============================================
	// --------------------------------------------------
	/**
	 *  過去データの日付を置き換える
	 * 
	 * @param deviceId 対象となるデバイスID
	 * @param year 対象となる年度
	 * @param DailyBaseデータ
	 */
	// --------------------------------------------------
	private void exchangeDay(Long deviceId, Short year,
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
	}
