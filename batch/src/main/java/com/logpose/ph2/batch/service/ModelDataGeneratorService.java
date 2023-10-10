package com.logpose.ph2.batch.service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.logpose.ph2.batch.alg.DeviceDayAlgorithm;
import com.logpose.ph2.batch.domain.StatusDomain;
import com.logpose.ph2.common.dao.db.entity.Ph2DeviceDayEntity;
import com.logpose.ph2.common.dao.db.entity.Ph2DeviceDayEntityExample;
import com.logpose.ph2.common.dao.db.entity.Ph2ModelDataEntity;
import com.logpose.ph2.common.dao.db.entity.Ph2SystemStatusEntity;
import com.logpose.ph2.common.dao.db.mappers.Ph2DailyBaseDataMapper;
import com.logpose.ph2.common.dao.db.mappers.Ph2DeviceDayMapper;
import com.logpose.ph2.common.dao.db.mappers.Ph2DevicesMapper;
import com.logpose.ph2.common.dao.db.mappers.Ph2ModelDataMapper;
import com.logpose.ph2.common.dao.db.mappers.joined.Ph2JoinedModelMapper;
import com.logpose.ph2.common.dto.ModelRefDataDTO;

@Service
public class ModelDataGeneratorService
	{
	// ===============================================
	// クラスメンバー
	// ===============================================
	private static Logger LOG = LogManager
			.getLogger(ModelDataGeneratorService.class);
	@Autowired
	private Ph2DevicesMapper ph2DeviceMapper;
	@Autowired
	private Ph2DeviceDayMapper ph2DeviceDayMapper;
	@Autowired
	private Ph2DailyBaseDataMapper ph2DailyBaseDataMapper;
	@Autowired
	private Ph2JoinedModelMapper ph2JoinedModelMapper;
	@Autowired
	private Ph2ModelDataMapper ph2ModelDataMapper;
	@Autowired
	private DeviceDayAlgorithm deviceDayAlgorithm;
	@Autowired
	private StatusDomain statusDomain;

	// ===============================================
	// 公開関数群
	// ===============================================
	@Transactional(rollbackFor = Exception.class)
	public List<Long> doService()
		{
		List<Long> idist = new ArrayList<>();
		// * システムステータステーブル(ph2_system_status)からmodel_dataレコードの
		// * 本処理での最終処理レコードの受信時刻を得る。
		Ph2SystemStatusEntity status = this.statusDomain
				.getSystemStatus("model_data");
		Date takenDataTime = status.getLastTime();
		// * 今年
		@SuppressWarnings("deprecation")
		int year = new Date().getYear() + 1900;

		// * デバイスリストを得る
		List<ModelRefDataDTO> devices = this.ph2DeviceMapper.selectDeviceList();
		// * デバイスごとに処理
		for (ModelRefDataDTO device : devices)
			{
			// * デバイスの基準日を得る
			Calendar baseDate = this.deviceDayAlgorithm
					.getBaseDate(device.getBaseDate());
			// * 未処理の年度から今年までの処理を行う
			for (short i = (short) (takenDataTime.getYear()+1900); i <= year; i++)
				{
				// * 年の設定
				baseDate.set(Calendar.YEAR, i);
				// * 時刻zero設定
				this.deviceDayAlgorithm.setTimeZero(baseDate);
				// * ディリーデータがあるかチェック
				long dailyCount = this.ph2DailyBaseDataMapper
						.countEffectiveData(
								device.getDeviceId(),
								baseDate.getTime());
				if (dailyCount > 0)
					{
					// * モデルデータがあるかチェック
					long modelCount = this.ph2JoinedModelMapper
							.countEffectiveData(device.getDeviceId(), baseDate.getTime());
					if (0 == modelCount)
						{
						Ph2DeviceDayEntityExample exm = new Ph2DeviceDayEntityExample();
						exm.createCriteria().andDeviceIdEqualTo(device.getDeviceId()).andYearEqualTo(i);
						List<Ph2DeviceDayEntity> deviceDays = this.ph2DeviceDayMapper.selectByExample(exm);
						for(Ph2DeviceDayEntity record :deviceDays)
							{
							Ph2ModelDataEntity entity = new Ph2ModelDataEntity();
							entity.setDayId(record.getId());
							this.ph2ModelDataMapper.insert(entity);
							}
						
						}
					idist.add(device.getDeviceId());
					}
				}
			}
		status.setLastTime(new Date());
		this.statusDomain.update(status);
		return idist;
		}
	}
