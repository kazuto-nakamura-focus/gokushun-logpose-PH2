package com.logpose.ph2.api.domain;

import com.logpose.ph2.api.controller.dto.AuthCookieDTO;
import com.logpose.ph2.api.master.CookieMaster;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class AuthCookieModel
	{
	// ===============================================
	// クラスメンバー
	// ===============================================
	private String domain;

	// ===============================================
	// 公開関数群
	// ===============================================
	public void setInitilalToken(HttpServletResponse response, AuthCookieDTO cookieInfo)
		{
		response.addCookie(this.createCookie(CookieMaster.ACCESS_TOKEN, cookieInfo.getAccessToken()));
		response.addCookie(this.createCookie(CookieMaster.USER_ID, cookieInfo.getAuthId()));
		response.addCookie(this.createCookie(CookieMaster.NAME, cookieInfo.getName()));
		}

	// ===============================================
	// 非公開関数群
	// ===============================================
	private Cookie createCookie(String name, String value)
		{
		Cookie atc = new Cookie(name, value);
		atc.setMaxAge(7776000);
		atc.setPath("/");
		atc.setDomain(this.domain);
		return atc;
		}
	}
