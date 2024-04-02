package com.logpose.ph2.api.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.logpose.ph2.api.configration.DefaultOAuthParameters;
import com.logpose.ph2.api.controller.dto.AuthCookieDTO;
import com.logpose.ph2.api.dao.api.entity.HerokuOauthAccountResponse;
import com.logpose.ph2.api.dao.api.entity.HerokuOauthTokenResponse;
import com.logpose.ph2.api.domain.auth.HerokuOAuthAPIDomain;
import com.logpose.ph2.api.domain.auth.HerokuOAuthLogicDomain;

import jakarta.servlet.http.HttpServletRequest;
import lombok.Getter;

/**
 * ログイン・ログアウトの処理を行う
 *
 */
@Service
public class AuthService
	{
	// ===============================================
	// クラスメンバー
	// ===============================================
	@Autowired
	@Getter
	private HerokuOAuthAPIDomain herokuOAuthAPIDomain;
	@Autowired
	private HerokuOAuthLogicDomain herokuOAuthLogicDomain;
	@Autowired
	private DefaultOAuthParameters params;

	// ===============================================
	// パブリック関数
	// ===============================================
	// --------------------------------------------------
	/**
	 * ログイン時の処理を行う
	 * @param request
	 * @param code
	 * @param antiFoorgeryToken
	 * @return AuthCookieDTO
	 */
	// -------------------------------------------------
	public AuthCookieDTO login(HttpServletRequest request, String code, String antiFoorgeryToken)
		{
// * remoteAddrが取得する
		String remoteAdder = this.herokuOAuthLogicDomain.getRemoteIP(request);
// * codeからアクセストークンを得る。
		HerokuOauthTokenResponse res = this.herokuOAuthAPIDomain.getAccessToken(code, antiFoorgeryToken);
// * アクセストークンからユーザー情報を取得する。
		HerokuOauthAccountResponse userInfo = this.herokuOAuthAPIDomain.getUserInfo(res.getUserId(),
				res.getAccessToken());
// * Authテーブルにトークン情報を設定する
		return this.herokuOAuthLogicDomain.registerUser(code, res, userInfo, remoteAdder);
		}

	// --------------------------------------------------
	/**
	 * CookieデータをURLに変換する
	 * @param AuthCookieDTO
	 * @return String
	 */
	// -------------------------------------------------
	public String convertToURL(AuthCookieDTO cookie)
		{
		return this.herokuOAuthAPIDomain.getOriginURL(cookie);
		}

	// --------------------------------------------------
	/**
	 * ログインアウト処理を行う
	 *@param authId OauthテーブルID
	 */
	// -------------------------------------------------
	public void logout(String authId)
		{
		this.herokuOAuthLogicDomain.logout(authId);
		}
	
	}
