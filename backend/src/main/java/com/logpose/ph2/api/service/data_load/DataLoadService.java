package com.logpose.ph2.api.service.data_load;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.logpose.ph2.api.bulk.domain.DeviceLogDomain;
import com.logpose.ph2.api.bulk.domain.DeviceStatusDomain;
import com.logpose.ph2.api.bulk.service.S0Initializer;
import com.logpose.ph2.api.bulk.service.S1SigFoxMessageService;
import com.logpose.ph2.api.bulk.service.S2DeviceDataInitService;
import com.logpose.ph2.api.bulk.service.S3RawDataLoaderService;
import com.logpose.ph2.api.bulk.service.S4HeadLineLoaderService;
import com.logpose.ph2.api.bulk.service.S5DeviceDayService;
import com.logpose.ph2.api.bulk.service.S6DailyBaseDataGeneratorService;
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
	private DeviceLogDomain deviceLogDomain;
	@Autowired
	private DeviceStatusDomain statusDomain;
	@Autowired
	private S0Initializer s0Initializer;
	@Autowired
	private S1SigFoxMessageService s1SigFoxMessageService;
	@Autowired
	private S2DeviceDataInitService s2deviceDataInitService;
	@Autowired
	private S3RawDataLoaderService s3RawDataLoaderService;
	@Autowired
	private S4HeadLineLoaderService s4HeadLineLoaderService;
	@Autowired
	private S5DeviceDayService s5deviceDayService;
	@Autowired
	private S6DailyBaseDataGeneratorService s6dailyBaseDataGeneratorService;
	@Autowired
	private S8ModelDataApplyrService s8modelDataApplyrService;

	// ===============================================
	// 公開関数群
	// ===============================================
	// --------------------------------------------------
	/**
	 * 全てのデバイスのメッセージテーブルからまだロードされていないデータ
	 * を各テーブルに加工して取り込む
	 * @param dto パラメータ
	 * @throws Exception 
	 */
	// --------------------------------------------------
	public void loadDevices(boolean mode, Ph2DevicesEntity device, Map<Long, Ph2DevicesEntity> finishList)
			throws Exception
		{
// * デバイスのログの初期化
		this.deviceLogDomain.cleanUp(device.getId());
		this.deviceLogDomain.log(device.getId(), getClass(), "デバイスのロックを開始します。");
// * 最新のみ更新の場合
		if (!mode)
			{
// * 終了リストに追加
			finishList.put(device.getId(), device);
// * 該当デバイスのロック
			if (false == this.statusDomain.setDataOnLoad(device))
				{
				LOG.info("デバイスデータがロック中なので、処理を終了します。:" + device.getId());
				this.deviceLogDomain.log(device.getId(), getClass(), "デバイスデータがロック中なので、処理を終了します。");
				return;
				}
			try
				{
// * コーディネーターの作成
				LoadCoordinator ldc = this.s0Initializer.initializeCoordinator(device, false);
// * コーディネーターを引数にデータロードを実行する
				this.loadDevice(ldc);
				}
			catch (Exception e)
				{
				throw e;
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
				this.deviceLogDomain.log(device.getId(), getClass(), "デバイスデータがロック中なので、処理を終了します。");
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
				
				this.deviceLogDomain.log(device.getId(), getClass(), "引継ぎ元のデバイス" + curr.getName() + "をロックします。");
				if (false == this.statusDomain.setDataOnLoad(curr))
					{
					LOG.info("デバイスデータがロック中なので、処理を終了します。:" + curr.getId());
					this.deviceLogDomain.log(device.getId(), getClass(), "引継ぎ元のデバイス" + curr.getName() + "がロック中なので、処理を終了します。");
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
			catch (Exception e)
				{
				throw e;
				}
			finally
				{
				this.deviceLogDomain.log(device.getId(), getClass(), "この処理中に発生した全てのデバイスのロックを解除します。");
// * 該当デバイスのロック解除
				this.statusDomain.unLockDevices(lockList);
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
		try
			{
			// * 必要な情報が揃っていなければ、処理をしないで終了する
			if (!ldc.isLoadable())
				{
				LOG.warn("ローディングに必要な情報はありません。:" + ldc.getDeviceId());
				this.deviceLogDomain.log(ldc.getDeviceId(), getClass(), "デバイス情報が未定義なので、処理を終了します。");
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
				// this.s7WeatherHistoryImport.importFromMaster(deviceDays);
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
