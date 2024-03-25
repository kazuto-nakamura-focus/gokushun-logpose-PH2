package com.logpose.ph2.api.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.logpose.ph2.api.controller.dto.AuthCookieDTO;
import com.logpose.ph2.api.dao.api.entity.HerokuOauthAccountResponse;
import com.logpose.ph2.api.dao.api.entity.HerokuOauthTokenResponse;
import com.logpose.ph2.api.domain.auth.HerokuOAuthAPIDomain;
import com.logpose.ph2.api.domain.auth.HerokuOAuthLogicDomain;

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
	private HerokuOAuthAPIDomain herokuOAuthAPIDomain;
	@Autowired
	private HerokuOAuthLogicDomain herokuOAuthLogicDomain;
	// ===============================================
	// パブリック関数
	// ===============================================
	// --------------------------------------------------
	/**
	 * ログイン時の処理を行う
	 *@param code
	 *@param antiFoorgeryToken
	 * @return AuthCookieDTO
	 */
	// -------------------------------------------------
	public AuthCookieDTO login(String code, String antiFoorgeryToken)
		{
// * codeからアクセストークンを得る。
		HerokuOauthTokenResponse res = this.herokuOAuthAPIDomain.getAccessToken(code, antiFoorgeryToken);
// * アクセストークンからユーザー情報を取得する。
		HerokuOauthAccountResponse userInfo = this.herokuOAuthAPIDomain.getUserInfo(res.getUserId());
// * Authテーブルにトークン情報を設定する
		return this.herokuOAuthLogicDomain.registerUser(code, res, userInfo);
		}
	// --------------------------------------------------
	/**
	 * ログインアウト処理を行う
	 *@param appUserId LogposeユーザーID
	 */
	// -------------------------------------------------
	public void logout(String appUserId)
		{
// * Authテーブルからトークンを削除する。
	   this.herokuOAuthLogicDomain.revokeUser(Long.valueOf(appUserId));
// * Herokuからトークンを無効化する
		this.herokuOAuthAPIDomain.logout(appUserId);
		}
	
	}
