package com.logpose.ph2.api.service;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.logpose.ph2.api.bulk.domain.DeviceStatusDomain;
import com.logpose.ph2.api.bulk.service.S0Initializer;
import com.logpose.ph2.api.bulk.service.S1SigFoxMessageService;
import com.logpose.ph2.api.bulk.service.S2DeviceDataInitService;
import com.logpose.ph2.api.bulk.service.S3RawDataLoaderService;
import com.logpose.ph2.api.bulk.service.S4HeadLineLoaderService;
import com.logpose.ph2.api.bulk.service.S5DeviceDayService;
import com.logpose.ph2.api.bulk.service.S6DailyBaseDataGeneratorService;
import com.logpose.ph2.api.bulk.service.S7ModelDataApplyrService;
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
	private DeviceStatusDomain statusDomain;
	@Autowired
	S0Initializer s0Initializer;
	@Autowired
	S1SigFoxMessageService s1SigFoxMessageService;
	@Autowired
	S2DeviceDataInitService s2deviceDataInitService;
	@Autowired
	S3RawDataLoaderService s3RawDataLoaderService;
	@Autowired
	S4HeadLineLoaderService s4HeadLineLoaderService;
	@Autowired
	S5DeviceDayService s5deviceDayService;
	@Autowired
	S6DailyBaseDataGeneratorService s6dailyBaseDataGeneratorService;
	@Autowired
	S7ModelDataApplyrService s7modelDataApplyrService;

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
		for (Ph2DevicesEnyity device : devices)
			{
			try
				{
// * コーディネーターを生成する
				LoadCoordinator ldc = this.s0Initializer.initializeCoordinator(device, false);
				// * コーディネーターを引数にデータロードを実行する
				this.loadDevice(ldc);
				}
			catch (Exception e)
				{
				LOG.error(device.getId() + "のロードに失敗しました。");
				e.printStackTrace();
				}
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
			try
				{
// * コーディネーターを生成する
				LoadCoordinator ldc = this.s0Initializer.initializeCoordinator(device, true);
// * コーディネーターを引数にデータロードを実行する
				this.loadDevice(ldc);
				}
			catch (Exception e)
				{
				LOG.error(device.getId() + "のロードに失敗しました。");
				e.printStackTrace();
				}
			}
		}

	// --------------------------------------------------
	/**
	 * 全てのデバイスのメッセージテーブルからまだロードされていないデータ
	 * を各テーブルに加工して取り込む
	 * @param dto パラメータ
	 * @throws Exception 
	 */
	// --------------------------------------------------
	public void createData(DataLoadDTO dto) throws Exception
		{
		List<Ph2DevicesEnyity> targets = new ArrayList<>();
		
// * 対象となるデバイスデータを取得する
		Long deviceId = dto.getDeviceId();
		while (null != deviceId)
			{
			Ph2DevicesEnyity device = this.s0Initializer.getDeviceInfo(deviceId);
			targets.add(0, device);
			deviceId = device.getPreviousDeviceId();
			}

// * 各対象デバイスに対して処理を実行する
		for (Ph2DevicesEnyity target : targets)
			{
			try
				{
// * コーディネーターを生成する
				LoadCoordinator ldc = this.s0Initializer.initializeCoordinator(target, true);
// * コーディネーターを引数にデータロードを実行する
				this.loadDevice(ldc);
				}
			catch (Exception e)
				{
				LOG.error(target.getId() + "のロードに失敗しました。");
				throw e;
				}
			}
		}

	// ===============================================
	// 保護関数群
	// ===============================================
	// --------------------------------------------------
	/**
	 * 指定されたデバイスのメッセージテーブルからまだロードされていないデータ
	 * を各テーブルに加工して取り込む
	 * @param ldc
	 * @throws Exception
	 */
	// --------------------------------------------------
	private void loadDevice(LoadCoordinator ldc) throws Exception
		{
// * 必要な情報が揃っていなければ、処理をしないで終了する
		if (!ldc.isLoadable())
			{
			LOG.warn("ローディングに必要な情報はありません。:" + ldc.getDeviceId());
			return;
			}
		Long deviceId = ldc.getDeviceId();

		LOG.info("デバイスデータのローディングを開始します。:" + deviceId);
		if (false == this.statusDomain.setDataOnLoad(deviceId))
			{
			LOG.info("デバイスデータがロック中なので、処理を終了します。:" + deviceId);
			return;
			}
		try
			{
// * 指定されたデバイスに対してSigFoxのデータを取り込む
			try
				{
				LOG.info("Sigfox データのローディングの開始:" + deviceId);
				this.s1SigFoxMessageService.doService(ldc.getDevice());
				}
			catch (Exception e)
				{
				LOG.error("Sigfox データのローディングに失敗しました。:" + deviceId);
				throw e;
				}
// * 全データ更新モードの場合、テーブルを初期化する
			if (ldc.isAll())
				{
				try
					{
					LOG.info("データ初期化の開始:" + deviceId);
					this.s2deviceDataInitService.deleteTables(deviceId, null);
					this.statusDomain.setDataInitialized(deviceId);
					LOG.info("データ初期化の終了:" + deviceId);
					}
				catch (Exception e)
					{
					LOG.error("データ初期化に失敗しました。:" + deviceId);
					throw e;
					}
				}
// * メッセージテーブルから基本情報のDBへのロードを実行する
			LOG.info("生データ生成の開始:" + deviceId);
			Date lastUpdated = this.s3RawDataLoaderService.loadMessages(ldc);
			this.statusDomain.setRawDataLoaded(deviceId);
			LOG.info("生データ生成の終了:" + deviceId);

// * メッセージテーブルから基本情報のDBへのロードを実行する
			if (lastUpdated != null)
				{
				LOG.info("ヘッドラインデータ生成の開始:" + deviceId);
				this.s4HeadLineLoaderService.createHealines(deviceId, lastUpdated);
				LOG.info("ヘッドラインデータ生成の終了:" + deviceId);
				}
// * 日付をまたがった場合、以下の処理を行う
			List<Ph2DeviceDayEntity> deviceDays = this.s5deviceDayService.doService(ldc);
			if ((null != deviceDays) && (deviceDays.size() > 0))
				{
				this.s6dailyBaseDataGeneratorService.doService(ldc, deviceDays);
				this.s7modelDataApplyrService.doService(deviceId, deviceDays);
				}
			LOG.info("デバイスデータのローディングが終了しました。");
			}
		finally
			{
			this.statusDomain.setDataNotLoading(deviceId);
			}
		}
	}
