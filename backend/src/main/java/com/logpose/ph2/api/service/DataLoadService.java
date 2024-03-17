package com.logpose.ph2.api.service;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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
import com.logpose.ph2.api.bulk.service.S7WeatherHistoryImport;
import com.logpose.ph2.api.bulk.service.S8ModelDataApplyrService;
import com.logpose.ph2.api.bulk.vo.LoadCoordinator;
import com.logpose.ph2.api.dao.db.entity.Ph2DeviceDayEntity;
import com.logpose.ph2.api.dao.db.entity.Ph2DevicesEntity;
import com.logpose.ph2.api.dto.element.ObjectStatus;

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
	S7WeatherHistoryImport s7WeatherHistoryImport;
	@Autowired
	S8ModelDataApplyrService s8modelDataApplyrService;

	// ===============================================
	// 公開関数群
	// ===============================================
	// --------------------------------------------------
	/**
	 * 全てのデバイスのデータを最新の状態に更新する
	 * @throws IOException
	 * @throws ParseException
	 */
	// --------------------------------------------------
	public void updateData() throws IOException, ParseException
		{
// * 処理済みハッシュリスト
		Map<Long, Ph2DevicesEntity> finishList = new HashMap<>();
// * 全てのデバイス情報を取得し、各デバイスに対して処理を行う
		List<Ph2DevicesEntity> devices = this.statusDomain.selectAll();
		for (Ph2DevicesEntity device : devices)
			{
			try
				{
// * 既に処理済みの場合、終了
				if (finishList.containsKey(device.getId())) continue;
// * 全ロードモードかどうか
				boolean isAll = this.statusDomain.isAll(device);
// * データロードを実行する
				this.loadDevices(isAll, device, finishList);
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
	 * @throws IOException
	 * @throws ParseException
	 */
	// --------------------------------------------------
	public List<Ph2DevicesEntity> getSchedule()
		{
// * 処理済みハッシュリスト
		Map<Long, Ph2DevicesEntity> finishList = new LinkedHashMap<>();
		Map<Long, Ph2DevicesEntity> result = new LinkedHashMap<>();

// * 全てのデバイス情報を取得し、各デバイスに対して処理を行う
		List<Ph2DevicesEntity> devices = this.statusDomain.selectAll();

// * 現在の時刻を取得する
		for (Ph2DevicesEntity device : devices)
			{
// * 既に処理済みの場合、終了
			if (finishList.containsKey(device.getId())) continue;
// * リストに追加
			finishList.put(device.getId(), device);
			result.put(device.getId(), device);

// * 引継ぎ先デバイスがあれば、リストから削除する
			Long prevId = device.getPreviousDeviceId();
			while (null != prevId)
				{
				Ph2DevicesEntity curr = this.s0Initializer.getDeviceInfo(prevId);
				if (!finishList.containsKey(curr.getId()))
					{
					finishList.put(curr.getId(), device);
					}
				result.remove(curr.getId());
				prevId = curr.getPreviousDeviceId();
				}
			}
		return new ArrayList<>(result.values());
		}

	// --------------------------------------------------
	/**
	 * 全てのデバイスのメッセージテーブルからまだロードされていないデータ
	 * を各テーブルに加工して取り込む
	 * @throws IOException
	 * @throws ParseException
	 */
	// --------------------------------------------------
	public void createAllData() throws IOException, ParseException
		{
// * 処理済みハッシュリスト
		Map<Long, Ph2DevicesEntity> finishList = new HashMap<>();
// * 全てのデバイス情報を取得し、各デバイスに対して処理を行う
		List<Ph2DevicesEntity> devices = this.statusDomain.selectAll();
// * 現在の時刻を取得する
		for (Ph2DevicesEntity device : devices)
			{
			try
				{
// * 既に処理済みの場合、終了
				if (finishList.containsKey(device.getId())) continue;
// * コーディネーターを引数にデータロードを実行する
				this.loadDevices(true, device, finishList);
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
	 * 指定されたデバイスIDに対してデータローディング処理を行う
	 * @param deviceId
	 * @throws Exception 
	 */
	// --------------------------------------------------
	public List<ObjectStatus> createData(Long deviceId) throws Exception
		{
// * 処理済みハッシュリスト
		Map<Long, Ph2DevicesEntity> finishList = new HashMap<>();
		Ph2DevicesEntity device = this.s0Initializer.getDeviceInfo(deviceId);
		try
			{
// * コーディネーターを引数にデータロードを実行する
			this.loadDevices(true, device, finishList);
			List<Ph2DevicesEntity> devices = new ArrayList<>(finishList.values());
			return this.statusDomain.getStatusList(devices);
			}
		catch (Exception e)
			{
			LOG.error(device.getId() + "のロードに失敗しました。");
			throw e;
			}
		}

	// ===============================================
	// 保護関数群
	// ===============================================
	// --------------------------------------------------
	/**
	 * 全てのデバイスのメッセージテーブルからまだロードされていないデータ
	 * を各テーブルに加工して取り込む
	 * @param dto パラメータ
	 * @throws Exception 
	 */
	// --------------------------------------------------
	private void loadDevices(boolean mode, Ph2DevicesEntity device, Map<Long, Ph2DevicesEntity> finishList)
			throws Exception
		{
// * 最新のみ更新の場合
		if (!mode)
			{
// * 終了リストに追加
			finishList.put(device.getId(), device);
// * 該当デバイスのロック
			if (false == this.statusDomain.setDataOnLoad(device))
				{
				LOG.info("デバイスデータがロック中なので、処理を終了します。:" + device.getId());
				return;
				}
			try
				{
// * コーディネーターの作成
				LoadCoordinator ldc = this.s0Initializer.initializeCoordinator(device, false);
// * コーディネーターを引数にデータロードを実行する
				this.loadDevice(ldc);
				}
			finally
				{
// * 該当デバイスのロック解除
				this.statusDomain.setDataNotLoading(device);
				}
			}
// * 全更新の場合
		else
			{
			// * 該当デバイスのロック
			if (false == this.statusDomain.setDataOnLoad(device))
				{
				LOG.info("デバイスデータがロック中なので、処理を終了します。:" + device.getId());
				return;
				}

			List<Ph2DevicesEntity> targets = new ArrayList<>();
			List<Long> lockList = new ArrayList<>();
			lockList.add(device.getId());
			targets.add(0, device);
			finishList.put(device.getId(), device);
// * 対象となるデバイスデータを取得する
			Long prevId = device.getPreviousDeviceId();
			while (null != prevId)
				{
				Ph2DevicesEntity curr = this.s0Initializer.getDeviceInfo(prevId);
				if (false == this.statusDomain.setDataOnLoad(curr))
					{
					LOG.info("デバイスデータがロック中なので、処理を終了します。:" + curr.getId());
					return;
					}
				// まだ実行されていなければ、ロックリストと実行リストに入れる
				if (!finishList.containsKey(curr.getId()))
					{
					lockList.add(curr.getId());
					targets.add(0, curr);
					finishList.put(curr.getId(), curr);
					}
				else
					{
					lockList.add(curr.getId());
					}
				prevId = curr.getPreviousDeviceId();
				}
			try
				{
// * 各対象デバイスに対して処理を実行する
				for (Ph2DevicesEntity target : targets)
					{
// * コーディネーターを生成する
					LoadCoordinator ldc = this.s0Initializer.initializeCoordinator(target, true);
// * コーディネーターを引数にデータロードを実行する
					this.loadDevice(ldc);
// * 全ロードモードになっていれば、解除する
					if (this.statusDomain.isAll(target))
						{
						this.statusDomain.setUpdateNotAll(target);
						}
					}
				}
			finally
				{
// * 該当デバイスのロック解除
				this.statusDomain.unLockDevices(lockList);
				}
			}
		}

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
		try
			{
			// * 必要な情報が揃っていなければ、処理をしないで終了する
			if (!ldc.isLoadable())
				{
				LOG.warn("ローディングに必要な情報はありません。:" + ldc.getDeviceId());
				return;
				}
			Long deviceId = ldc.getDeviceId();

			LOG.info("デバイスデータのローディングを開始します。:" + deviceId);

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
					this.statusDomain.setDataInitialized(ldc.getDevice());
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
			this.statusDomain.setRawDataLoaded(ldc.getDevice());
			LOG.info("生データ生成の終了:" + deviceId);

// * メッセージテーブルから基本情報のDBへのロードを実行する
			if (lastUpdated != null)
				{
				LOG.info("ヘッドラインデータ生成の開始:" + deviceId);
				this.s4HeadLineLoaderService.createHealines(ldc.getDevice(), lastUpdated);
				LOG.info("ヘッドラインデータ生成の終了:" + deviceId);
				}
// * 日付をまたがった場合、以下の処理を行う
			List<Ph2DeviceDayEntity> deviceDays = this.s5deviceDayService.doService(ldc);
			if ((null != deviceDays) && (deviceDays.size() > 0))
				{
				this.statusDomain.setUpdateNotModel(ldc.getDevice());
				this.s6dailyBaseDataGeneratorService.doService(ldc, deviceDays);

				LOG.info("デバイスデータの欠損をマスターデータから補完します。:" + deviceId);
				//this.s7WeatherHistoryImport.importFromMaster(deviceDays);
				LOG.info("デバイスデータの欠損のマスターデータからの補完完了：" + deviceId);

				this.s8modelDataApplyrService.doService(deviceId, deviceDays);
				this.statusDomain.setModelDataCreated(ldc.getDevice());
				}
			LOG.info("デバイスデータのローディングが終了しました。:" + deviceId);
			}
		catch (Exception e)
			{
			e.printStackTrace();
			LOG.info("デバイスデータのローディングに失敗しました。:" + ldc.getDeviceId());
			}
		}

	// --------------------------------------------------------
	/**
	 * デバイスのロード情報を得る
	 * @param date 取得するステータスデータの日付
	 * @return List<ObjectStatus>
	 */
	// --------------------------------------------------------
	public List<ObjectStatus> getInfo(Date date)
		{
		return this.statusDomain.getAllStatusList(date);
		}
	}
