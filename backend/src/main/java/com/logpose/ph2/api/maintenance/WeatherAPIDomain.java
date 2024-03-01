package com.logpose.ph2.api.maintenance;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.transaction.annotation.Transactional;

import com.logpose.ph2.api.algorythm.DateTimeUtility;
import com.logpose.ph2.api.dao.api.WeatherAPI;
import com.logpose.ph2.api.dao.api.entity.Weather;
import com.logpose.ph2.api.dao.api.entity.WeatherDaily;
import com.logpose.ph2.api.dao.api.entity.WeatherHourly;
import com.logpose.ph2.api.dao.api.entity.WeatherRequest;
import com.logpose.ph2.api.dao.db.cache.WeatherDailyMasterCacher;
import com.logpose.ph2.api.dao.db.entity.Ph2DeviceDayEntity;
import com.logpose.ph2.api.dao.db.entity.Ph2DeviceDayEntityExample;
import com.logpose.ph2.api.dao.db.entity.Ph2DevicesEntity;
import com.logpose.ph2.api.dao.db.entity.Ph2FieldsEntity;
import com.logpose.ph2.api.dao.db.entity.Ph2WeatherDailyMasterEntity;
import com.logpose.ph2.api.dao.db.entity.Ph2WeatherDailyMasterEntityExample;

public class WeatherAPIDomain
	{
	// ===============================================
	// クラスメンバー
	// ===============================================
	private static Logger LOG = LogManager.getLogger(WeatherAPIDomain.class);

	private WeatherAPIDaoPackage apiDao;
	private WeatherAPI api = new WeatherAPI();
	private List<WeatherRequest> requests;
	private WeatherDailyMasterCacher cache;

	// ===============================================
	// コンストラクタ
	// ===============================================
	public WeatherAPIDomain(WeatherAPIDaoPackage apiDao)
		{
		this.apiDao = apiDao;
		this.cache = new WeatherDailyMasterCacher(this.apiDao.getPh2WeatherDailyMasterMapper());
		}

	// ===============================================
	// 非公開関数群
	// ===============================================
	// --------------------------------------------------
	/**
	 * APIへのリクエストリストを作成する
	 * @param device
	 * @param deviceDays
	 * @throws Exception 
	 */
	// --------------------------------------------------
	private void createRequests(Ph2DevicesEntity device, List<Ph2DeviceDayEntity> deviceDays) throws ParseException
		{
		requests = new ArrayList<>();

		WeatherRequest request = new WeatherRequest();
// * URL
		request.setUrl(this.apiDao.getParams().getHistoryUrl());
// * 圃場情報から位置を得る
		Ph2FieldsEntity field = this.apiDao.getPh2FieldsMapper().selectByPrimaryKey(device.getFieldId());
		request.setLatitude(field.getLatitude().toString());
		request.setLongitude(field.getLongitude().toString());
// * タイムゾーンの設定
		request.setTimeZone(device.getTz());
// * クエリ対象
		request.setHourly(this.apiDao.getParams().getHourly());
		request.setDaily(this.apiDao.getParams().getDaily());
// * 検索期間
		Date startDate = null;
		Date endDate = null;
		// 本日の日付設定
		Calendar cal = Calendar.getInstance();
		this.apiDao.getDeviceDayAlgorithm().getTimeZero(cal);
		Date today = cal.getTime();
// * デバイス情報からデータが無い期間の取得
		for (Ph2DeviceDayEntity entity : deviceDays)
			{
			if (entity.getDate().getTime() == today.getTime()) break;
			if (!entity.getHasReal())
				{
				if (null == startDate)
					{
					startDate = entity.getDate();
					request.setStartDate(DateTimeUtility.getStringFromDate(startDate));
					}
				endDate = entity.getDate();
				}
			else
				{
				if (null != startDate)
					{
					request.setEndDate(DateTimeUtility.getStringFromDate(endDate));
					this.requests.add(request);
					}
				startDate = null;
				endDate = null;
				}
			}
		if (null != startDate)
			{
			request.setEndDate(DateTimeUtility.getStringFromDate(endDate));
			this.requests.add(request);
			}
		}

	// ===============================================
	// 公開関数群
	// ===============================================
	// --------------------------------------------------
	/**
	 * デバイスリストからデータの無いデバイスの情報を抽出し、APIへのリクエストリストを作成する
	 * @throws Exception 
	 */
	// --------------------------------------------------
	@Transactional(rollbackFor = Exception.class)
	public void importData(Ph2DevicesEntity device) throws Exception
		{
		if (!this.apiDao.getDeviceStatusDomain().setDataOnLoad(device))
			{
			LOG.warn(device.getId() + "はロック中なので処理を飛ばします。");
			}
		try
			{
// * マスターの削除
			Ph2WeatherDailyMasterEntityExample wexm = new Ph2WeatherDailyMasterEntityExample();
			wexm.createCriteria().andDeviceIdEqualTo(device.getId());
			this.apiDao.getPh2WeatherDailyMasterMapper().deleteByExample(wexm);
			
// * デバイスディデータから実データを、持っていないリストを作成する
			Ph2DeviceDayEntityExample exm = new Ph2DeviceDayEntityExample();
			exm.createCriteria().andDeviceIdEqualTo(device.getId()).andHasRealEqualTo(false);
			exm.setOrderByClause("date asc");
			List<Ph2DeviceDayEntity> targets = this.apiDao.getPh2DeviceDayMapper().selectByExample(exm);

// * リクエストを作成する。
			this.createRequests(device, targets);
			
// * 作成されたリクエストを使ってAPIをコールする
			for (WeatherRequest request : this.requests)
				{
				LOG.info(device.getId() + "のデータをインポートします。" 
						+ request.getStartDate() + "-" + request.getEndDate());
				Weather weather = this.api.getHistory(request);
// * 取得したAPIデータから、DailyBaseDataを作成する
				this.createDailyBaseData(device, weather);
				}
			this.cache.flush();
			}
		catch (Exception e)
			{
			throw e;
			}
		finally
			{
			this.apiDao.getDeviceStatusDomain().setDataNotLoading(device);
			}
		}

	// --------------------------------------------------
	/**
	 * WeatherデータからDailyBaseDataのマスターを作成する
	 * @param url
	 * @return SigFoxMessagesEntity
	 * @throws InterruptedException 
	 */
	// -------------------------------------------------
	public void createDailyBaseData(Ph2DevicesEntity device, Weather weather)
		{
		WeatherDaily daily = weather.getDaily();
		WeatherHourly hourly = weather.getHourly();
		Calendar cal = Calendar.getInstance();
		int j = 0;
		for (int i = 0; i < daily.getTime().size(); i++)
			{
// * DailyBaseDataの生成と基礎データの作成
			Ph2WeatherDailyMasterEntity master = new Ph2WeatherDailyMasterEntity();
			master.setDeviceId(device.getId());

			Date date = daily.getTime().get(i);
			date = this.apiDao.getDeviceDayAlgorithm().getTimeZero(date);
			master.setCastedAt(date);

			long daylight = daily.getDaylightDuration().get(i);
			master.setSunTime(daylight);

			float mint = daily.getTemperature2mMin().get(i);
			float maxt = daily.getTemperature2mMax().get(i);
			master.setTm((mint + maxt) / 2 - 10);
			master.setRawCdd((mint + maxt) / 2 - 9.18);

// * その日の時間単位を抽出
			List<Float> trvals = new ArrayList<>();

			cal.setTime(date);
			cal.add(Calendar.DATE, 1);
			this.apiDao.getDeviceDayAlgorithm().setTimeZero(cal);
			Date day = cal.getTime();

			for (; j < hourly.getTime().size(); j++)
				{
				Date time = hourly.getTime().get(j);
				if (time.getTime() >= day.getTime()) break;
				trvals.add(hourly.getTerrestrialRadiationInstant().get(j));
				}

			double cdd = this.setIsolationData(trvals);
			master.setPar(cdd);
// * DBへの追加
			this.cache.add(master);
			}
		}

	// --------------------------------------------------
	/**
	 * DBにPARと日射時間を設定する
	 * @param device
	 * @param tmRecords
	 * @param result
	 */
	// --------------------------------------------------
	private double setIsolationData(List<Float> values)
		{
		float prevValue = 0;
		double par = 0;
		for (Float value : values)
			{
			double tmp = value * 3600;
			par += tmp;
			}
		return par;
		}
	}
