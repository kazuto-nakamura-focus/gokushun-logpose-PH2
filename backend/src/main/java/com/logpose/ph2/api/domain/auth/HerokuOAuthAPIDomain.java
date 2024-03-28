package com.logpose.ph2.api.domain.auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.logpose.ph2.api.configration.DefaultOAuthParameters;
import com.logpose.ph2.api.controller.dto.AuthCookieDTO;
import com.logpose.ph2.api.dao.api.HerokuAuthAPI;
import com.logpose.ph2.api.dao.api.entity.HerokuOauthAccountResponse;
import com.logpose.ph2.api.dao.api.entity.HerokuOauthRefreshTokenRequest;
import com.logpose.ph2.api.dao.api.entity.HerokuOauthTokenRequest;
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
				params.getCleintId()  + "&response_type=code&scope=identity&state=" + params.getForgery();
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
		return params.getOriginUrl() + "?" + "id=" + cookie.getId() + "&name=" + cookie.getName() + "&at=" +  cookie.getAccessToken();
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
		HerokuOauthRefreshTokenRequest authReq = new HerokuOauthRefreshTokenRequest();
		authReq.setUrl(this.params.getUrl());
		authReq.setGrantType(this.params.getRefreshTokenGrantType());
		authReq.setRefreshToken(entity.getRefreshToken());
		authReq.setClienSecret(this.params.getCleintSecret());
		return new HerokuAuthAPI().getRefreshToken(authReq);
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
		HerokuOauthTokenRequest authReq = new HerokuOauthTokenRequest();
		authReq.setUrl(this.params.getUrl());
		authReq.setGrantType(this.params.getRefreshTokenGrantType());
		authReq.setCode(entity.getToken());
		authReq.setClienSecret(this.params.getCleintSecret());
		return new HerokuAuthAPI().getToken(authReq);
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
		if(!antiFoorgeryToken.equals(this.params.getForgery()))
			{
			throw new RuntimeException("不正なドメインからのアクセスです");
			}
		HerokuOauthTokenRequest authReq = new HerokuOauthTokenRequest();
		authReq.setUrl(this.params.getUrl());
		authReq.setGrantType(this.params.getRefreshTokenGrantType());
		authReq.setCode(code);
		authReq.setClienSecret(this.params.getCleintSecret());
		return new HerokuAuthAPI().getToken(authReq);
		}
	// --------------------------------------------------
	/**
	 * ユーザーIDからユーザー情報を取得する
	 * @param userId
	 * @return HerokuOauthAccountResponse
	 */
	// --------------------------------------------------	
	public HerokuOauthAccountResponse getUserInfo(String userId, String code)
		{
		return new HerokuAuthAPI().getUserInfo(params.getUserUrl(), userId, code);
		}
	// --------------------------------------------------
	/**
	 * アクセストークンを無効にする
	 * @param userId
	 */
	// --------------------------------------------------
	public void logout(String accssToken, String token)
		{
		new HerokuAuthAPI().logout(params.getAuthUrl(), accssToken, token, token);
		}
	}
