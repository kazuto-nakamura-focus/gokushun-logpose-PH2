package com.logpose.ph2.api.dao.api;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import com.logpose.ph2.api.dao.api.entity.SigFoxDeviceListEntity;
import com.logpose.ph2.api.dao.api.entity.SigFoxMessagesEntity;
import com.logpose.ph2.api.exception.APIException;

import lombok.Data;

/**
 * SigFoxAPIをコールする
 */
@Data
public class SigFoxAPI
	{
	// ===============================================
	// クラスメンバー
	// ===============================================
	private String sigFoxUrl;
	private HttpHeaders headers = new HttpHeaders();
	private RestTemplate restTemplate = new RestTemplate();

	// ===============================================
	// 公開関数群
	// ===============================================
	// --------------------------------------------------
	/**
	 * ベーシック認証を設定する
	 * @param auth
	 */
	// --------------------------------------------------
	public void setBasicAuth(String auth)
		{
		this.headers.set("Authorization", "Basic " + auth);
		}

	// --------------------------------------------------
	/**
	 * デバイスリストを取得する
	 * @return SigFoxDeviceListEntity
	 * @throws InterruptedException 
	 */
	// --------------------------------------------------
	public SigFoxDeviceListEntity getDeviceList() throws APIException
		{
// * 問合せの実行
		return this.getDeviceList(sigFoxUrl);
		}

	// --------------------------------------------------
	/**
	 * SigFoxからメッセージデータを取得する
	 * @param sigFoxDeviceId
	 * @param sinceTimeStamp
	 * @return SigFoxMessagesEntity
	 * @throws APIException 
	 * @throws InterruptedException 
	 */
	// -------------------------------------------------
	public SigFoxMessagesEntity getMessages(String sigFoxDeviceId, long sinceTimeStamp) throws APIException 
		{
// * URLの設定
		String url = sigFoxUrl.replace("%deviceId", sigFoxDeviceId);
		if (sinceTimeStamp > 0)
			{
			url = url + "?since=" + String.valueOf(sinceTimeStamp);
			}
// * 問合せの実行
		return this.getMessages(url);
		}

	// --------------------------------------------------
	/**
	 * SigFoxからメッセージデータを取得する
	 * @param url
	 * @param baseAuth
	 * @return SigFoxMessagesEntity
	 * @throws InterruptedException 
	 */
	// -------------------------------------------------
	public SigFoxMessagesEntity getMessages(String url) throws APIException
		{
		try
			{
			Thread.sleep(2000);
			}
		catch (Exception e)
			{
			}
		ResponseEntity<SigFoxMessagesEntity> response = null;
		HttpEntity<String> request = new HttpEntity<>(headers);

// * Get処理の実行
		try
			{
			response = restTemplate.exchange(url, HttpMethod.GET, request, SigFoxMessagesEntity.class);
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

	// --------------------------------------------------
	/**
	 * デバイスリストを取得する
	 * @return SigFoxDeviceListEntity
	 * @throws InterruptedException 
	 */
	// --------------------------------------------------
	public SigFoxDeviceListEntity getDeviceList(String url) throws APIException
		{
		try { Thread.sleep(1000); } catch(Exception e) {}

		ResponseEntity<SigFoxDeviceListEntity> response = null;
		HttpEntity<String> request = new HttpEntity<>(headers);

// * Get処理の実行
		try
			{
			response = restTemplate.exchange(url, HttpMethod.GET, request, SigFoxDeviceListEntity.class);
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