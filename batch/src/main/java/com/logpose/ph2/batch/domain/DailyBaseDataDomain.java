package com.logpose.ph2.batch.domain;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.logpose.ph2.batch.dao.db.entity.Ph2DailyBaseDataEntity;
import com.logpose.ph2.batch.dao.db.entity.Ph2DailyBaseDataEntityExample;
import com.logpose.ph2.batch.dao.db.entity.Ph2DeviceDayEntity;
import com.logpose.ph2.batch.dao.db.entity.Ph2DeviceDayEntityExample;
import com.logpose.ph2.batch.dao.db.mappers.Ph2DailyBaseDataMapper;
import com.logpose.ph2.batch.dao.db.mappers.Ph2DeviceDayMapper;
import com.logpose.ph2.batch.dto.BaseDataDTO;
import com.logpose.ph2.batch.formula.Formula;
import com.logpose.ph2.batch.service.S2DeviceDayService;

@Component
public class DailyBaseDataDomain extends S2DeviceDayService
	{
	// ===============================================
	// クラスメンバー
	// ===============================================
	@Autowired
	private Ph2DeviceDayMapper ph2DeviceDayMapper;
	@Autowired
	private Ph2DailyBaseDataMapper ph2DailyBaseDataMapper;

	// ===============================================
	// 公開関数群
	// ===============================================
	// --------------------------------------------------
	/**
	 * デバイス日付テーブルと日別基礎データテーブルにデータを
	 * 追加する
	 */
	// --------------------------------------------------
	public void createTMValues(Long deviceId, YearDateModel yearDateModel, List<BaseDataDTO> records)
		{
		Calendar date = null;
		Calendar recordTime = null;
		List<BaseDataDTO> tempList = new ArrayList<>();

		for (BaseDataDTO record : records)
			{
			// * 対象データのレコード時刻を設定
			recordTime = Calendar.getInstance();
			recordTime.setTime(record.getCastedAt());
			// * 対象データのレコード時刻から年度と経過日を設定
			//	yearDateModel.setYear(record.getCastedAt());
			// * 日にちの変更があった場合
			if (date != null)
				{
				if (date.getTime().getTime() == record.getCastedAt().getTime())
					{
					continue;
					}
				if ((date.get(Calendar.YEAR) != recordTime.get(Calendar.YEAR)) ||
						(date.get(Calendar.MONTH) != recordTime.get(Calendar.MONTH)) ||
						(date.get(Calendar.DATE) != recordTime.get(Calendar.DATE)))
					{
					if (tempList.size() > 0)
						{
						long id = this.addTmData(deviceId, yearDateModel, tempList);
						this.setIsolationData(id, deviceId, yearDateModel, tempList);
						tempList.clear();
						}
					}
				}
			date = recordTime;
			tempList.add(record);
			}

		if (tempList.size() > 0)
			{
			long id = this.addTmData(deviceId, yearDateModel, tempList);
			this.setIsolationData(id, deviceId, yearDateModel, tempList);
			}
		}

	private long addTmData(Long deviceId, YearDateModel yearDateModel, List<BaseDataDTO> tempList)
		{
		float min = tempList.get(0).getTemperature();
		float max = min;
		long prevTime = 0;
		float sum = min;
		int count = 1;
		float prevTemp = 0;
		for (BaseDataDTO data : tempList)
			{
			float temperature = data.getTemperature();
			// * 取得時刻
			long dataTime = data.getCastedAt().getTime();
			// * 時間差
			// * 1分の誤差を追加して１０分で割る
			dataTime += 60000;
			long diff = (prevTime != 0) ? (dataTime - prevTime) / 600000 : 1;
			sum += temperature;
			count++;
			if (diff > 1)
				{
				float halfTemp = (prevTemp + temperature) / 2;
				for (int i = 0; i < diff - 1; i++)
					{
					sum += halfTemp;
					count++;
					}
				}
			prevTemp = temperature;
			prevTime = dataTime;
			// * 最高気温・最低気温の設定
			if (temperature < min)
				min = temperature;
			else if (temperature > max)
				max = temperature;
			}
		// * 日付データテーブルに追加
		Calendar cal = Calendar.getInstance();
		cal.setTime(tempList.get(0).getCastedAt());
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		Ph2DeviceDayEntityExample exm = new Ph2DeviceDayEntityExample();
		exm.createCriteria().andDeviceIdEqualTo(deviceId)
			.andDateEqualTo(cal.getTime());
		Ph2DeviceDayEntity ddEntity = this.ph2DeviceDayMapper.selectByExample(exm).get(0);
		long id = ddEntity.getId();
		ddEntity.setHasReal(true);
		this.ph2DeviceDayMapper.updateByPrimaryKey(ddEntity);
		// * 日別基礎データテーブルに追加
		Ph2DailyBaseDataEntity entity = new Ph2DailyBaseDataEntity();
		entity.setDayId(id);
		entity.setAverage(sum / count);
		entity.setTm((min + max) / 2 - 10);
		entity.setCdd((min + max) / 2 - 9.18);
		this.ph2DailyBaseDataMapper.insert(entity);
		// * 作成された日付データテーブルのレコードIDを返却する。
		return id;
		}

	public void setIsolationData(Long id, Long deviceId, YearDateModel yearDateModel, List<BaseDataDTO> tmRecords)
		{
		List<BaseDataDTO> records = new ArrayList<>();
		for (BaseDataDTO data : tmRecords)
			{
			if (null != data.getInsolation())
				{
				records.add(data);
				}
			}
		if (0 == records.size())
			return;

		long prevTime = 0;
		double prevValue = records.get(0).getInsolation();
		prevValue = Formula.toPAR(prevValue);
		double sum = 0;
		for (BaseDataDTO data : records)
			{
			if (null == data.getInsolation())
				continue;
			double value = data.getInsolation();
			value = Formula.toPAR(value);
			// * 取得時刻
			long dataTime = data.getCastedAt().getTime();
			// * 時間差
			// * 1分の誤差を追加して１０分で割る
			dataTime += 60000;
			long diff = (prevTime != 0) ? (dataTime - prevTime) / 600000 : 1;
			sum += value;
			if (diff > 1)
				{
				double halfTemp = (prevValue + value) / 2;
				for (int i = 0; i < diff - 1; i++)
					{
					sum += halfTemp;
					}
				}
			prevValue = value;
			prevTime = dataTime;
			}

		Ph2DailyBaseDataEntityExample exm = new Ph2DailyBaseDataEntityExample();
		exm.createCriteria().andDayIdEqualTo(id);
		Ph2DailyBaseDataEntity entity = this.ph2DailyBaseDataMapper.selectByExample(exm).get(0);
		entity.setPar(sum);
		this.ph2DailyBaseDataMapper.updateByExample(entity, exm);
		}
	}
