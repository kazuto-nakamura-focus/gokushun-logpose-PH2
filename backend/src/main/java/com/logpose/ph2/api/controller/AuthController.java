package com.logpose.ph2.api.controller;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.logpose.ph2.api.controller.dto.AuthCookieDTO;
import com.logpose.ph2.api.master.CookieMaster;
import com.logpose.ph2.api.service.AuthService;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;

/**
 * ログイン・ログアウトのコールバック
 * 
 * @since 2024/01/03
 * @version 1.0
 */
@CrossOrigin(origins = { "http://localhost:8080", "http://localhost:3000",
		"https://gokushun-ph2-it.herokuapp.com" }, methods = { RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT,
				RequestMethod.DELETE }, allowCredentials = "true")
@RestController
@RequestMapping(path = "/auth")
public class AuthController
	{
	// ===============================================
	// クラスメンバー
	// ===============================================
	@Autowired
	private AuthService authService;

	// ===============================================
	// パブリック関数群
	// ===============================================
	// --------------------------------------------------
	/**
	 * herokuからのコールバックの受付
	 * @throws IOException 
	 */
	// --------------------------------------------------
	@GetMapping("/callback")
	public void login(HttpServletResponse response,
			@RequestParam("code") String code,
			@RequestParam("state") String antiFoorgeryToken) throws IOException
		{
		try
			{
// * ログインの実行
			AuthCookieDTO cookieData = this.authService.login(code, antiFoorgeryToken);
// * Cookieの設定
			response.addCookie(new Cookie(CookieMaster.ACCESS_TOKEN, cookieData.getAccessToken()));
			response.addCookie(new Cookie(CookieMaster.USER_ID, cookieData.getId().toString()));
			response.addCookie(new Cookie(CookieMaster.NAME, cookieData.getName()));
// * アプリへリダイレクト
			response.sendRedirect("/app");
			}
		catch (Exception e)
			{
			response.sendError(401, "not authorized -> " + e.getMessage());
			}
		}

	// --------------------------------------------------
	/**
	 * herokuからのログアウト
	 * @throws IOException 
	 */
	// --------------------------------------------------
	@DeleteMapping("/logout")
	public void login(HttpServletResponse response,
			@CookieValue(CookieMaster.USER_ID) String appUserId) throws IOException
		{
		try
			{
//* ログアウト処理の実行
			this.authService.logout(appUserId);
			}
		catch (Exception e)
			{
			response.sendError(401, "not authorized -> " + e.getMessage());
			}
		}
	}
