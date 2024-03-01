package com.logpose.ph2.api.bulk.service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.logpose.ph2.api.algorythm.DeviceDayAlgorithm;
import com.logpose.ph2.api.bulk.vo.LoadCoordinator;
import com.logpose.ph2.api.dao.db.cache.DailyBaseCacher;
import com.logpose.ph2.api.dao.db.entity.Ph2DailyBaseDataEntity;
import com.logpose.ph2.api.dao.db.entity.Ph2DeviceDayEntity;
import com.logpose.ph2.api.dao.db.entity.Ph2DeviceDayEntityExample;
import com.logpose.ph2.api.dao.db.entity.Ph2DevicesEntity;
import com.logpose.ph2.api.dao.db.entity.joined.ExtendDailyBaseDataEntity;
import com.logpose.ph2.api.dao.db.mappers.Ph2DailyBaseDataMapper;
import com.logpose.ph2.api.dao.db.mappers.Ph2DeviceDayMapper;
import com.logpose.ph2.api.dao.db.mappers.joined.Ph2JoinedModelMapper;
import com.logpose.ph2.api.dto.BaseDataDTO;

import lombok.Data;

@Service
public class S6DailyBaseDataGeneratorService
	{
	// ===============================================
	// クラスメンバー
	// ===============================================
	private static Logger LOG = LogManager
			.getLogger(S6DailyBaseDataGeneratorService.class);
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
	// --------------------------------------------------
	/**
	 * DBに日ごとデータを設定する
	 * @param device
	 * @param deviceDays
	 */
	// --------------------------------------------------
	@Transactional(rollbackFor = Exception.class)
	public void doService(LoadCoordinator ldc, List<Ph2DeviceDayEntity> deviceDays)
		{
		LOG.info("日ベースのデータ作成開始：" + ldc.getDeviceId());
		DailyBaseCacher cache = new DailyBaseCacher(ph2DailyBaseDataMapper);

// * データの移行が必要かどうかチェックする
		List<ExtendDailyBaseDataEntity> trs_dd = this.checkTransfer(ldc);
		int i = 0;
		if (null != trs_dd)
			{
// * データの移行を行う
			i = this.copyOldData(ldc.getDeviceId(), deviceDays, trs_dd, cache);
			}

		List<Ph2DeviceDayEntity> unset_devices = new ArrayList<>();
		for (; i < deviceDays.size(); i++)
			{
			Ph2DeviceDayEntity device_day = deviceDays.get(i);

// * その日の１０分単位のデータで気温と光合成有効放射束密度を取得する
			List<BaseDataDTO> tmRecords = this.ph2JoinedModelMapper.getBaseData(
					device_day.getDeviceId(), device_day.getDate(),
					this.deviceDayAlgorithm.getNextDayZeroHour(device_day.getDate()));
// * その日のデータが存在する場合
			if (tmRecords.size() > 0)
				{
				DailyBaseDataDTO result = this.addTmData(device_day, tmRecords);
				this.setIsolationData(device_day, tmRecords, result, cache);
				device_day.setHasReal(true);
				this.ph2DeviceDayMapper.updateByPrimaryKey(device_day);
				unset_devices.clear();
				}
			else
				{
				unset_devices.add(device_day);
				}
			}
// * 同じデバイスで年度間の参照をするので、一旦DBへ書き込む
		cache.flush();
// * 実データ以後のデータを前年度から取り込む
		this.copyLastYearData(ldc.getDeviceId(), unset_devices, 0, cache);
// * DBへの更新
		cache.flush();
		LOG.info("日ベースのデータ作成終了");
		}

	// ===============================================
	// 保護関数群
	// ===============================================
	// --------------------------------------------------
	/**
	 * DBに日ごとの基礎データを設定する
	 * @param device
	 * @param tmRecords
	 * @param prevCdd
	 */
	// --------------------------------------------------
	private DailyBaseDataDTO addTmData(Ph2DeviceDayEntity device, List<BaseDataDTO> tempList)
		{
		DailyBaseDataDTO result = new DailyBaseDataDTO();

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
		Ph2DailyBaseDataEntity entity = this.ph2DailyBaseDataMapper.selectByPrimaryKey(device.getId());
		if (null == entity)
			{
			entity = new Ph2DailyBaseDataEntity();
			result.setNew(true);
			}
		entity.setDayId(device.getId());
		entity.setAverage(sum / count);
		entity.setTm((min + max) / 2 - 10);
		entity.setRawCdd((min + max) / 2 - 9.18);
		entity.setCdd(0.0);
		result.setEntity(entity);
		return result;
		}

	// --------------------------------------------------
	/**
	 * DBにPARと日射時間を設定する
	 * @param device
	 * @param tmRecords
	 * @param result
	 */
	// --------------------------------------------------
	private void setIsolationData(Ph2DeviceDayEntity device,
			List<BaseDataDTO> tmRecords, DailyBaseDataDTO result, DailyBaseCacher cache)
		{
// * PAR値（光合成有効放射束密度ベース）のみのリスト作成する
// * --- BEGIN ---
		List<BaseDataDTO> par_records = new ArrayList<>();
		for (BaseDataDTO data : tmRecords)
			{
			if (null != data.getInsolation()) par_records.add(data);
			}
		if (0 == par_records.size()) return;
// * --- END ---

		double sum = 0;   // その日のPAR合計値
		long sun_time = 0;   // 日射時間
		Double prev_value = null;   // 直前のPAR値
		Date prev_time = null;   // 直前の時間
		for (BaseDataDTO data : par_records)
			{
// * 光合成有効放射束密度の取得
			double value = data.getInsolation();
// * 初期値の場合はその設定をする
			if (null == prev_value)
				{
				prev_value = value;
				prev_time = data.getCastedAt();
				continue;
				}
// * 前の値が０の場合、前の値に対するデータの追加は行わない
			if (0 != prev_value.doubleValue())
				{
// * valueが取得された時間との差(秒)
				long diff_time = (data.getCastedAt().getTime() - prev_time.getTime()) / 1000;
// * valueが０の場合のPARの時間差
				if ((0 == value) && (diff_time > 600)) diff_time = 600;
// * PARの算出
				double par = prev_value * diff_time;
				sum += par;
// * 日射時間の算出
				sun_time += diff_time;
				}
			prev_value = value;
			prev_time = data.getCastedAt();
			}
// * DBにPARと日射時間を設定する
		result.getEntity().setPar(sum);
		result.getEntity().setSunTime(sun_time);
		if (result.isNew())
			{
			cache.addDailyBaseData(result.getEntity());
			}
		else
			this.ph2DailyBaseDataMapper.updateByPrimaryKey(result.getEntity());
		}

	// --------------------------------------------------
	/**
	 * データの移行が必要かどうかチェックする
	 * @param ldc
	 * @retrun List<ExtendDailyBaseDataEntity>
	 */
	// --------------------------------------------------
	private List<ExtendDailyBaseDataEntity> checkTransfer(LoadCoordinator ldc)
		{
// * 引継ぎ元デバイスIDが無ければ、終了
		Ph2DevicesEntity device = ldc.getDevice();
		if (null == device.getPreviousDeviceId()) return null;

// * 現在処理中のデバイスの最も古いデータの日付をRelBaseDataから取得する
		final Calendar oldest_date = Calendar.getInstance();
		oldest_date.setTime(ldc.getOldestBaseDate());
		this.deviceDayAlgorithm.setTimeZero(oldest_date);

// * デバイスディテーブルからその日付のデバイスディを取得する
		Ph2DeviceDayEntityExample exm = new Ph2DeviceDayEntityExample();
		exm.createCriteria().andDeviceIdEqualTo(ldc.getDeviceId())
				.andDateEqualTo(oldest_date.getTime());
		List<Ph2DeviceDayEntity> device_days = this.ph2DeviceDayMapper.selectByExample(exm);
		Ph2DeviceDayEntity device_day = device_days.get(0);

// * 引継ぎデータを取得
// * 現在処理中のデバイスの最も古いデータの日付より前のDailyBaseDataを引継ぎ元デバイスから取得する
		List<ExtendDailyBaseDataEntity> base_data = this.ph2DailyBaseDataMapper.selectOldData(
				device.getPreviousDeviceId(),
				device_day.getYear(), device_day.getLapseDay());

// * データが無ければ終了
		if (base_data.size() == 0) return null;

		return base_data;
		}

	// --------------------------------------------------
	/**
	 * 過去のデータをコピーする
	 * @param deviceId
	 * @param newDeviceDays
	 * @param oldDeviceDays
	 * @param cache
	 */
	// --------------------------------------------------
	private int copyOldData(long deviceId,
			List<Ph2DeviceDayEntity> newDeviceDays, List<ExtendDailyBaseDataEntity> oldDeviceDays,
			DailyBaseCacher cache)
		{
// * 引継ぎ元のデータの経過日までデバイスディを移動
		int i = 0;
		ExtendDailyBaseDataEntity first_prev_data = oldDeviceDays.get(0);
		for (Ph2DeviceDayEntity dd : newDeviceDays)
			{
			if (first_prev_data.getLapseDay().intValue() == dd.getLapseDay().intValue()) break;
			i++;
			} ;
// * 引継ぎデバイスIDからのデータの移行
		for (ExtendDailyBaseDataEntity prev : oldDeviceDays)
			{
			Ph2DeviceDayEntity device_day = newDeviceDays.get(i++);
// * DailyBaseDataレコードの作成とDBへの登録
			prev.setDayId(device_day.getId());
			// レコードに追加
			if (null == this.ph2DailyBaseDataMapper.selectByPrimaryKey(prev.getDayId()))
				{
				cache.addDailyBaseData(prev);
				}
			else
				this.ph2DailyBaseDataMapper.updateByPrimaryKey(prev);
// * DeviceDayレコードの更新
			device_day.setHasReal(true);
			this.ph2DeviceDayMapper.updateByPrimaryKey(device_day);
			}
		return i;
		}

	// --------------------------------------------------
	/**
	 * 引継ぎ元の日ごとデータをコピーする
	 * @param deviceId
	 * @param deviceDays
	 * @param cache
	 */
	// --------------------------------------------------
	private void copyLastYearData(
			Long deviceId, List<Ph2DeviceDayEntity> deviceDays, double cdd, DailyBaseCacher cache)
		{
// * 未設定のデータが無い場合は終了
		if (deviceDays.size() == 0) return;
// * 実データの無い最初の日
		Ph2DeviceDayEntity day1 = deviceDays.get(0);
// * 実データの無い最後の日
		Ph2DeviceDayEntity day2 = deviceDays.get(deviceDays.size() - 1);

// * 引継ぎ元のデータを得る。その年の実データがあるデバイスディデータを昇順で
		List<ExtendDailyBaseDataEntity> prev_data =//
				this.ph2DailyBaseDataMapper.selectLastYearData(//
						day1.getDeviceId(), (short) (day1.getYear() - 1), day1.getLapseDay(), day2.getLapseDay());
// * 引継ぎ元のデータが無ければ終了
		if (0 == prev_data.size()) return;
// * データが継続できなければ(同じ経過日でなければ）終了
		if (prev_data.get(0).getLapseDay().intValue() != day1.getLapseDay().intValue()) return;

// * 昨年データの移行
		int i = 0;
		for (ExtendDailyBaseDataEntity prev : prev_data)
			{
// * DailyBaseDataレコードの作成とDBへの登録
// デバイスディIDの設定
			Ph2DeviceDayEntity device_day = deviceDays.get(i++);
			prev.setDayId(device_day.getId());
			// cddの更新
			cdd += prev.getRawCdd();
			prev.setCdd(cdd);
			// レコードに追加
			if (null == this.ph2DailyBaseDataMapper.selectByPrimaryKey(prev.getDayId()))
				{
				cache.addDailyBaseData(prev);
				}
			else
				this.ph2DailyBaseDataMapper.updateByPrimaryKey(prev);
			}
		}
	}

@Data
class DailyBaseDataDTO
	{
	Ph2DailyBaseDataEntity entity;
	boolean isNew = false;
	}
