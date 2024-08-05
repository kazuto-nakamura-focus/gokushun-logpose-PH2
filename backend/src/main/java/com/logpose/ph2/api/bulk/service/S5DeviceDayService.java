package com.logpose.ph2.api.bulk.service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.logpose.ph2.api.algorythm.DeviceDayAlgorithm;
import com.logpose.ph2.api.bulk.domain.DeviceLogDomain;
import com.logpose.ph2.api.bulk.vo.LoadCoordinator;
import com.logpose.ph2.api.dao.db.cache.DeviceDayCacher;
import com.logpose.ph2.api.dao.db.entity.Ph2DeviceDayEntity;
import com.logpose.ph2.api.dao.db.entity.Ph2DeviceDayEntityExample;
import com.logpose.ph2.api.dao.db.entity.Ph2DevicesEntity;
import com.logpose.ph2.api.dao.db.mappers.Ph2DeviceDayMapper;
import com.logpose.ph2.api.dao.db.mappers.Ph2ModelDataMapper;

import lombok.Data;

@Component
public class S5DeviceDayService
	{
	// ===============================================
	// クラスメンバー
	// ===============================================
	private static Logger LOG = LogManager.getLogger(S5DeviceDayService.class);
	@Autowired
	private DeviceLogDomain deviceLogDomain;
	@Autowired
	private Ph2DeviceDayMapper ph2DeviceDayMapper;
	@Autowired
	private Ph2ModelDataMapper ph2ModelDataMapper;
	@Autowired
	private DeviceDayAlgorithm deviceDayAlgorithm;

	// ===============================================
	// 公開関数群
	// ===============================================
	// --------------------------------------------------
	/**
	 * 日次データに必要なデバイスデータを作成し、返却する。
	 * @param ldc
	 * @return List<Ph2DeviceDayEntity>
	 */
	// --------------------------------------------------
	public List<Ph2DeviceDayEntity> doService(LoadCoordinator ldc)
		{
// * 部分更新の場合
		if (!ldc.isAll())
			{
			return this.updateDeviceDays(ldc);
			}
// * 一括更新の場合
		else
			{
			return this.addDeviceDays(ldc);
			}
		}

	// --------------------------------------------------
	/**
	 * 更新時、データがすでにあるかチェックする。
	 * 無い場合は、日次データに必要なデバイスデータを作成し、返却する。
	 * @param  ldc
	 * @return Date 最終更新日時
	 */
	// --------------------------------------------------
	@Transactional(readOnly = true, rollbackFor = Exception.class)
	public List<Ph2DeviceDayEntity> updateDeviceDays(LoadCoordinator ldc)
		{
		final Long deviceId = ldc.getDeviceId();
// * 現時点でのRelBaseDataのそのデバイスの最新更新日時を取得する
		Date latest = ldc.getLatestBaseDate();
// * データが無ければnullを返却して終了
		if (null == latest)
			{
			this.deviceLogDomain.log(LOG, deviceId, getClass(), "最新の更新データが存在しないため、処理を終了します。");
			return null;
			}
		else
			{
			this.deviceLogDomain.log(LOG, deviceId, getClass(), "生データの最新の更新日時" + latest.toString() + "からのデータを取得します。");
			}

// * 最新更新日時をその日の始まりの前日に設定し、もしデバイスディに登録済みならば、終了
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(latest);
		this.deviceDayAlgorithm.setTimeZero(calendar);
		calendar.add(Calendar.DATE, -1);
		Ph2DeviceDayEntityExample exm = new Ph2DeviceDayEntityExample();
		exm.createCriteria().andDeviceIdEqualTo(ldc.getDeviceId()).andDateEqualTo(calendar.getTime())
				.andHasRealEqualTo(true);
		if (this.ph2DeviceDayMapper.countByExample(exm) > 0) 
			{
			this.deviceLogDomain.log(LOG, deviceId, getClass(), "最新の更新データはすでに登録済みなので、処理を終了します。");
			return null;
			}


// * 該当デバイスの最後の実データを持った日付の翌日を得る
		Date last_device_date = this.ph2DeviceDayMapper.selectMaxTrueDate(ldc.getDeviceId());
		last_device_date = this.deviceDayAlgorithm.getNextDayZeroHour(last_device_date);

// * 更新対象期間を得る
		DeviceTerm trm = this.setEffectiveTerm(ldc, last_device_date);
		if (null == trm) 
			{
			this.deviceLogDomain.log(LOG, deviceId, getClass(), "データ更新に必要な期間が無いので、処理を終了します。");
			return null;
			}
		

// * デバイスディテーブルに期間中のレコードがあるか確認し、無ければ生成する
		List<Ph2DeviceDayEntity> device_days = this.createDeviceDayTable(ldc.getDevice(), trm);
		List<Ph2DeviceDayEntity> result = new ArrayList<>();
		for (Ph2DeviceDayEntity entity : device_days)
			{
			// データがある場合
			if (entity.getHasReal())
				{
				result.clear();
				continue;
				}
			result.add(entity);
			}
// * 変更対象のデバイスディリストを返却する
		return result;
		}

	// --------------------------------------------------
	/**
	 * 日次データに必要Device Dayのリストを作成し、返却する。
	 * @param ldc ローディング情報
	 * @return List<Ph2DeviceDayEntity> 検査対象となるDevice Dayのリスト
	 */
	// --------------------------------------------------
	@Transactional(readOnly = true, rollbackFor = Exception.class)
	public List<Ph2DeviceDayEntity> addDeviceDays(LoadCoordinator ldc)
		{
		final Long deviceId = ldc.getDeviceId();
// * 現時点でのRelBaseDataのそのデバイスの最新更新日時を取得する
		Date oldest = ldc.getOldestBaseDate();
// * データが無ければnullを返却して終了
		if (null == oldest)
			{
			this.deviceLogDomain.log(LOG, deviceId, getClass(), "最新の更新データが存在しないため、処理を終了します。");
			return null;
			}
		else
			{
			this.deviceLogDomain.log(LOG, deviceId, getClass(), "生データの最新の更新日時" + oldest.toString() + "からのデータを取得します。");
			}

// * 更新対象期間を得る
		DeviceTerm trm = this.setEffectiveTerm(ldc, oldest);
		if (null == trm) 
			{
			this.deviceLogDomain.log(LOG, deviceId, getClass(), "データ更新に必要な期間が無いので、処理を終了します。");
			return null;
			}

// * デバイスディテーブルに期間中のレコードがあるか確認し、無ければ生成する
		return this.createDeviceDayTable(ldc.getDevice(), trm);
		}

	// ===============================================
	// 保護関数群
	// ===============================================
	// --------------------------------------------------
	/**
	 * データの取得開始日と終了日を作成する
	 * @param device
	 * @param oldest
	 * @return DeviceTerm
	 */
	// --------------------------------------------------
	private DeviceTerm setEffectiveTerm(LoadCoordinator ldc, Date oldest)
		{
		Ph2DevicesEntity device = ldc.getDevice();

// * 必要なデータの最も古い日付け
		final Calendar oldest_date = Calendar.getInstance();
		oldest_date.setTime(oldest);

// * デバイスの基準日
		Calendar base_date = this.deviceDayAlgorithm.getBaseDate(device.getBaseDate());
		this.deviceDayAlgorithm.setTimeZero(base_date);
		this.deviceLogDomain.log(LOG, device.getId(), getClass(), "デバイスの基準日は" + base_date.getTime().toString() + "です。");

// * デバイスのデータ設定の開始日
		final Calendar start_date = Calendar.getInstance();
		// デバイスの開始日の指定が無い場合
		if (null == device.getOpStart())
			{
			start_date.setTime(oldest_date.getTime());
			}
		// デバイスの開始日の指定がある場合
		else
			{
			start_date.setTime(device.getOpStart());
			// デバイスの開始日より、最も古いデータの日付が新しい場合
			if (start_date.getTimeInMillis() < oldest_date.getTimeInMillis())
				{
				start_date.setTime(oldest_date.getTime());
				}
			}
		this.deviceDayAlgorithm.setTimeZero(start_date);

// * データ開始日の基準日からの日数を算出する
		base_date.set(Calendar.YEAR, start_date.get(Calendar.YEAR));
		if (start_date.getTimeInMillis() < base_date.getTimeInMillis())
			{
			base_date.add(Calendar.YEAR, -1);
			}
// * 全データロードの場合、上で設定されたbase_dateを開始日として、経過日を０とする
		long daysBetween = 0;
		if (ldc.isAll())
			{
			start_date.setTime(base_date.getTime());
			}
		else
			{
// * 基準日のデータがあるかどうかチェックし、無ければ、上で設定されたbase_dateを開始日として、経過日を０とする
			Ph2DeviceDayEntityExample exm = new Ph2DeviceDayEntityExample();
			exm.createCriteria().andDeviceIdEqualTo(device.getId())
					.andDateEqualTo(base_date.getTime())
					.andLapseDayEqualTo((short) 1);
			if (0 == this.ph2DeviceDayMapper.countByExample(exm))
				{
				start_date.setTime(base_date.getTime());
				}
			else
				{
				LocalDate date1 = LocalDate.of(
						base_date.get(Calendar.YEAR),
						base_date.get(Calendar.MONTH) + 1,
						base_date.get(Calendar.DATE));
				LocalDate date2 = LocalDate.of(
						start_date.get(Calendar.YEAR),
						start_date.get(Calendar.MONTH) + 1,
						start_date.get(Calendar.DATE));
				daysBetween = ChronoUnit.DAYS.between(date1, date2);
				}
			}
		this.deviceLogDomain.log(LOG, device.getId(), getClass(), "デバイスの対象データの開始日は" + start_date.getTime().toString() + "です。");

// * デバイスのデータ設定の終了日を設定する
		final Calendar end_date = Calendar.getInstance();
		/// デバイスの終了日の指定が無い場合
		if (null == device.getOpEnd())
			{
			Calendar today = Calendar.getInstance();
			end_date.set(Calendar.YEAR, today.get(Calendar.YEAR));
			end_date.set(Calendar.MONTH, base_date.get(Calendar.MONTH));
			end_date.set(Calendar.DATE, base_date.get(Calendar.DATE));
			if (end_date.getTimeInMillis() < today.getTimeInMillis())
				{
				end_date.add(Calendar.YEAR, 1);
				}
			}
		else
			{
			end_date.setTime(device.getOpEnd());
			}
		this.deviceDayAlgorithm.setTimeZero(end_date);
		this.deviceLogDomain.log(LOG, device.getId(), getClass(), "対象データ期間は" + 
				start_date.getTime().toString() + "～" + end_date.getTime().toString() + "までとなります。");
		// * 開始日が終了日より後の場合はnullを返却する
		if (start_date.getTimeInMillis() >= end_date.getTimeInMillis()) return null;
// * 返却オブジェクトの作成と返却
		DeviceTerm term = new DeviceTerm();
		term.setBaseDate(base_date);
		term.setStartDate(start_date);
		term.setEndDate(end_date);
		term.setDayCount(daysBetween);
		return term;
		}

	// -------------------- ------------------------------
	/**
	 * デバイスディレコードを作成する
	 * @param device
	 * @param term
	 */
	// --------------------------------------------------
	private List<Ph2DeviceDayEntity> createDeviceDayTable(Ph2DevicesEntity device, DeviceTerm term)
		{
		this.deviceLogDomain.log(LOG, device.getId(), getClass(), "日数分の該当期間のモデルデータテーブルを準備します。");
		List<Ph2DeviceDayEntity> result = new ArrayList<>();

		while (!DeviceDayCacher.lock())
			{
			try
				{
				Thread.sleep(1000);
				}
			catch (Exception e)
				{
				}
			}
		try
			{
// * デバイスディのIDとキャッシュ管理オブジェクトの生成
			Long maxId = this.ph2DeviceDayMapper.selectMaxId();
			if (null == maxId) maxId = Long.valueOf(1);
			DeviceDayCacher cacher = new DeviceDayCacher(maxId, ph2DeviceDayMapper, ph2ModelDataMapper);

// * 初期化
			Calendar seek = Calendar.getInstance();
			seek.setTime(term.getStartDate().getTime());
			long dayCount = term.getDayCount() + 1;

// * デバイス年度の設定
			int year = seek.get(Calendar.YEAR);
			Calendar baseDate = Calendar.getInstance();
			// * 基準日
			baseDate.setTime(term.getBaseDate().getTime());
			// * シークする年の設定
			baseDate.set(Calendar.YEAR, seek.get(Calendar.YEAR));
			// * シーク開始日が基準日より以前の場合
			if (baseDate.getTimeInMillis() >= seek.getTimeInMillis())
				{
				year--;
				}

// * デバイスディテーブルのレコードを追加する
			for (; seek.getTimeInMillis() < term.getEndDate().getTimeInMillis(); seek.add(Calendar.DATE, 1), dayCount++)
				{
				// * 基準日に到達した場合
				if ((seek.get(Calendar.MONTH) == term.getBaseDate().get(Calendar.MONTH)) &&
						(seek.get(Calendar.DATE) == term.getBaseDate().get(Calendar.DATE)))
					{
					year++;
					dayCount = 1;
					}
				// * 日付テーブルから該当デバイスレコードを取得する。
				Ph2DeviceDayEntityExample exm = new Ph2DeviceDayEntityExample();
				exm.createCriteria().andDeviceIdEqualTo(device.getId())
						.andDateEqualTo(seek.getTime()).andLapseDayEqualTo((short) dayCount);
				List<Ph2DeviceDayEntity> olds = this.ph2DeviceDayMapper.selectByExample(exm);
				if (this.ph2DeviceDayMapper.countByExample(exm) > 1)
					{
					throw new RuntimeException("重複データがあります。day_id" + olds.get(1).getId());
					}
				if (this.ph2DeviceDayMapper.countByExample(exm) == 1)
					{
					result.add(olds.get(0));
					continue;
					}

				Ph2DeviceDayEntity entity = new Ph2DeviceDayEntity();
				entity.setDate(seek.getTime());
				entity.setDeviceId(device.getId());
				entity.setHasReal(false);
				entity.setLapseDay((short) dayCount);
				entity.setYear((short) year);
				cacher.addDeviceDayData(entity);
				result.add(entity);
				}
			cacher.flush();
			return result;
			}
		finally
			{
			DeviceDayCacher.unlock();
			}
		}
	}

@Data
class DeviceTerm
	{
	private Calendar baseDate;
	private Calendar startDate;
	private Calendar endDate;
	private long dayCount;
	}
