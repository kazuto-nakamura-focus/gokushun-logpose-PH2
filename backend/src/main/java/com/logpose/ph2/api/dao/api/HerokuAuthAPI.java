package com.logpose.ph2.api.dao.api;

import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import com.logpose.ph2.api.dao.api.entity.HerokuOauthAccountResponse;
import com.logpose.ph2.api.dao.api.entity.HerokuOauthIdResponse;
import com.logpose.ph2.api.dao.api.entity.HerokuOauthRefreshTokenRequest;
import com.logpose.ph2.api.dao.api.entity.HerokuOauthTokenRequest;
import com.logpose.ph2.api.dao.api.entity.HerokuOauthTokenResponse;

import lombok.Data;

/**
 * Heroku OAuth APIをコールする
 */
@Data
public class HerokuAuthAPI
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
	 * Herokuとトークンを交換する
	 * @param request
	 * @return SigFoxMessagesEntity
	 * @throws InterruptedException 
	 */
	// -------------------------------------------------
	public HerokuOauthTokenResponse getToken(HerokuOauthTokenRequest request)
		{		
// * URLの設定
		StringBuilder builder = new StringBuilder();
		builder.append(request.getUrl());
		builder.append("?");
		builder.append("grant_type=").append(request.getGrantType());
		builder.append("&code=").append(request.getCode());
		builder.append("&client_secret=").append(request.getClienSecret());
		
// * 問合せの実行
		return this.getData(builder.toString());
		}
	// --------------------------------------------------
	/**
	 * Herokuとリフレッシュトークンを交換する
	 * @param request
	 * @return SigFoxMessagesEntity
	 * @throws InterruptedException 
	 */
	// -------------------------------------------------
	public HerokuOauthTokenResponse getRefreshToken(HerokuOauthRefreshTokenRequest request)
		{		
// * URLの設定
		StringBuilder builder = new StringBuilder();
		builder.append(request.getUrl());
		builder.append("?");
		builder.append("grant_type=").append(request.getGrantType());
		builder.append("&refresh_token=").append(request.getRefreshToken());
		builder.append("&client_secret=").append(request.getClienSecret());
		
// * 問合せの実行
		return this.getData(builder.toString());
		}
	// --------------------------------------------------
	/**
	 * User情報を得る
	 * @param userId
	 * @return HerokuOauthAccountResponse
	 */
	// -------------------------------------------------
	public HerokuOauthAccountResponse getUserInfo(String url, String userId)
		{
		return this.getUserData(url+ "/" + userId);
		}
	// --------------------------------------------------
	/**
	 * ログアウト処理をコールする
	 * @param accessToken
	 * @return HerokuOauthAccountResponse
	 */
	// -------------------------------------------------
	public HerokuOauthIdResponse logout(String url, String accessToken)
		{
		return this.logoutUser(url+ "/" + accessToken);
		}
	// --------------------------------------------------
	/**
	 * Heroku OAuth APIからトークンデータを取得する
	 * @param url
	 * @return HerokuOauthTokenResponse
	 */
	// -------------------------------------------------
	private HerokuOauthTokenResponse getData(String url)
		{
		ResponseEntity<HerokuOauthTokenResponse> response = null;
// * Get処理の実行
		try
			{
			response = restTemplate.exchange(url, HttpMethod.POST, null, HerokuOauthTokenResponse.class);
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
	// --------------------------------------------------
	/**
	 * Heroku OAuth APIからユーザーデータを取得する
	 * @param url
	 * @return HerokuOauthAccountResponse
	 */
	// -------------------------------------------------
	private HerokuOauthAccountResponse getUserData(String url)
		{
		ResponseEntity<HerokuOauthAccountResponse> response = null;
// * Get処理の実行
		try
			{
			response = restTemplate.exchange(url, HttpMethod.GET, null, HerokuOauthAccountResponse.class);
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
	// --------------------------------------------------
	/**
	 * ログアウト処理をコールする
	 * @param url
	 * @return HerokuOauthAccountResponse
	 */
	// -------------------------------------------------
	private HerokuOauthIdResponse logoutUser(String url)
		{
		ResponseEntity<HerokuOauthIdResponse> response = null;
// * Get処理の実行
		try
			{
			response = restTemplate.exchange(url, HttpMethod.DELETE, null, HerokuOauthIdResponse.class);
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