package com.logpose.ph2.api.controller;

import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.logpose.ph2.api.configration.DefaultOAuthParameters;
import com.logpose.ph2.api.controller.dto.AuthCookieDTO;
import com.logpose.ph2.api.domain.AuthCookieModel;
import com.logpose.ph2.api.dto.ResponseDTO;
import com.logpose.ph2.api.master.CookieMaster;
import com.logpose.ph2.api.service.AuthService;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * ログイン・ログアウトのコールバック
 * 
 * @since 2024/01/03
 * @version 1.0
 */
@CrossOrigin(origins = { "http://localhost:8080", "http://localhost:3000",
		"https://gokushun-ph2-it.herokuapp.com" }, methods = { RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT,
				RequestMethod.DELETE }, allowedHeaders = "*", exposedHeaders = "*", allowCredentials = "true")
@RestController
@RequestMapping(path = "/api/auth")
public class AuthController
	{
	// ===============================================
	// クラスメンバー
	// ===============================================
	private static Logger LOG = LogManager.getLogger(AuthController.class);

	@Autowired
	private AuthService authService;
	
	@Autowired
	private DefaultOAuthParameters params;

	// ===============================================
	// パブリック関数群
	// ===============================================
	// --------------------------------------------------
	/**
	 * Login受付
	 * @throws IOException 
	 */
	// --------------------------------------------------
	@GetMapping("/login")
	public void login(HttpServletResponse response)
		{
		Cookie atc = new Cookie("heroku_session_nonce", null);
		atc.setMaxAge(0);
		atc.setPath("/");
		atc.setDomain("heroku.com");
		response.addCookie(atc);
		atc = new Cookie("heroku_session", null);
		atc.setMaxAge(0);
		atc.setPath("/");
		atc.setDomain("heroku.com");
		response.addCookie(atc);
		atc = new Cookie("heroku_user_session", null);
		atc.setMaxAge(0);
		atc.setPath("/");
		atc.setDomain("heroku.com");
		response.addCookie(atc);
		atc = new Cookie("identity-session", null);
		atc.setMaxAge(0);
		atc.setPath("/");
		atc.setDomain("id.heroku.com");
		response.addCookie(atc);
		/*
		 * atc = new Cookie("OptanonAlertBoxClosed", null);
		 * atc.setMaxAge(0);
		 * atc.setPath("/");
		 * atc.setDomain("id.heroku.com");
		 * response.addCookie(atc);
		 */
		/*
		 * atc = new Cookie("OptanonConsent", null);
		 * atc.setMaxAge(0);
		 * atc.setPath("/");
		 * atc.setDomain("id.heroku.com");
		 * response.addCookie(atc);
		 */
		atc = new Cookie("_ga", null);
		atc.setMaxAge(0);
		atc.setPath("/");
		atc.setDomain("heroku.com");
		response.addCookie(atc);

		try
			{
			response.sendRedirect(this.params.getAuthrizeURL());
			}
		catch (IOException e)
			{
			e.printStackTrace();
			}

		}

	// --------------------------------------------------
	/**
	 * herokuからのコールバックの受付
	 * @throws IOException 
	 */
	// --------------------------------------------------
	@GetMapping("/callback")
	public void login(HttpServletResponse response, HttpServletRequest request,
			@RequestParam("code") String code,
			@RequestParam("state") String forgeryToekn) throws IOException
		{
		try
			{
			LOG.info("ログイン処理開始");
// * ログインの実行
			AuthCookieDTO cookieData = this.authService.login(request, code, forgeryToekn);
// * Cookieに設定する
			AuthCookieModel cookieModel = new AuthCookieModel(this.params.getDomain());
			cookieModel.setInitilalToken(response, cookieData);
			response.sendRedirect(this.params.getOriginUrl());
			}
		catch (Exception e)
			{
			e.printStackTrace();
			response.sendError(401, "not authorized -> " + e.getMessage());
			}
		}

	// --------------------------------------------------
	/**
	 * herokuからのログアウト
	 * @throws IOException 
	 */
	// --------------------------------------------------
	@GetMapping("/logout")
	public ResponseDTO login(HttpServletResponse response,
			// public ResponseDTO login(HttpServletResponse response,

			@CookieValue(CookieMaster.USER_ID) String appUserId) throws IOException
		{
		ResponseDTO dto = new ResponseDTO();
		try
			{
// * ログアウト処理の実行
			this.authService.logout(appUserId);
			}
		catch (Exception e)
			{
			e.printStackTrace();
			}
		finally
			{
			}
		dto.setRedirect("http://localhost:8080/logout.html");
		return dto;
		}
	}
