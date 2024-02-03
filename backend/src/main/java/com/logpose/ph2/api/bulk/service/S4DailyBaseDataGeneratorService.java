package com.logpose.ph2.api.bulk.service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.logpose.ph2.api.algorythm.DeviceDayAlgorithm;
import com.logpose.ph2.api.bulk.vo.LoadCoordinator;
import com.logpose.ph2.api.dao.db.entity.Ph2DailyBaseDataEntity;
import com.logpose.ph2.api.dao.db.entity.Ph2DailyBaseDataEntityExample;
import com.logpose.ph2.api.dao.db.entity.Ph2DeviceDayEntity;
import com.logpose.ph2.api.dao.db.entity.Ph2DeviceDayEntityExample;
import com.logpose.ph2.api.dao.db.entity.Ph2DeviceDayEntityExample.Criteria;
import com.logpose.ph2.api.dao.db.mappers.Ph2DailyBaseDataMapper;
import com.logpose.ph2.api.dao.db.mappers.Ph2DeviceDayMapper;
import com.logpose.ph2.api.dao.db.mappers.joined.Ph2JoinedModelMapper;
import com.logpose.ph2.api.dto.BaseDataDTO;
import com.logpose.ph2.api.dto.SingleDoubleValueDTO;

@Service
public class S4DailyBaseDataGeneratorService
	{
	// ===============================================
	// クラスメンバー
	// ===============================================
	private static Logger LOG = LogManager
			.getLogger(S4DailyBaseDataGeneratorService.class);
	@Autowired
	private Ph2JoinedModelMapper ph2JoinedModelMapper;
	@Autowired
	private DeviceDayAlgorithm deviceDayAlgorithm;
	@Autowired
	private Ph2DeviceDayMapper ph2DeviceDayMapper;
	@Autowired
	Ph2DailyBaseDataMapper ph2DailyBaseDataMapper;

	// ===============================================
	// 公開関数群
	// ===============================================
	@Transactional(rollbackFor = Exception.class)
	public List<Ph2DeviceDayEntity> doService(LoadCoordinator ldc,
			List<Ph2DeviceDayEntity> deviceDays)
		{
		LOG.info("日ベースのデータ作成開始");
		SingleDoubleValueDTO prevCdd = new SingleDoubleValueDTO();
		prevCdd.setValue((double) 0);
		for (Ph2DeviceDayEntity deviceDay : deviceDays)
			{
// * その日の１０分単位のデータで気温と光合成有効放射束密度を取得する
			List<BaseDataDTO> tmRecords = this.ph2JoinedModelMapper.getBaseData(
					deviceDay.getDeviceId(), deviceDay.getDate(),
					this.deviceDayAlgorithm.getNextDayZeroHour(deviceDay.getDate()));
// * その日のデータが存在する場合
			if (tmRecords.size() > 0)
				{
				this.addTmData(deviceDay, tmRecords, prevCdd);
				this.setIsolationData(deviceDay, tmRecords);
				deviceDay.setHasReal(true);
				this.ph2DeviceDayMapper.updateByPrimaryKey(deviceDay);
				}
			}
		LOG.info("日ベースのデータ作成終了");
// * 指定デバイスの実値を持つ最初の1日めのデータを取得する設定を行う。
		Ph2DeviceDayEntityExample exm = new Ph2DeviceDayEntityExample();
		Criteria criteria = exm.createCriteria().andDeviceIdEqualTo(ldc.getDeviceId())
				.andLapseDayEqualTo((short) 1).andHasRealEqualTo(true);
// * 日付の指定がある場合
		if (null != ldc.getLastHadledDate())
			{
			Calendar cal = Calendar.getInstance();
			cal.setTime(ldc.getLastHadledDate());
			cal.add(Calendar.YEAR, -1);
			criteria.andDateGreaterThan(cal.getTime());
			}
		exm.setOrderByClause("date  asc");
		return this.ph2DeviceDayMapper.selectByExample(exm);
		}

	private void addTmData(Ph2DeviceDayEntity device, List<BaseDataDTO> tempList,
			SingleDoubleValueDTO prevCdd)
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
		// * 日別基礎データテーブルに追加
		Ph2DailyBaseDataEntityExample exm = new Ph2DailyBaseDataEntityExample();
		exm.createCriteria().andDayIdEqualTo(device.getId());
		Ph2DailyBaseDataEntity entity;
		List<Ph2DailyBaseDataEntity> entities = this.ph2DailyBaseDataMapper.selectByExample(exm);
		if (entities.size() > 0)
			{
			entity = entities.get(0);
			}
		else
			{
			entity = new Ph2DailyBaseDataEntity();
			}
		entity.setDayId(device.getId());
		entity.setAverage(sum / count);
		entity.setTm((min + max) / 2 - 10);
		double cdd = prevCdd.getValue() + ((min + max) / 2 - 9.18);
		prevCdd.setValue(cdd);
		entity.setCdd(cdd);
		if (entities.size() > 0)
			{
			this.ph2DailyBaseDataMapper.updateByExample(entity, exm);
			}
		else
			{
			this.ph2DailyBaseDataMapper.insert(entity);
			}
		}

	public void setIsolationData(Ph2DeviceDayEntity device, List<BaseDataDTO> tmRecords)
		{
// * PAR値（光合成有効放射束密度ベース）のみのリスト作成する
// * --- BEGIN ---
		List<BaseDataDTO> par_records = new ArrayList<>();
		for (BaseDataDTO data : tmRecords)
			{
			if (null != data.getInsolation()) par_records.add(data);
			}
		if (0 == par_records.size()) return;
// * --- END ---

		double sum = 0;   // その日のPAR合計値
		long sun_time = 0;   // 日射時間
		Double prev_value = null;   // 直前のPAR値
		Date prev_time = null;   // 直前の時間
		for (BaseDataDTO data : par_records)
			{
// * 光合成有効放射束密度の取得
			double value = data.getInsolation();
// * 初期値の場合はその設定をする
			if (null == prev_value)
				{
				prev_value = value;
				prev_time = data.getCastedAt();
				continue;
				}
// * 前の値が０の場合、前の値に対するデータの追加は行わない
			if (0 != prev_value.doubleValue())
				{
// * valueが取得された時間との差(秒)
				long diff_time = (data.getCastedAt().getTime() - prev_time.getTime()) / 1000;
// * valueが０の場合のPARの時間差
				if ((0 == value) && (diff_time > 600)) diff_time = 600;
// * PARの算出
				double par = prev_value * diff_time;
				sum += par;
// * 日射時間の算出
				sun_time += diff_time;
				}
			prev_value = value;
			prev_time = data.getCastedAt();
			}
// * DBにPARと日射時間を設定する
		Ph2DailyBaseDataEntityExample exm = new Ph2DailyBaseDataEntityExample();
		exm.createCriteria().andDayIdEqualTo(device.getId());
		Ph2DailyBaseDataEntity entity = this.ph2DailyBaseDataMapper.selectByExample(exm).get(0);
		entity.setPar(sum);
		entity.setSunTime(sun_time);
		this.ph2DailyBaseDataMapper.updateByExample(entity, exm);
		}
	}
