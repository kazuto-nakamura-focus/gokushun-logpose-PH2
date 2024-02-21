package com.logpose.ph2.api.dao.api;

import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import com.logpose.ph2.api.dao.api.entity.Weather;
import com.logpose.ph2.api.dao.api.entity.WeatherRequest;

import lombok.Data;

/**
 * WeatherAPIをコールする
 */
@Data
public class WeatherAPI
	{
	// ===============================================
	// クラスメンバー
	// ===============================================
	private RestTemplate restTemplate = new RestTemplate();

	// ===============================================
	// 公開関数群
	// ===============================================
	// --------------------------------------------------
	/**
	 * Weather APIから過去天気情報を取得する
	 * @param sigFoxDeviceId
	 * @param sinceTimeStamp
	 * @return SigFoxMessagesEntity
	 * @throws InterruptedException 
	 */
	// -------------------------------------------------
	public Weather getHistory(WeatherRequest request)
		{
		try { Thread.sleep(1000); } catch(Exception e) {}
		
// * URLの設定
		StringBuilder builder = new StringBuilder();
		builder.append(request.getUrl());
		builder.append("?");
		builder.append("latitude=").append(request.getLatitude());
		builder.append("&longitude=").append(request.getLongitude());
		builder.append("&start_date=").append(request.getStartDate());
		builder.append("&end_date=").append(request.getEndDate());
		builder.append("&hourly=").append(request.getHourly());	
		builder.append("&daily=").append(request.getDaily());	
		builder.append("&timezone=").append(request.getTimeZone());	
		
// * 問合せの実行
		return this.getData(builder.toString());
		}

	// --------------------------------------------------
	/**
	 * Weather APIからデータを取得する
	 * @param url
	 * @return SigFoxMessagesEntity
	 * @throws InterruptedException 
	 */
	// -------------------------------------------------
	private Weather getData(String url)
		{
		ResponseEntity<Weather> response = null;
// * Get処理の実行
		try
			{
			response = restTemplate.exchange(url, HttpMethod.GET, null, Weather.class);
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