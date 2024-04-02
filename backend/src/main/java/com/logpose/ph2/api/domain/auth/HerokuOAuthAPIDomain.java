package com.logpose.ph2.api.domain.auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.logpose.ph2.api.configration.DefaultOAuthParameters;
import com.logpose.ph2.api.controller.dto.AuthCookieDTO;
import com.logpose.ph2.api.dao.api.HerokuAuthAPI;
import com.logpose.ph2.api.dao.api.entity.HerokuOauthAccountResponse;
import com.logpose.ph2.api.dao.api.entity.HerokuOauthTokenResponse;
import com.logpose.ph2.api.dao.db.entity.Ph2OauthEntity;

@Component
public class HerokuOAuthAPIDomain
	{
	// ===============================================
	// クラスメンバー
	// ===============================================
	@Autowired
	private DefaultOAuthParameters params;

	// ===============================================
	// パブリック関数群
	// ===============================================
	// --------------------------------------------------
	/**
	 * HerokuのLogin URLを返す
	 * @param code
	 * @return HerokuOauthTokenResponse
	 */
	// --------------------------------------------------
	public String getHerokuLogin()
		{
		return "https://id.heroku.com/oauth/authorize?client_id=" +
				params.getCleintId() + "&response_type=code&scope=identity&state=" + params.getForgery();
		}

	// --------------------------------------------------
	/**
	 * オリジンのURLを返す
	 * @param AuthCookieDTO
	 * @return String
	 */
	// --------------------------------------------------
	public String getOriginURL(AuthCookieDTO cookie)
		{
		return params.getOriginUrl() + "?" + "id=" + cookie.getAuthId() + "&name=" + cookie.getName() + "&at="
				+ cookie.getAccessToken();
		}

	// --------------------------------------------------
	/**
	 * コードからアクセストークンを取得する
	 * @param code
	 * @param antiFoorgeryToken 
	 * @return HerokuOauthTokenResponse
	 */
	// --------------------------------------------------
	public HerokuOauthTokenResponse getAccessToken(String code, String antiFoorgeryToken)
		{
		if (!antiFoorgeryToken.equals(this.params.getForgery()))
			{
			throw new RuntimeException("不正なドメインからのアクセスです");
			}
		HerokuAuthAPI api = new HerokuAuthAPI();
		api.setUrl(this.params.getUrl());
		api.addParameter("grant_type", "authorization_code");
		api.addParameter("code", code);
		api.addParameter("client_secret", this.params.getCleintSecret());
		api.createQuery();
		return api.getData();
		}

	// --------------------------------------------------
	/**
	 * ユーザーIDからユーザー情報を取得する
	 * @param userId アカウントID
	 * @param accessToken アクセストークン
	 * @return HerokuOauthAccountResponse
	 */
	// --------------------------------------------------
	public HerokuOauthAccountResponse getUserInfo(String userId, String accessToken)
		{
		HerokuAuthAPI api = new HerokuAuthAPI();
		api.setUrl(params.getUserUrl());
		api.addPath(userId);
		api.createQuery();
		api.createHeaders(accessToken);
		return api.getUserData();
		}

	// --------------------------------------------------
	/**
	 * リフレッシュトークンを取得する
	 * @param entity トークン情報
	 * @return HerokuOauthTokenResponse
	 */
	// --------------------------------------------------
	public HerokuOauthTokenResponse refreshToken(Ph2OauthEntity entity)
		{
		HerokuAuthAPI api = new HerokuAuthAPI();
		api.setUrl(this.params.getUrl());
		api.addParameter("grant_type", this.params.getRefreshTokenGrantType());
		api.addParameter("refresh_token", entity.getRefreshToken());
		api.addParameter("client_secret", this.params.getCleintSecret());
		api.createQuery();
		return api.getData();
		}

	// --------------------------------------------------
	/**
	 * トークンの再取得を実行する
	 * @param entity トークン情報
	 * @param HerokuOauthTokenResponse
	 */
	// --------------------------------------------------
	public HerokuOauthTokenResponse confirm(Ph2OauthEntity entity)
		{
		HerokuAuthAPI api = new HerokuAuthAPI();
		api.setUrl(this.params.getUrl());
		api.addParameter("grant_type", this.params.getRefreshTokenGrantType());
		api.addParameter("refresh_token", entity.getRefreshToken());
		api.addParameter("client_secret", this.params.getCleintSecret());
		api.createQuery();
		return api.getData();
		}

	// --------------------------------------------------
	/**
	 * アクセストークンを無効にする
	 * @param userId
	 */
	// --------------------------------------------------
	public void logout(String token, String accessToken)
		{
		HerokuAuthAPI api = new HerokuAuthAPI();
		api.setUrl("https://api.heroku.com/oauth/tokens");
		api.addPath(accessToken);
		api.createQuery();
		api.createHeaders(this.params.getApiKey());
		api.logoutUser();
		}
	}
