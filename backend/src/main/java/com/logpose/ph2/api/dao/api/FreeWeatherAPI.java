package com.logpose.ph2.api.dao.api;

import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import com.logpose.ph2.api.dao.api.entity.FreeWeatherRequest;
import com.logpose.ph2.api.dao.api.entity.FreeWheatherDay;
import com.logpose.ph2.api.dao.api.entity.FreeWheatherHour;
import com.logpose.ph2.api.dao.api.entity.FreeWheatherResponse;
import com.logpose.ph2.api.dao.db.entity.Ph2DevicesEntity;
import com.logpose.ph2.api.dao.db.entity.Ph2WeatherForecastEntity;
import com.logpose.ph2.api.exception.APIException;

import lombok.Data;

/**
 * FreeWeatherAPIをコールする
 */
@Data
public class FreeWeatherAPI
	{
	// ===============================================
	// クラスメンバー
	// ===============================================
	private RestTemplate restTemplate = new RestTemplate();
	private List<String> urlList = new ArrayList<>();

	// ===============================================
	// 公開関数群
	// ===============================================
	// --------------------------------------------------
	/**
	 * Weather APIから天気予報情報を取得し、要求するオブジェクトに変換する
	 * @param deviceId
	 * @param request
	 * @return FreeWheatherResponse
	 * @throws APIException 
	 */
	// -------------------------------------------------
	public List<Ph2WeatherForecastEntity> getForcastEntities(Ph2DevicesEntity device, FreeWeatherRequest request)
			throws APIException
		{
		ZoneId deviceZoneId = ZoneId.of(device.getTz());
		ZoneId tokyoeZoneId = ZoneId.of("Asia/Tokyo");
		// 現在の日時を指定タイムゾーンで取得
		ZoneOffset deviceZoneOffset = ZonedDateTime.now(deviceZoneId).getOffset();
		ZoneOffset tokyoZoneOffset = ZonedDateTime.now(tokyoeZoneId).getOffset();
		// オフセットを秒単位で取得
		int offsetInSeconds = deviceZoneOffset.getTotalSeconds() - tokyoZoneOffset.getTotalSeconds();

		final List<Ph2WeatherForecastEntity> result = new ArrayList<>();
		Ph2WeatherForecastEntity data;

		FreeWheatherResponse res = this.getWeather(request);
		final Long epoch = res.getCurrent().getEpoch();

		data = new Ph2WeatherForecastEntity();
		data.setDeviceId(device.getId());
		data.setCode(res.getCurrent().getCondition().getCode());
		data.setUrl(res.getCurrent().getCondition().getIcon());
		
		Date date = new Date();
		date.setTime((epoch.longValue()+offsetInSeconds)*1000);
		data.setTime(date);
		result.add(data);

		int count = 0;
		List<FreeWheatherDay> forecast = res.getForecast().getForecastday();
		for (final FreeWheatherDay dayItem : forecast)
			{
			List<FreeWheatherHour> hours = dayItem.getHour();
			for (final FreeWheatherHour hourItem : hours)
				{
				if (hourItem.getTimeEpoch() > epoch)
					{
					count++;
					data = new Ph2WeatherForecastEntity();
					data.setDeviceId(device.getId());
					data.setCode(hourItem.getCondition().getCode());
					data.setUrl(hourItem.getCondition().getIcon());

					date = new Date();
					date.setTime((hourItem.getTimeEpoch()+offsetInSeconds)*1000);
					data.setTime(date);
					result.add(data);
					if (count == 3) break;
					}
				}
			}
		return result;
		}

	// --------------------------------------------------
	/**
	 * Weather APIから天気予報情報を取得する
	 * @param request
	 * @return FreeWheatherResponse
	 * @throws APIException 
	 */
	// -------------------------------------------------
	public FreeWheatherResponse getWeather(FreeWeatherRequest request) throws APIException
		{
		try
			{
			Thread.sleep(1000);
			}
		catch (Exception e)
			{
			}

// * URLの設定
		StringBuilder builder = new StringBuilder();
		builder.append(request.getUrl());
		builder.append("?");
		builder.append("q=").append(request.getLatitude());
		builder.append(",").append(request.getLongitude());
		builder.append(request.getDefaults());
		builder.append("&key=").append(request.getKey());
		String url = builder.toString();
		urlList.add(url);
// * 問合せの実行
		return this.getData(url);
		}

	// --------------------------------------------------
	/**
	 * Weather APIからデータを取得する
	 * @param url
	 * @return FreeWheatherResponse
	 * @throws APIException 
	 */
	// -------------------------------------------------
	private FreeWheatherResponse getData(String url) throws APIException
		{
		ResponseEntity<FreeWheatherResponse> response = null;
// * Get処理の実行
		try
			{
			response = restTemplate.exchange(url, HttpMethod.GET, null, FreeWheatherResponse.class);
			}
		catch (Exception e)
			{
			throw new APIException(url, e);
			}

// * 戻り値のチェックと返却
		HttpStatusCode statusCode = response.getStatusCode();
		if (statusCode.is2xxSuccessful())
			{
			return response.getBody();
			}
		else
			{
			throw new APIException(url, response);
			}
		}
	}