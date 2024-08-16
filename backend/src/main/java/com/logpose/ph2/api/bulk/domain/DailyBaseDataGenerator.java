package com.logpose.ph2.api.bulk.domain;

import java.text.ParseException;
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
import com.logpose.ph2.api.dao.api.entity.Weather;
import com.logpose.ph2.api.dao.db.cache.DailyBaseCacher;
import com.logpose.ph2.api.dao.db.entity.Ph2DailyBaseDataEntity;
import com.logpose.ph2.api.dao.db.entity.Ph2DeviceDayEntity;
import com.logpose.ph2.api.dao.db.entity.Ph2DevicesEntity;
import com.logpose.ph2.api.dao.db.entity.Ph2FieldsEntity;
import com.logpose.ph2.api.dao.db.entity.Ph2WeatherDailyMasterEntity;
import com.logpose.ph2.api.dao.db.entity.joined.ExtendDailyBaseDataEntity;
import com.logpose.ph2.api.dao.db.mappers.Ph2DailyBaseDataMapper;
import com.logpose.ph2.api.dao.db.mappers.Ph2DeviceDayMapper;
import com.logpose.ph2.api.dao.db.mappers.Ph2FieldsMapper;
import com.logpose.ph2.api.dao.db.mappers.Ph2WeatherDailyMasterMapper;
import com.logpose.ph2.api.dao.db.mappers.joined.Ph2JoinedModelMapper;
import com.logpose.ph2.api.dto.BaseDataDTO;
import com.logpose.ph2.api.exception.APIException;
import com.logpose.ph2.api.master.DeviceDayMaster;

import lombok.Data;

@Component
public class DailyBaseDataGenerator
	{
	// ===============================================
	// クラスメンバー
	// ===============================================
	private static Logger LOG = LogManager
			.getLogger(DailyBaseDataGenerator.class);
	@Autowired
	private Ph2JoinedModelMapper ph2JoinedModelMapper;
	@Autowired
	private DeviceDayAlgorithm deviceDayAlgorithm;
	@Autowired
	private Ph2FieldsMapper ph2FieldsMapper;
	@Autowired
	private Ph2DeviceDayMapper ph2DeviceDayMapper;
	@Autowired
	private Ph2DailyBaseDataMapper ph2DailyBaseDataMapper;
	@Autowired
	private Ph2WeatherDailyMasterMapper ph2WeatherDailyMasterMapper;
	@Autowired
	private WeatherAPIDomain weatherAPIDomain;

	// ===============================================
	// パブリック関数群
	// ===============================================
	// --------------------------------------------------
	/**
	 * 10分単位データから日単位のデータを作成し、ディリーデータ・テーブルに登録する
	 * @param deviceDays
	 * @return List<Ph2DeviceDayEntity> 未設定のディリーデータ・リスト
	 */
	// --------------------------------------------------
	@Transactional(rollbackFor = Exception.class)
	public List<Ph2DeviceDayEntity> loadData(List<Ph2DeviceDayEntity> deviceDays)
		{
// * キャッシュの作成
		DailyBaseCacher cache = new DailyBaseCacher(ph2DailyBaseDataMapper);
// * 未設定のデバイスリスト
		List<Ph2DeviceDayEntity> unset_devices = new ArrayList<>();
// * 各デバイスディデータに対してそれと連携するディリーデータを作成し、関連するテーブルに登録する
		for (final Ph2DeviceDayEntity device_day : deviceDays)
			{
// * その日の１０分単位のデータで気温と光合成有効放射束密度を取得する
			List<BaseDataDTO> tmRecords = this.ph2JoinedModelMapper.getBaseData(
					device_day.getDeviceId(), device_day.getDate(),
					this.deviceDayAlgorithm.getNextDayZeroHour(device_day.getDate()));
// * その日のデータが存在する場合
			if (tmRecords.size() > 0)
				{
				// 10分単位データから日単位のデータを作成し、ディリーデータ・テーブルに登録する
				DailyBaseDataDTO result = this.addTmData(device_day, tmRecords);
				this.setIsolationData(device_day, tmRecords, result, cache);
				// ディリーデータに実データがあることを示すフラグを立て、テーブルを更新する
				device_day.setHasReal(true);
				device_day.setSourceType(DeviceDayMaster.ORIGINAL);
				this.ph2DeviceDayMapper.updateByPrimaryKey(device_day);
				}
// * データが存在しない場合、未設定リストに追加する
			else
				{
				unset_devices.add(device_day);
				}
			}
// * キャッシュの残りデータをテーブルに追加する。
		cache.flush();
// * 未設定のディリーデータ・リストを返却
		return unset_devices;
		}

	// --------------------------------------------------
	/**
	 * 作成されなかった日付に対して、去年のデータを追加する
	 * @param device
	 * @param List<Ph2DeviceDayEntity>
	 * @return 新たに未設定となったリスト
	 */
	// --------------------------------------------------
	public List<Ph2DeviceDayEntity> loadFromPreviousYear(Ph2DevicesEntity device, List<Ph2DeviceDayEntity> unsetDevices)
		{
// * 未設定のディリーデータが無ければ、終了
		if (unsetDevices.size() == 0) return unsetDevices;

		LOG.info("ディリーデータ・テーブルを去年のデータから補完します。:" + device.getId());

// * 最初の一件を取り出す
		Ph2DeviceDayEntity device_day = unsetDevices.get(0);
// * 取り込み処理を実行する
		short last_year = (short) (device_day.getYear() - 1);
		return this.loadFromOtherDailyBaseData(device_day.getDeviceId(), last_year, unsetDevices,
				DeviceDayMaster.PREVIOUS_YEAR);
		}

	// --------------------------------------------------
	/**
	 * 作成されなかった日付に対して、引継ぎ元のデータを追加する
	 * @param device
	 * @param List<Ph2DeviceDayEntity>
	 * @return 新たに未設定となったリスト
	 */
	// --------------------------------------------------
	@Transactional(rollbackFor = Exception.class)
	public List<Ph2DeviceDayEntity> loadFromPreviousDevice(
			Ph2DevicesEntity device,
			List<Ph2DeviceDayEntity> unsetDevices)
		{
// * 未設定のディリーデータが無ければ、終了
		if (unsetDevices.size() == 0) return unsetDevices;
// * 引継ぎ元デバイスIDが無ければ、終了
		if (null == device.getPreviousDeviceId()) return unsetDevices;

		LOG.info("ディリーデータ・テーブルを去年のデータから補完します。:" + device.getId());

// * 最初の一件を取り出す
		Ph2DeviceDayEntity device_day = unsetDevices.get(0);
// * 取り込み処理を実行する
		return this.loadFromOtherDailyBaseData(device.getPreviousDeviceId(), device_day.getYear(), unsetDevices, DeviceDayMaster.PREVIOUS_DEVICE);
		}

	// --------------------------------------------------
	/**
	 * 作成されなかった日付に対して、天気予報マスターからデータを追加する
	 * @param device
	 * @param List<Ph2DeviceDayEntity>
	 * @return 新たに未設定となったリスト
	 */
	// --------------------------------------------------
	@Transactional(rollbackFor = Exception.class)
	public List<Ph2DeviceDayEntity> loadFromWheatherMaster(Ph2DevicesEntity device,
			List<Ph2DeviceDayEntity> unsetDevices)
		{
// * 未設定のディリーデータが無ければ、終了
		if (unsetDevices.size() == 0) return unsetDevices;

		List<Ph2DeviceDayEntity> resultList = new ArrayList<>();
// * キャッシュの作成
		DailyBaseCacher cache = new DailyBaseCacher(ph2DailyBaseDataMapper);
// * 各未設定データのデバイスディに対して
		Date today = deviceDayAlgorithm.getTimeZero(new Date());
		Calendar cal = Calendar.getInstance();
		for (final Ph2DeviceDayEntity deviceDay : unsetDevices)
			{
			Date targetDate = deviceDay.getDate();
// * マスターデータから取る日付を設定する
			if (targetDate.getTime() >= today.getTime())
				{
				cal.setTime(deviceDay.getDate());
				cal.add(Calendar.YEAR, -1);
				targetDate = cal.getTime();
				}
			Ph2WeatherDailyMasterEntity master = this.ph2WeatherDailyMasterMapper.selectByDate(device.getId(),
					targetDate);
			if (null != master)
				{
				Ph2DailyBaseDataEntity baseData = new Ph2DailyBaseDataEntity();
				baseData.setAverage(null);
				baseData.setDayId(deviceDay.getId());
				baseData.setCdd((double) 0);
				baseData.setPar(master.getPar());
				baseData.setRawCdd(master.getRawCdd());
				baseData.setSunTime(master.getSunTime());
				baseData.setTm(master.getTm());
				if (null != this.ph2DailyBaseDataMapper.selectByPrimaryKey(deviceDay.getId()))
					{
					this.ph2DailyBaseDataMapper.updateByPrimaryKey(baseData);
					}
				else
					{
					cache.addDailyBaseData(baseData);
					}
				deviceDay.setSourceType(DeviceDayMaster.WHEATHER);
				this.ph2DeviceDayMapper.updateByPrimaryKey(deviceDay);
				}
			else
				{
				resultList.add(deviceDay);
				}
			}
		cache.flush();
		return resultList;
		}

	// --------------------------------------------------
	/**
	 *  天気予報APIをコールし、天気予報マスターを作成する
	 * @param device
	 * @param List<Ph2DeviceDayEntity>
	 * @throws APIException 
	 */
	// --------------------------------------------------
	@Transactional(rollbackFor = Exception.class)
	public void createWheatherMaster(Ph2DevicesEntity device, List<Ph2DeviceDayEntity> unsetDevices)
			throws ParseException, APIException
		{
		Ph2FieldsEntity field = this.ph2FieldsMapper.selectByPrimaryKey(device.getFieldId());

		Date today = deviceDayAlgorithm.getTimeZero(new Date());
		Calendar cal = Calendar.getInstance();
		Date startDate = null;
		Date prevDate = null;
		for (Ph2DeviceDayEntity deviceDay : unsetDevices)
			{
			Date targetDate = deviceDay.getDate();
// * マスターデータから取る日付を設定する
			if (targetDate.getTime() >= today.getTime())
				{
				cal.setTime(deviceDay.getDate());
				cal.add(Calendar.YEAR, -1);
				targetDate = cal.getTime();
				}
// * インポート開始日が未設定の場合、開始日を設定する
			if (null == startDate)
				{
				startDate = targetDate;
				}
// * 前の日の翌日で無い場合、APIをコールし、マスターにインポートする
			else if ((prevDate.getTime() + 86400000) != targetDate.getTime())
				{
				Weather weather = this.weatherAPIDomain.createRequests(field, device, startDate, prevDate);
				this.weatherAPIDomain.createDailyBaseData(device, weather, ph2WeatherDailyMasterMapper);
				// 開始日を設定
				startDate = targetDate;
				}
			prevDate = targetDate;
			}
		if ((null != startDate) && (startDate.getTime() < prevDate.getTime()))
			{
			Weather weather = this.weatherAPIDomain.createRequests(field, device, startDate, prevDate);
			this.weatherAPIDomain.createDailyBaseData(device, weather, ph2WeatherDailyMasterMapper);
			}
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
		entity.setTm((min + max) / 2);
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
			if (0 != prev_value.doubleValue()) // TODO 36.76以上の場合だけ足す
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
	 * 他のディリーデータをインポートする
	 * @param deviceId
	 * @param year
	 * @param unsetDevices
	 * @return 新たに未設定となったリスト
	 */
	// --------------------------------------------------
	public List<Ph2DeviceDayEntity> loadFromOtherDailyBaseData(long deviceId, short year,
			List<Ph2DeviceDayEntity> unsetDevices, short souceType)
		{
		List<Ph2DeviceDayEntity> resultList = new ArrayList<>();
// * キャッシュの作成
		DailyBaseCacher cache = new DailyBaseCacher(ph2DailyBaseDataMapper);
// * 未設定データの経過日のリストを作成する
		List<Short> lapseDayList = new ArrayList<>();
		for (final Ph2DeviceDayEntity deviceDay : unsetDevices)
			{
			lapseDayList.add(deviceDay.getLapseDay());
			}
// * 未設定データの経過日のリストからそれに対応する引継ぎデータを取得する
		List<ExtendDailyBaseDataEntity> base_data = this.ph2DailyBaseDataMapper.selectOldData(deviceId, year,
				lapseDayList);
// * 引継ぎデータが無い場合、元のリストを返す
		if (0 == base_data.size())
			{
			return unsetDevices;
			}
		Date today = (new DeviceDayAlgorithm()).getTimeZero(new Date());
// * 引継ぎデータを未設定ディリーデータに渡す
		for (Ph2DeviceDayEntity deviceDay : unsetDevices)
			{
			int i = 0;
			boolean has_data = false;
// * 引継ぎデータを検索する
			for (final ExtendDailyBaseDataEntity entity : base_data)
				{
				if (entity.getLapseDay().longValue() == deviceDay.getLapseDay().longValue())
					{
					entity.setDayId(deviceDay.getId());
					entity.setCdd((double) 0);
					Ph2DailyBaseDataEntity baseData = this.ph2DailyBaseDataMapper.selectByPrimaryKey(deviceDay.getId());
					if (null != baseData)
						{
						this.ph2DailyBaseDataMapper.updateByPrimaryKey(entity);
						}
					else
						{
						cache.addDailyBaseData(entity);
						}
// * 引継ぎ元データの実データ有無を引き継ぐ
					if (deviceDay.getDate().getTime() < today.getTime())
						{
						deviceDay.setHasReal(entity.getHasReal());
						}
					has_data = true;
					break;
					}
				i++;
				}
			if (has_data)
				{
				deviceDay.setSourceType(souceType);
				this.ph2DeviceDayMapper.updateByPrimaryKey(deviceDay);
				base_data.remove(i);
				}
			else
				resultList.add(deviceDay);
			}

		cache.flush();
		return resultList;
		}

	@Data
	class DailyBaseDataDTO
		{
		Ph2DailyBaseDataEntity entity;
		boolean isNew = false;
		}

	}
