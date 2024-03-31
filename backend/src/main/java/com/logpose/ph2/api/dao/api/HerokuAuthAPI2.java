package com.logpose.ph2.api.dao.api;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import com.logpose.ph2.api.dao.api.entity.HerokuOauthAccountResponse;
import com.logpose.ph2.api.dao.api.entity.HerokuOauthLogoutResponse;
import com.logpose.ph2.api.dao.api.entity.HerokuOauthRefreshTokenRequest;
import com.logpose.ph2.api.dao.api.entity.HerokuOauthTokenRequest;
import com.logpose.ph2.api.dao.api.entity.HerokuOauthTokenResponse;
import com.logpose.ph2.api.dao.db.entity.Ph2OauthEntity;

import lombok.Data;

/**
 * Heroku OAuth APIをコールする
 */
@Data
public class HerokuAuthAPI2
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
		builder.append("grant_type=").append("authorization_code");
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
	public HerokuOauthAccountResponse getUserInfo(String url, String userId, String code)
		{
		return this.getUserData(url+ "/" + userId, code);
		}
	// --------------------------------------------------
	/**
	 * ログアウト処理をコールする
	 * @param accessToken
	 * @return HerokuOauthAccountResponse
	 */
	// -------------------------------------------------
	public HerokuOauthLogoutResponse logout(String url, Ph2OauthEntity  auth, String apikey)
		{
		return this.logoutUser("https://api.heroku.com/oauth/authorizations/" + auth.getUserId(), apikey);
		}
	// --------------------------------------------------s
	/**
	 * Heroku OAuth APIからトークンデータを取得するs
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
	private HerokuOauthAccountResponse getUserData(String url, String code)
		{
		ResponseEntity<HerokuOauthAccountResponse> response = null;
		HttpHeaders headers = new HttpHeaders();
		Map<String, String> map = new HashMap<>();
		map.put("Accept", "application/vnd.heroku+json; version=3.webhooks");
		headers.setAll(map);
	//	headers.setAccept(Collections.singletonList(new MediaType("application", "vnd.heroku json; version=3")));
		headers.setBearerAuth(code);
	    HttpEntity<String> request = new HttpEntity<>(headers);
// * Get処理の実行
		try
			{
			response = restTemplate.exchange(url, HttpMethod.GET, request, HerokuOauthAccountResponse.class);
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
	private HerokuOauthLogoutResponse logoutUser(String url, String apikey)
		{
		ResponseEntity<HerokuOauthLogoutResponse> response = null;
		HttpHeaders headers = new HttpHeaders();
		Map<String, String> map = new HashMap<>();
		map.put("Content-Type", "application/json");
		map.put("Accept", "application/vnd.heroku+json; version=3");
		headers.setAll(map);
		headers.setBearerAuth("f7806d85-7fdc-4c6b-bc1c-d1bb08fd939f");
	    HttpEntity<String> request = new HttpEntity<>(headers);
// * Get処理の実行
		try
			{
			response = restTemplate.exchange(url, HttpMethod.DELETE, request, HerokuOauthLogoutResponse.class);
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