package com.logpose.ph2.api.filter;

import java.io.IOException;
import java.io.PrintWriter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.logpose.ph2.api.configration.DefaultOAuthParameters;
import com.logpose.ph2.api.dao.api.entity.HerokuOauthTokenResponse;
import com.logpose.ph2.api.dao.db.entity.Ph2OauthEntity;
import com.logpose.ph2.api.domain.auth.HerokuOAuthAPIDomain;
import com.logpose.ph2.api.domain.auth.HerokuOAuthLogicDomain;
import com.logpose.ph2.api.dto.ResponseDTO;
import com.logpose.ph2.api.master.CookieMaster;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
// @WebFilter(urlPatterns = { "/api/*", "/"})
@Order(100)
public class AppAuthFilter implements Filter
	{
	// ===============================================
	// クラスメンバー
	// ===============================================
	@Autowired
	private DefaultOAuthParameters params;
	@Autowired
	private HerokuOAuthLogicDomain logicDomain;
	@Autowired
	private HerokuOAuthAPIDomain apiDomain;

	// ===============================================
	// パブリック関数群
	// ===============================================

	@Override
	public void init(FilterConfig filterConfig) throws ServletException
		{
		}

	@Override
	public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
			throws IOException, ServletException
		{
		HttpServletRequest request = (HttpServletRequest) servletRequest;
		System.out.println(request.getServletPath());
		if (request.getServletPath().startsWith("/api/auth/"))
		// if (request.getServletPath().startsWith("/"))
			{
			filterChain.doFilter(servletRequest, servletResponse);
			return;
			}
		HttpServletResponse response = (HttpServletResponse) servletResponse;
// * Cookieから必要な情報を取り出す
		String accessToken = null;
		String appId = null;
		Cookie[] cookies = request.getCookies();
		if (null != cookies)
			{
			for (int i = 0; i < cookies.length; i++)
				{
				Cookie cookie = cookies[i];
				// * Cookieのパスが違う場合は無視
				if ((null != cookie.getPath()) && (!cookie.getPath().equals("/")))
					{
					continue;
					}
				if (null == accessToken)
					{
					if (cookie.getName().equals(CookieMaster.ACCESS_TOKEN))
						{
						accessToken = cookie.getValue();
						}
					}
				if (null == appId)
					{
					if (cookie.getName().equals(CookieMaster.USER_ID))
						{
						appId = cookie.getValue();
						}
					}
				}
			}
// * Cookieに必要な情報が無い場合、ログイン画面へリダイレクトして終了
		if ((null == appId) || (0 == appId.length()) || (null == accessToken) || (0 == accessToken.length()))
			{
			this.sendRedirect(response, this.apiDomain.getHerokuLogin());
			return;
			}
		try
			{
// * トークン情報の取得
			Ph2OauthEntity oauth = this.logicDomain.getOauthInfo(appId);
			try
				{
				oauth = this.logicDomain.getOauthInfo(appId);
// * ユーザーがいない場合
				if (null == oauth) throw new RuntimeException("ユーザーではありません。");
				}
			catch (Exception e)
				{
				this.sendRedirect(response, this.apiDomain.getHerokuLogin());
				return;
				}
// * リモートアドレスの取得
			String remoteAddr = this.logicDomain.getRemoteIP(request);
// * トークンのチェック
			int result = this.logicDomain.checkUser(accessToken, remoteAddr, oauth);
// * トークンが違っている場合
			if (result == HerokuOAuthLogicDomain.TOKEN_ERR)
				{
				throw new RuntimeException("不正なアクセスです。");
				}
			// * ログアウト中
			if (result == HerokuOAuthLogicDomain.IN_LOGOUT)
				{
				this.sendRedirect(response, "http://localhost:8080/logout.html");
				return;
				}
// * トークンが期限切れの場合
			if (result == HerokuOAuthLogicDomain.OUT_OF_TIME)
				{
// * トークンの更新
				HerokuOauthTokenResponse authRes = this.apiDomain.refreshToken(oauth);
				accessToken = authRes.getAccessToken();
				this.logicDomain.upateDB(oauth, authRes);
				}

			Cookie atc = new Cookie(CookieMaster.ACCESS_TOKEN, accessToken);
			atc.setMaxAge(7776000);
			atc.setPath("/");
			atc.setDomain(this.params.getDomain());
			response.addCookie(atc);

			filterChain.doFilter(request, response);
			}
		catch (RuntimeException re)
			{
			AppAuthFilter.clearCookie(response, CookieMaster.ACCESS_TOKEN, params.getDomain());
			this.sendRedirect(response, this.apiDomain.getHerokuLogin());
			return;
			}
		catch (Exception e)
			{
			e.printStackTrace();
			response.sendError(401, "not authorized -> " + e.getMessage());
			}

		}

	public static void clearCookie(HttpServletResponse response, String name, String domain)
		{
		Cookie atc = new Cookie(CookieMaster.ACCESS_TOKEN, null);
		atc.setMaxAge(0);
		atc.setPath("/");
		atc.setDomain(domain);
		response.addCookie(atc);
		}

	public static Cookie getCookie(HttpServletRequest request, String name)
		{
		Cookie target = null;
		if (request.getCookies() != null)
			{
			for (Cookie cookie : request.getCookies())
				{
				if (cookie.getName().equals(name))
					{
					cookie.setMaxAge(0);
					target = cookie;
					}
				}
			}

		return target;
		}

	@Override
	public void destroy()
		{
		}

	private void sendRedirect(HttpServletResponse response, String url) throws IOException
		{
		response.setStatus(HttpServletResponse.SC_OK);
		response.addHeader("Access-Control-Allow-Origin", params.getOriginUrl());
		response.addHeader("Access-Control-Allow-Credentials", "true");
		response.addHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, PUT, DELETE, HEAD");
		response.addHeader("Access-Control-Allow-Headers",
				"X-PINGOTHER, Origin, X-Requested-With, Content-Type, Accept");
		response.addHeader("Access-Control-Max-Age", "1728000");

		ResponseDTO mssg = new ResponseDTO();
		mssg.setRedirect(url);

		ObjectMapper mapper = new ObjectMapper();
		String jsonData = mapper.writeValueAsString(mssg);

		PrintWriter out = response.getWriter();
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		out.print(jsonData);
		out.flush();
		out.close();
		}
	}