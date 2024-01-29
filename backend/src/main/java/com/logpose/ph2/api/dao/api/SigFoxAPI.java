package com.logpose.ph2.api.dao.api;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import com.logpose.ph2.api.dao.api.entity.SigFoxMessagesEntity;

import lombok.Data;

@Data
public class SigFoxAPI
	{
	// ===============================================
	// クラスメンバー
	// ===============================================
	private String sigFoxUrl;
	private RestTemplate restTemplate = new RestTemplate();

	// ===============================================
	// パブリック関数
	// ===============================================
	// --------------------------------------------------
	/**
	 * SigFoxからメッセージデータを取得する
	 * @param baseAuth
	 * @param sigFoxDeviceId
	 * @param sinceTimeStamp
	 * @return SigFoxMessagesEntity
	 */
	// -------------------------------------------------
	public SigFoxMessagesEntity getMessages(String baseAuth, String sigFoxDeviceId, long sinceTimeStamp)
		{
// * URLの設定
		String url = sigFoxUrl.replace("%deviceId", sigFoxDeviceId);
		url = url.replace("%since", String.valueOf(sinceTimeStamp));
// * 問合せの実行
		return this.getMessages(url, baseAuth);
		}
	// --------------------------------------------------
	/**
	 * SigFoxからメッセージデータを取得する
	 * @param url
	 * @param baseAuth
	 * @return SigFoxMessagesEntity
	 */
	// -------------------------------------------------
	public SigFoxMessagesEntity getMessages(String url, String baseAuth)
		{
// * ヘッダーにBaseURLを設定する
		HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization", baseAuth);
// * Get処理の実行
		ResponseEntity<SigFoxMessagesEntity> response = restTemplate.exchange(url,
				HttpMethod.GET, new HttpEntity<>(headers), SigFoxMessagesEntity.class);
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
