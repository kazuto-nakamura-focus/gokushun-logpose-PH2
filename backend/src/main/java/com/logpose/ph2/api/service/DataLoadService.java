package com.logpose.ph2.api.service;

import java.io.IOException;
import java.text.ParseException;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.logpose.ph2.api.bulk.service.S0Initializer;
import com.logpose.ph2.api.bulk.service.S1DeviceDataLoaderService;
import com.logpose.ph2.api.bulk.service.S2DeviceDayService;
import com.logpose.ph2.api.bulk.service.S3DailyBaseDataGeneratorService;
import com.logpose.ph2.api.bulk.service.S4ModelDataApplyrService;
import com.logpose.ph2.api.bulk.service.SigFoxMessageService;
import com.logpose.ph2.api.bulk.vo.LoadCoordinator;
import com.logpose.ph2.api.controller.dto.DataLoadDTO;
import com.logpose.ph2.api.dao.db.entity.Ph2DeviceDayEntity;
import com.logpose.ph2.api.dao.db.entity.Ph2DevicesEnyity;

/**
 * データロードを実行するサービス
 */
@Service
public class DataLoadService
	{
	// ===============================================
	// クラスメンバー
	// ===============================================
	private static Logger LOG = LogManager.getLogger(DataLoadService.class);

	@Autowired
	S0Initializer s0Initializer;
	@Autowired
	SigFoxMessageService sigFoxMessageService;
	@Autowired
	S1DeviceDataLoaderService s1deviceDataLoaderService;
	@Autowired
	S2DeviceDayService s2deviceDayService;
	@Autowired
	S3DailyBaseDataGeneratorService s3dailyBaseDataGeneratorService;
	@Autowired
	S4ModelDataApplyrService s4modelDataApplyrService;

	// ===============================================
	// 公開関数群
	// ===============================================
	// --------------------------------------------------
	/**
	 * 全てのデバイスのメッセージテーブルからまだロードされていないデータ
	 * を各テーブルに加工して取り込む
	 * @throws IOException
	 * @throws ParseException
	 */
	// --------------------------------------------------
	public void updateData() throws IOException, ParseException
		{
// * 全てのデバイス情報を取得し、各デバイスに対して処理を行う
		List<Ph2DevicesEnyity> devices = this.s0Initializer.getDeviceAllInfo();
		this.sigFoxMessageService.doService(devices);	
		for (Ph2DevicesEnyity device : devices)
			{
// * コーディネーターを生成する
			LoadCoordinator ldc = this.s0Initializer.initializeCoordinator(device,
					false,
					null);
			// * コーディネーターを引数にデータロードを実行する
			this.loadDevice(ldc);
			}
		}

	// --------------------------------------------------
	/**
	 * 全てのデバイスのメッセージテーブルからまだロードされていないデータ
	 * を各テーブルに加工して取り込む
	 * @param dto パラメータ
	 * @throws IOException
	 * @throws ParseException
	 */
	// --------------------------------------------------
	public void createAllData(DataLoadDTO dto) throws IOException, ParseException
		{
// * 全てのデバイス情報を取得し、各デバイスに対して処理を行う
		List<Ph2DevicesEnyity> devices = this.s0Initializer.getDeviceAllInfo();
		for (Ph2DevicesEnyity device : devices)
			{
// * コーディネーターを生成する
			LoadCoordinator ldc = this.s0Initializer.initializeCoordinator(device,
					dto.getIsAll(),
					dto.getStartDate());
// * コーディネーターを引数にデータロードを実行する
			this.loadDevice(ldc);
			}
		}

	// --------------------------------------------------
	/**
	 * 全てのデバイスのメッセージテーブルからまだロードされていないデータ
	 * を各テーブルに加工して取り込む
	 * @param dto パラメータ
	 * @throws IOException
	 * @throws ParseException
	 */
	// --------------------------------------------------
	public void createData(DataLoadDTO dto) throws IOException, ParseException
		{
// * コーディネーターを生成する
		LoadCoordinator ldc = this.s0Initializer.initializeCoordinator(dto.getDeviceId(),
				dto.getIsAll(),
				dto.getStartDate());
// * コーディネーターを引数にデータロードを実行する
		this.loadDevice(ldc);
		}

	// ===============================================
	// 保護関数群
	// ===============================================
	// --------------------------------------------------
	/**
	 * 指定されたデバイスのメッセージテーブルからまだロードされていないデータ
	 * を各テーブルに加工して取り込む
	 * @param ldc
	 * @throws IOException
	 * @throws ParseException
	 */
	// --------------------------------------------------
	private void loadDevice(LoadCoordinator ldc) throws IOException, ParseException
		{
// * 必要な情報が揃っていなければ、処理をしないで終了する
		if (!ldc.isLoadable())
			{
			LOG.warn("ローディングに必要な情報はありません。:" + ldc.getDeviceId());
			return;
			}

		LOG.info("デバイスデータのローディングを開始します。:" + ldc.getDeviceId());
// * メッセージテーブルから基本情報のDBへのロードを実行する
		s1deviceDataLoaderService.loadMessages(ldc);
// * 日付をまたがった場合、以下の処理を行う
		List<Ph2DeviceDayEntity> deviceDays = this.s2deviceDayService.initDeviceDay(ldc);
		if (deviceDays.size() > 0)
			{
			deviceDays = this.s3dailyBaseDataGeneratorService.doService(ldc, deviceDays);
			this.s4modelDataApplyrService.doService(ldc.getDeviceId(), deviceDays);
			}
		LOG.info("デバイスデータのローディングが終了しました。");
		}
	}