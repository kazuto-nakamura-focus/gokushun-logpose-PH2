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

import com.logpose.ph2.api.algorythm.DateTimeUtility;
import com.logpose.ph2.api.algorythm.DeviceDayAlgorithm;
import com.logpose.ph2.api.configration.DefaultWeatherlAPIParameters;
import com.logpose.ph2.api.dao.api.WeatherAPI;
import com.logpose.ph2.api.dao.api.entity.Weather;
import com.logpose.ph2.api.dao.api.entity.WeatherDaily;
import com.logpose.ph2.api.dao.api.entity.WeatherHourly;
import com.logpose.ph2.api.dao.api.entity.WeatherRequest;
import com.logpose.ph2.api.dao.db.cache.WeatherDailyMasterCacher;
import com.logpose.ph2.api.dao.db.entity.Ph2DevicesEntity;
import com.logpose.ph2.api.dao.db.entity.Ph2FieldsEntity;
import com.logpose.ph2.api.dao.db.entity.Ph2WeatherDailyMasterEntity;
import com.logpose.ph2.api.dao.db.mappers.Ph2WeatherDailyMasterMapper;

@Component
public class WeatherAPIDomain
	{
	// ===============================================
	// クラスメンバー
	// ===============================================
	private static Logger LOG = LogManager.getLogger(WeatherAPIDomain.class);

	@Autowired
	private DefaultWeatherlAPIParameters params;
	@Autowired
	private DeviceDayAlgorithm deviceDayAlgorithm;

	// ===============================================
	// 公開関数群
	// ===============================================
	// --------------------------------------------------
	/**
	 * APIへのリクエストリストを作成する
	 * @param device
	 * @param deviceDays
	 * @throws Exception 
	 */
	// --------------------------------------------------
	public Weather createRequests(Ph2FieldsEntity field, Ph2DevicesEntity device, Date startDate, Date endDate)
			throws ParseException
		{
		WeatherRequest request = new WeatherRequest();
// * URL
		request.setUrl(this.params.getHistoryUrl());
// * 圃場情報から位置を得る
		request.setLatitude(field.getLatitude().toString());
		request.setLongitude(field.getLongitude().toString());
// * タイムゾーンの設定
		request.setTimeZone(device.getTz());
// * クエリ対象
		request.setHourly(this.params.getHourly());
		request.setDaily(this.params.getDaily());
// * 検索期間
		request.setStartDate(DateTimeUtility.getStringFromDate(startDate));
		request.setEndDate(DateTimeUtility.getStringFromDate(endDate));
		return new WeatherAPI().getHistory(request);
		}
	// --------------------------------------------------
	/**
	 * WeatherデータからDailyBaseDataのマスターを作成する
	 * @param url
	 * @return SigFoxMessagesEntity
	 * @throws InterruptedException 
	 */
	// -------------------------------------------------
	public void createDailyBaseData(Ph2DevicesEntity device, Weather weather, Ph2WeatherDailyMasterMapper mapper)
		{
		WeatherDailyMasterCacher cache = new WeatherDailyMasterCacher(mapper);
		WeatherDaily daily = weather.getDaily();
		WeatherHourly hourly = weather.getHourly();
		Calendar cal = Calendar.getInstance();
		int j = 0;
		for (int i = 0; i < daily.getTime().size(); i++)
			{
// * DailyBaseDataの生成と基礎データの作成
			Ph2WeatherDailyMasterEntity master = new Ph2WeatherDailyMasterEntity();
			master.setDeviceId(device.getId());

// * 日付の設定
			Date date = daily.getTime().get(i);
			date = deviceDayAlgorithm.getTimeZero(date);
			master.setCastedAt(date);
// * 日照時間の設定
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
			deviceDayAlgorithm.setTimeZero(cal);
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
			cache.add(master);
			}
		cache.flush();
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
		double par = 0;
		for (Float value : values)
			{
			double tmp = value * 3600;
			par += tmp;
			}
		return par;
		}
	}
