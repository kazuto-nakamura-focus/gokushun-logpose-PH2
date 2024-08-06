package com.logpose.ph2.api.dao.api;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import com.logpose.ph2.api.dao.api.entity.FreeWeatherRequest;
import com.logpose.ph2.api.dao.api.entity.FreeWheatherDay;
import com.logpose.ph2.api.dao.api.entity.FreeWheatherHour;
import com.logpose.ph2.api.dao.api.entity.FreeWheatherResponse;
import com.logpose.ph2.api.dao.db.entity.Ph2WeatherForecastEntity;

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
	 */
	// -------------------------------------------------
	public List<Ph2WeatherForecastEntity> getForcastEntities(Long deviceId, FreeWeatherRequest request)
		{
		Calendar cal = Calendar.getInstance();
		
		final List<Ph2WeatherForecastEntity> result = new ArrayList<>();
		Ph2WeatherForecastEntity data;

		FreeWheatherResponse res = this.getWeather(request);
		data = new Ph2WeatherForecastEntity();
		data.setDeviceId(deviceId);
		data.setCode(res.getCurrent().getCondition().getCode());
		data.setUrl(res.getCurrent().getCondition().getIcon());
		cal.setTimeInMillis(res.getCurrent().getEpoch()*1000);
		data.setTime(cal.getTime());
		result.add(data);

		int count = 0;
		Long epoch = res.getCurrent().getEpoch();
		List<FreeWheatherDay> forecast = res.getForecast().getForecastday();
		for (final FreeWheatherDay dayItem : forecast)
			{
			List<FreeWheatherHour> hours = dayItem.getHour();
			for (final FreeWheatherHour hourItem : hours)
				{
				if (hourItem.getTimeEpoch() > epoch)
					{
					count ++;
					data = new Ph2WeatherForecastEntity();
					data.setDeviceId(deviceId);
					data.setCode(hourItem.getCondition().getCode());
					data.setUrl(hourItem.getCondition().getIcon());
					cal.setTimeInMillis(hourItem.getTimeEpoch()*1000);
					data.setTime(cal.getTime());
					result.add(data);
					if(count ==3) break;
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
	 */
	// -------------------------------------------------
	public FreeWheatherResponse getWeather(FreeWeatherRequest request)
		{
		try { Thread.sleep(1000); } catch(Exception e) {}

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
	 */
	// -------------------------------------------------
	private FreeWheatherResponse getData(String url)
		{
		ResponseEntity<FreeWheatherResponse> response = null;
// * Get処理の実行
		try
			{
			response = restTemplate.exchange(url, HttpMethod.GET, null, FreeWheatherResponse.class);
			}
		catch (Exception e)
			{
			throw new RuntimeException("クエリ" + url + "は失敗しました。", e);
			}

// * 戻り値のチェックと返却
		HttpStatusCode statusCode = response.getStatusCode();
		if (statusCode.is2xxSuccessful())
			{
			return response.getBody();
			}
		else
			{
			throw new RuntimeException("クエリ" + url + "は失敗しました。");
			}
		}
	}