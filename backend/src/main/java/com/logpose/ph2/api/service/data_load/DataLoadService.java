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
		this.deviceLogDomain.log(LOG, device.getId(), getClass(), "デバイスのロックを開始します。");
// * 最新のみ更新の場合
		if (!mode)
			{
// * 終了リストに追加
			finishList.put(device.getId(), device);
// * 該当デバイスのロック
			if (false == this.statusDomain.setDataOnLoad(device))
				{
				this.deviceLogDomain.log(LOG, device.getId(), getClass(), "デバイスデータがロック中なので、処理を終了します。");
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
				this.deviceLogDomain.log(LOG, device.getId(), getClass(), "デバイスデータがロック中なので、処理を終了します。");
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

				this.deviceLogDomain.log(LOG, device.getId(), getClass(), "引継ぎ元のデバイス" + curr.getName() + "をロックします。");
				if (false == this.statusDomain.setDataOnLoad(curr))
					{
					this.deviceLogDomain.log(LOG, device.getId(), getClass(),
							"引継ぎ元のデバイス" + curr.getId() + "がロック中なので、処理を終了します。");
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
				this.deviceLogDomain.log(LOG, device.getId(), getClass(), "この処理中に発生した全てのデバイスのロックを解除します。");
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
				this.deviceLogDomain.log(LOG, ldc.getDeviceId(), getClass(), "デバイス情報が未定義なので、処理を終了します。");
				return;
				}
			Long deviceId = ldc.getDeviceId();

// * 指定されたデバイスに対してSigFoxのデータを取り込む
			try
				{
				this.deviceLogDomain.log(LOG, deviceId, getClass(), "Sigfox データの取り込みを開始します。");
				this.s1SigFoxMessageService.doService(ldc.getDevice());
				this.deviceLogDomain.log(LOG, deviceId, getClass(), "Sigfox データの取り込みが完了しました。");
				}
			catch (Exception e)
				{
				this.deviceLogDomain.log(LOG, deviceId, getClass(), "Sigfox データの取り込みに失敗しました。処理を終了します。");
				throw e;
				}
// * 全データ更新モードの場合、テーブルを初期化する
			if (ldc.isAll())
				{
				this.deviceLogDomain.log(LOG, deviceId, getClass(), "過去のモデル関連データを全削除します。");
				try
					{
					this.s2deviceDataInitService.deleteTables(deviceId, null);
					this.statusDomain.setDataInitialized(ldc.getDevice());
					this.deviceLogDomain.log(LOG, deviceId, getClass(), "過去のモデル関連データの全削除を完了しました。");
					}
				catch (Exception e)
					{
					this.deviceLogDomain.log(LOG, deviceId, getClass(), "テーブルの削除に失敗しました。処理を終了します。");
					throw e;
					}
				}
// * メッセージテーブルから基本情報のDBへのロードを実行する
			this.deviceLogDomain.log(LOG, deviceId, getClass(), "生データの生成を開始します。");
			Date lastUpdated;
			try
				{
				lastUpdated = this.s3RawDataLoaderService.loadMessages(ldc);
				this.statusDomain.setRawDataLoaded(ldc.getDevice());
				this.deviceLogDomain.log(LOG, deviceId, getClass(), "生データの生成が完了しました。");
				}
			catch (Exception e)
				{
				this.deviceLogDomain.log(LOG, deviceId, getClass(), "生データの生成に失敗しました。処理を終了します。");
				throw e;
				}

// * メッセージテーブルから基本情報のDBへのロードを実行する
			if (lastUpdated != null)
				{
				this.deviceLogDomain.log(LOG, deviceId, getClass(), "ヘッドラインデータの生成を開始します。");
				try
					{
					this.s4HeadLineLoaderService.createHealines(ldc.getDevice(), lastUpdated);
					this.deviceLogDomain.log(LOG, deviceId, getClass(), "ヘッドラインデータの生成が完了しました。");
					}
				catch (Exception e)
					{
					this.deviceLogDomain.log(LOG, deviceId, getClass(), "ヘッドラインデータの生成に失敗しました。処理を終了します。");
					throw e;
					}
				}
			else
				{
				this.deviceLogDomain.log(LOG, deviceId, getClass(), "生データの更新は無かったので、ヘッドラインデータに変更はありませんでした。");
				}
// * 日付をまたがった場合、以下の処理を行う
			this.deviceLogDomain.log(LOG, deviceId, getClass(),
					"データが日付をまたがっているか、またがっている場合、何日分の処理が必要か確認処理を行います。");
			List<Ph2DeviceDayEntity> deviceDays;
			try
				{
				deviceDays = this.s5deviceDayService.doService(ldc);
				}
			catch (Exception e)
				{
				this.deviceLogDomain.log(LOG, deviceId, getClass(), "確認処理ができませんでした。処理を終了します。");
				throw e;
				}
			if ((null != deviceDays) && (deviceDays.size() > 0))
				{
				this.statusDomain.setUpdateNotModel(ldc.getDevice());

				this.deviceLogDomain.log(LOG, deviceId, getClass(), "日付単位でのモデル基礎データを生成します。");
				try
					{
					this.s6dailyBaseDataGeneratorService.doService(ldc, deviceDays);
					this.deviceLogDomain.log(LOG, deviceId, getClass(), "日付単位でのモデル基礎データの生成が完了しました。");
					}
				catch (Exception e)
					{
					this.deviceLogDomain.log(LOG, deviceId, getClass(), "日付単位でのモデル基礎データを生成できませんでした。処理を終了します。");
					throw e;
					}
				
				this.deviceLogDomain.log(LOG, deviceId, getClass(), "モデルデータの生成を開始します。");
				try
					{
					this.s8modelDataApplyrService.doService(deviceId, deviceDays);
					this.statusDomain.setModelDataCreated(ldc.getDevice());
					this.deviceLogDomain.log(LOG, deviceId, getClass(), "モデルデータの生成が完了しました。処理を完了します。");
					}
				catch(Exception e)
					{
					this.deviceLogDomain.log(LOG, deviceId, getClass(), "モデルデータの生成に失敗しました。処理を終了します。");
					throw e;				
					}
				}
			else
				{
				this.deviceLogDomain.log(LOG, deviceId, getClass(),
						"日付をまたがったデータはないので、モデルデータの更新は必要ありませんでした。処理を完了します。");
				}
			}
		catch (Exception e)
			{
			e.printStackTrace();
			this.deviceLogDomain.log(LOG, ldc.getDeviceId(), getClass(),
					"デバイスデータのローディングに失敗しました。処理を終了します。");
			}
		}

	}
