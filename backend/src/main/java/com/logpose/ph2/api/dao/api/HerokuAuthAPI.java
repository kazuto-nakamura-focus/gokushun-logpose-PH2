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
import com.logpose.ph2.api.dao.api.entity.HerokuOauthTokenResponse;

import lombok.Data;
import lombok.Setter;

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
	private HttpEntity<String> request = null;
	@Setter
	private String url;
	private StringBuilder builder = new StringBuilder();

	// ===============================================
	// 公開関数群
	// ===============================================
	// --------------------------------------------------
	/**
	 * ヘッダーを作成する
	 * @param authorization
	 */
	// --------------------------------------------------
	public void createHeaders(String authorization)
		{
		HttpHeaders headers = new HttpHeaders();
		Map<String, String> map = new HashMap<>();
		map.put("Content-Type", "application/json");
		map.put("Accept", "application/vnd.heroku+json; version=3.webhook");
		headers.setAll(map);
		if (null != authorization) headers.setBearerAuth(authorization);
		this.request = new HttpEntity<>(headers);
		}

	// --------------------------------------------------
	/**
	 * パスを追加する
	 * @param name パス名
	 */
	// --------------------------------------------------
	public void addPath(String path)
		{
		this.url = this.url + "/" + path;
		}
	// --------------------------------------------------
	/**
	 * パスを生成する
	 */
	// --------------------------------------------------
	public void createQuery()
		{
		if (this.builder.length() > 0)
			{
			this.url = this.url + "?" + this.builder.toString();
			}
		}
	// --------------------------------------------------
	/**
	 * 引数を追加する
	 * @param name パラメータ名
	 * @param value パラメータの値
	 */
	// --------------------------------------------------
	public void addParameter(String name, String value)
		{
		if(this.builder.length() > 0) this.builder.append('&');
		this.builder.append(name).append('=').append(value);
		}
	// --------------------------------------------------
	/**
	 * Heroku OAuth APIからトークンデータを取得するs
	 * @param url
	 * @return HerokuOauthTokenResponse
	 */
	// -------------------------------------------------
	public HerokuOauthTokenResponse getData()
		{
		try
			{
			ResponseEntity<HerokuOauthTokenResponse> response = restTemplate.exchange(url, HttpMethod.POST, request,
					HerokuOauthTokenResponse.class);
			HttpStatusCode statusCode = response.getStatusCode();
			if (statusCode.is2xxSuccessful())
				{
				return response.getBody();
				}
			else
				{
				throw new RuntimeException(response.getBody().toString());
				}
			}
		catch (Exception e)
			{
			throw new RuntimeException("クエリ" + url + "は失敗しました。", e);
			}
		}

	// --------------------------------------------------
	/**
	 * Heroku OAuth APIからユーザーデータを取得する
	 * @param url
	 * @return HerokuOauthAccountResponse
	 */
	// -------------------------------------------------
	public HerokuOauthAccountResponse getUserData()
		{
		try
			{
			ResponseEntity<HerokuOauthAccountResponse> response = restTemplate.exchange(url, HttpMethod.GET, request,
					HerokuOauthAccountResponse.class);
			HttpStatusCode statusCode = response.getStatusCode();
			if (statusCode.is2xxSuccessful())
				{
				return response.getBody();
				}
			else
				{
				throw new RuntimeException(response.getBody().toString());
				}
			}
		catch (Exception e)
			{
			throw new RuntimeException("クエリ" + url + "は失敗しました。", e);
			}
		}

	// --------------------------------------------------
	/**
	 * ログアウト処理をコールする
	 * @param url
	 * @return HerokuOauthAccountResponse
	 */
	// -------------------------------------------------
	public HerokuOauthLogoutResponse logoutUser()
		{
		try
			{
			ResponseEntity<HerokuOauthLogoutResponse> response = restTemplate.exchange(url, HttpMethod.DELETE, request,
					HerokuOauthLogoutResponse.class);
			HttpStatusCode statusCode = response.getStatusCode();
			if (statusCode.is2xxSuccessful())
				{
				return response.getBody();
				}
			else
				{
				throw new RuntimeException(response.getBody().toString());
				}
			}
		catch (Exception e)
			{
			throw new RuntimeException("クエリ" + url + "は失敗しました。", e);
			}
		}
	}