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
import com.logpose.ph2.api.exception.APIException;

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
// * 最新のみ更新の場合
		if (!mode)
			{
			if (false == this.statusDomain.setDataOnLoad(device))
				{
				LOG.warn("デバイス" + device.getId() + "がロック中なので、処理を終了します。");
				return;
				}
			this.deviceLogDomain.startBatch(false, device.getId());
			this.deviceLogDomain.log(LOG, device, getClass(), "***START*** デバイスの更新バッチを開始します。", mode);
			this.deviceLogDomain.log(LOG, device, getClass(), "デバイスのロックをロックします。", mode);
// * 終了リストに追加
			finishList.put(device.getId(), device);
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
				this.deviceLogDomain.log(LOG, device, getClass(), "デバイスのロックを解除します。", mode);
				this.statusDomain.setDataNotLoading(device);
				this.deviceLogDomain.log(LOG, device, getClass(), "***END*** デバイスの更新バッチを終了します。", mode);
				this.deviceLogDomain.endBatch(false, device.getId());
				}
			}
// * 全更新の場合
		else
			{
			List<Ph2DevicesEntity> targets = new ArrayList<>();
			targets.add(0, device);
			finishList.put(device.getId(), device);
// * 対象となるデバイスデータを取得する
			Long prevId = device.getPreviousDeviceId();
			while (null != prevId)
				{
				Ph2DevicesEntity curr = this.s0Initializer.getDeviceInfo(prevId);
// まだ実行されていなければ、ロックリストと実行リストに入れる
				if (!finishList.containsKey(curr.getId()))
					{
					targets.add(0, curr);
					finishList.put(curr.getId(), curr);
					}
				prevId = curr.getPreviousDeviceId();
				}
// * 各対象デバイスに対して処理を実行する
			for (Ph2DevicesEntity target : targets)
				{
// * 既にロード済みならば、処理をしない
				if (!this.statusDomain.isAll(target)) continue;

				if (!this.deviceLogDomain.startBatch(true, target.getId()))
					{
					LOG.warn("他のデバイス" + device.getId() + "が実行中なので、処理を終了します。", mode);
					return;
					}
				LoadCoordinator ldc = this.s0Initializer.initializeCoordinator(target, true);
				this.deviceLogDomain.log(LOG, target, getClass(), "***START*** デバイスのデータロードを開始します。", mode);
				this.deviceLogDomain.log(LOG, target, getClass(), "デバイスのロックを開始します。", mode);
				if (false == this.statusDomain.setDataOnLoad(target))
					{
					this.deviceLogDomain.log(LOG, target, getClass(),
							"***END*** 引継ぎ元のデバイス" + target.getId() + "がロック中なので、処理を終了します。", mode);
					this.deviceLogDomain.endBatch(true, target.getId());
					return;
					}
				try
					{
					// * コーディネーターを引数にデータロードを実行する
					this.loadDevice(ldc);
// * 全ロードモードになっていれば、解除する
					this.statusDomain.setUpdateNotAll(target);
					}
				catch (Exception e)
					{
					throw e;
					}
				finally
					{
					this.deviceLogDomain.log(LOG, target, getClass(), "デバイスのロックを解除します。", mode);
					this.statusDomain.setDataNotLoading(target);
					this.deviceLogDomain.log(LOG, device, getClass(), "***END*** デバイスのデータロードを終了します。***", mode);
					this.deviceLogDomain.endBatch(true, target.getId());
					}
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
				this.deviceLogDomain.log(LOG, ldc.getDevice(), getClass(), "#デバイス情報が未定義なので、処理を終了します。", ldc.isAll());
				return;
				}
			Long deviceId = ldc.getDeviceId();

// * 指定されたデバイスに対してSigFoxのデータを取り込む
			try
				{
				this.deviceLogDomain.log(LOG, ldc.getDevice(), getClass(), "Sigfox データの取り込みを開始します。", ldc.isAll());
				this.s1SigFoxMessageService.doService(ldc.getDevice(), ldc.isAll());
				this.deviceLogDomain.log(LOG, ldc.getDevice(), getClass(), "Sigfox データの取り込みが完了しました。", ldc.isAll());
				}
			catch(APIException e)
				{
				this.deviceLogDomain.log(LOG, ldc.getDevice(), getClass(), e.getCauseText(), ldc.isAll());
				this.deviceLogDomain.log(LOG, ldc.getDevice(), getClass(), "#Sigfox データの取り込みに失敗しました。処理を終了します。", ldc.isAll());
				throw e;
				}
			catch (Exception e)
				{
				this.deviceLogDomain.log(LOG, ldc.getDevice(), getClass(), "#Sigfox データの取り込みに失敗しました。処理を終了します。", ldc.isAll());
				throw e;
				}
// * 全データ更新モードの場合、テーブルを初期化する
			if (ldc.isAll())
				{
				this.deviceLogDomain.log(LOG, ldc.getDevice(), getClass(), "過去のモデル関連データを全削除します。", ldc.isAll());
				try
					{
					this.s2deviceDataInitService.deleteTables(deviceId, null);
					this.statusDomain.setDataInitialized(ldc.getDevice());
					this.deviceLogDomain.log(LOG, ldc.getDevice(), getClass(), "過去のモデル関連データの全削除を完了しました。", ldc.isAll());
					}
				catch (Exception e)
					{
					this.deviceLogDomain.log(LOG, ldc.getDevice(), getClass(), "#" + e.getMessage(), ldc.isAll());
					this.deviceLogDomain.log(LOG, ldc.getDevice(), getClass(), "#テーブルの削除に失敗しました。処理を終了します。", ldc.isAll());
					throw e;
					}
				}
// * メッセージテーブルから基本情報のDBへのロードを実行する
			this.deviceLogDomain.log(LOG, ldc.getDevice(), getClass(), "生データの生成を開始します。", ldc.isAll());
			Date lastUpdated;
			try
				{
				lastUpdated = this.s3RawDataLoaderService.loadMessages(ldc);
				this.statusDomain.setRawDataLoaded(ldc.getDevice());
				this.deviceLogDomain.log(LOG, ldc.getDevice(), getClass(), "生データの生成が完了しました。", ldc.isAll());
				}
			catch (Exception e)
				{
				this.deviceLogDomain.log(LOG, ldc.getDevice(), getClass(), "#" + e.getMessage(), ldc.isAll());
				this.deviceLogDomain.log(LOG, ldc.getDevice(), getClass(), "#生データの生成に失敗しました。処理を終了します。", ldc.isAll());
				throw e;
				}

// * メッセージテーブルから基本情報のDBへのロードを実行する
			if (lastUpdated != null)
				{
				this.deviceLogDomain.log(LOG, ldc.getDevice(), getClass(), "ヘッドラインデータの生成を開始します。", ldc.isAll());
				try
					{
					this.s4HeadLineLoaderService.createHealines(ldc.getDevice(), lastUpdated, ldc.isAll());
					this.deviceLogDomain.log(LOG, ldc.getDevice(), getClass(), "ヘッドラインデータの生成が完了しました。", ldc.isAll());
					}
				catch (Exception e)
					{
					this.deviceLogDomain.log(LOG, ldc.getDevice(), getClass(), "#" + e.getMessage(), ldc.isAll());
					this.deviceLogDomain.log(LOG, ldc.getDevice(), getClass(), "#ヘッドラインデータの生成に失敗しました。処理を終了します。", ldc.isAll());
					throw e;
					}
				}
			else
				{
				this.deviceLogDomain.log(LOG, ldc.getDevice(), getClass(), "生データの更新は無かったので、ヘッドラインデータに変更はありませんでした。", ldc.isAll());
				}
// * 日付をまたがった場合、以下の処理を行う
			this.deviceLogDomain.log(LOG, ldc.getDevice(), getClass(),
					"データが日付をまたがっているか、またがっている場合、何日分の処理が必要か確認処理を行います。", ldc.isAll());
			List<Ph2DeviceDayEntity> deviceDays;
			try
				{
				deviceDays = this.s5deviceDayService.doService(ldc);
				}
			catch (Exception e)
				{
				this.deviceLogDomain.log(LOG, ldc.getDevice(), getClass(), "#" + e.getMessage(), ldc.isAll());
				this.deviceLogDomain.log(LOG, ldc.getDevice(), getClass(), "#確認処理ができませんでした。処理を終了します。", ldc.isAll());
				throw e;
				}
			if ((null != deviceDays) && (deviceDays.size() > 0))
				{
				this.statusDomain.setUpdateNotModel(ldc.getDevice());

				this.deviceLogDomain.log(LOG, ldc.getDevice(), getClass(), "日付単位でのモデル基礎データを生成します。", ldc.isAll());
				try
					{
					this.s6dailyBaseDataGeneratorService.doService(ldc, deviceDays);
					this.deviceLogDomain.log(LOG, ldc.getDevice(), getClass(), "日付単位でのモデル基礎データの生成が完了しました。", ldc.isAll());
					}
				catch(APIException e)
					{
					this.deviceLogDomain.log(LOG, ldc.getDevice(), getClass(), e.getCauseText(), ldc.isAll());
					this.deviceLogDomain.log(LOG, ldc.getDevice(), getClass(), "#日付単位でのモデル基礎データを生成できませんでした。処理を終了します。", ldc.isAll());
					throw e;
					}
				catch (Exception e)
					{
					this.deviceLogDomain.log(LOG, ldc.getDevice(), getClass(), "#日付単位でのモデル基礎データを生成できませんでした。処理を終了します。", ldc.isAll());
					throw e;
					}

				this.deviceLogDomain.log(LOG, ldc.getDevice(), getClass(), "モデルデータの生成を開始します。", ldc.isAll());
				try
					{
					this.s8modelDataApplyrService.doService(ldc.getDevice(), deviceDays, ldc.isAll());
					this.statusDomain.setModelDataCreated(ldc.getDevice());
					this.deviceLogDomain.log(LOG, ldc.getDevice(), getClass(), "モデルデータの生成が完了しました。処理を完了します。", ldc.isAll());
					}
				catch (Exception e)
					{
					this.deviceLogDomain.log(LOG, ldc.getDevice(), getClass(), "#" + e.getMessage(), ldc.isAll());
					this.deviceLogDomain.log(LOG, ldc.getDevice(), getClass(), "#モデルデータの生成に失敗しました。処理を終了します。", ldc.isAll());
					throw e;
					}
				}
			else
				{
				this.deviceLogDomain.log(LOG, ldc.getDevice(), getClass(),
						"日付をまたがったデータはないので、モデルデータの更新は必要ありませんでした。処理を完了します。", ldc.isAll());
				}
			}
		catch (Exception e)
			{
			e.printStackTrace();
			this.deviceLogDomain.log(LOG, ldc.getDevice(), getClass(),
					"#デバイスデータのローディングに失敗しました。処理を終了します。", ldc.isAll());
			}
		}

	}
