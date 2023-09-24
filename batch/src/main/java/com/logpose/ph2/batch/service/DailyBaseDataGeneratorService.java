package com.logpose.ph2.batch.service;

import java.util.Calendar;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.logpose.ph2.batch.dao.db.entity.Ph2SystemStatusEntity;
import com.logpose.ph2.batch.dao.db.mappers.Ph2DevicesMapper;
import com.logpose.ph2.batch.dao.db.mappers.joined.Ph2JoinedModelMapper;
import com.logpose.ph2.batch.domain.DailyBaseDataDomain;
import com.logpose.ph2.batch.domain.StatusDomain;
import com.logpose.ph2.batch.domain.YearDateModel;
import com.logpose.ph2.batch.dto.BaseDataDTO;
import com.logpose.ph2.batch.dto.ModelRefDataDTO;

@Service
public class DailyBaseDataGeneratorService
	{
	// ===============================================
	// クラスメンバー
	// ===============================================
	private static Logger LOG = LogManager
			.getLogger(DailyBaseDataGeneratorService.class);
	@Autowired
	private StatusDomain statusDomain;
	@Autowired
	private Ph2DevicesMapper ph2DeviceMapper;
	@Autowired
	private Ph2JoinedModelMapper ph2JoinedModelMapper;
	@Autowired
	private DailyBaseDataDomain dailyBaseDataDomain;

	// ===============================================
	// 公開関数群
	// ===============================================
	@Transactional(rollbackFor = Exception.class)
	public void doService()
		{
		// * システムステータステーブル(ph2_system_status)からbase_dataレコードの
		// * 本処理での最終処理レコードの受信時刻を得る。
		Ph2SystemStatusEntity status = this.statusDomain
				.getSystemStatus("base_data");
// this.dailyBaseDataDomain.delete(status.getLastTime());// TODO
// * デバイスリストを得る
		List<ModelRefDataDTO> devices = this.ph2DeviceMapper.selectDeviceList();
		// * データの取得日時の設定
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		// * デバイスごとに処理
		for (ModelRefDataDTO device : devices)
			{
			// * 基準日の設定
			YearDateModel yearDateModel = new YearDateModel();
			// * 圃場の統計開始日を設定する
			yearDateModel.setBase(device.getBaseDate());
			// * 日付テーブルの初期化
			this.dailyBaseDataDomain.initDeviceDay(
					device.getDeviceId(), yearDateModel, status.getLastTime());
			// * 温度を取得する
			List<BaseDataDTO> tmRecords = this.ph2JoinedModelMapper.getBaseData(
					device.getDeviceId(),
					status.getLastTime(), cal.getTime());
			if (tmRecords.size() != 0)
				{
				// * DailyBaseDataへの登録を行う
				this.dailyBaseDataDomain.createTMValues(device.getDeviceId(),
						yearDateModel, tmRecords);
				}
			}
		// * base_dataレコードの最終時刻を設定する
		status.setLastTime(cal.getTime());
		this.statusDomain.update(status);
		}
	}
