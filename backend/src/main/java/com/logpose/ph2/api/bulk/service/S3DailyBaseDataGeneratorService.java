package com.logpose.ph2.api.bulk.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.logpose.ph2.api.algorythm.DeviceDayAlgorithm;
import com.logpose.ph2.api.bulk.dto.BaseDataDTO;
import com.logpose.ph2.batch.dao.db.entity.Ph2DailyBaseDataEntity;
import com.logpose.ph2.batch.dao.db.entity.Ph2DailyBaseDataEntityExample;
import com.logpose.ph2.batch.dao.db.entity.Ph2DeviceDayEntity;
import com.logpose.ph2.batch.dao.db.mappers.Ph2DailyBaseDataMapper;
import com.logpose.ph2.batch.dao.db.mappers.Ph2DeviceDayMapper;
import com.logpose.ph2.batch.dao.db.mappers.joined.Ph2JoinedModelMapper;
import com.logpose.ph2.batch.formula.Formula;

@Service
public class S3DailyBaseDataGeneratorService
	{
	// ===============================================
	// クラスメンバー
	// ===============================================
	private static Logger LOG = LogManager
			.getLogger(S3DailyBaseDataGeneratorService.class);
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
	public void doService(List<Ph2DeviceDayEntity> deviceDays, Date startDate)
		{
		LOG.info("日ベースのデータ作成開始");
		for (Ph2DeviceDayEntity deviceDay : deviceDays)
			{
			List<BaseDataDTO> tmRecords = this.ph2JoinedModelMapper.getBaseData(
					deviceDay.getDeviceId(), deviceDay.getDate(),
					this.deviceDayAlgorithm.getNextDayZeroHour(deviceDay.getDate()));
			if (tmRecords.size() > 0)
				{
				this.addTmData(deviceDay, tmRecords);
				this.setIsolationData(deviceDay, tmRecords);
				deviceDay.setHasReal(true);
				this.ph2DeviceDayMapper.updateByPrimaryKey(deviceDay);
				}
			}
		LOG.info("日ベースのデータ作成終了");
		}

	private void addTmData(Ph2DeviceDayEntity device, List<BaseDataDTO> tempList)
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
		entity.setCdd((min + max) / 2 - 9.18);
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
		exm.createCriteria().andDayIdEqualTo(device.getId());
		Ph2DailyBaseDataEntity entity = this.ph2DailyBaseDataMapper.selectByExample(exm).get(0);
		entity.setPar(sum);
		this.ph2DailyBaseDataMapper.updateByExample(entity, exm);
		}
	}
