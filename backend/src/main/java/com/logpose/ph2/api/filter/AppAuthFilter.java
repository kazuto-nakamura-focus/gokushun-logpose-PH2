package com.logpose.ph2.api.filter;

import java.io.IOException;
import java.io.PrintWriter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.logpose.ph2.api.configration.DefaultDomainParameter;
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
	private DefaultDomainParameter param;
	@Autowired
	private DefaultOAuthParameters oAuthParameters;
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
		if (request.getServletPath().startsWith("/api/auth/"))
		// if (request.getServletPath().startsWith("/"))
			{
			filterChain.doFilter(servletRequest, servletResponse);
			return;
			}
		HttpServletResponse response = (HttpServletResponse) servletResponse;
// * Cookieから必要な情報を取り出す
		String accessToken = null;
		Long appId = null;
		Cookie[] cookies = request.getCookies();
		if (null != cookies)
			{
			short count = 0;
			for (int i = 0; i < cookies.length; i++)
				{
				Cookie cookie = cookies[i];
				if (cookie.getName().equals(CookieMaster.ACCESS_TOKEN))
					{
					accessToken = cookie.getValue();
					count++;
					}
				else if (cookie.getName().equals(CookieMaster.USER_ID))
					{
					if ((null != cookie.getValue()) && (cookie.getValue().length() > 0))
						{
						appId = Long.valueOf(cookie.getValue());
						}
					count++;
					}
				if (count == 2) break;
				}
			}
// * Cookieに必要な情報が無い場合、ログイン画面へリダイレクトして終了
		if ((null == appId) || (null == accessToken))
			{
			this.sendRedirect(response, this.apiDomain.getHerokuLogin());
			return;
			}
		try
			{
// * トークン情報の取得
			Ph2OauthEntity oauth = this.logicDomain.getOauthInfo(appId);

// * トークンのチェック
			int result = this.logicDomain.checkUser(accessToken, oauth);
// * ユーザーがいない場合
			if (result == HerokuOAuthLogicDomain.NO_USER)
				{
				throw new RuntimeException("ユーザーではありません。");
				}
// * トークンが無い場合
			else if (result == HerokuOAuthLogicDomain.TOKEN_ERR)
				{
				throw new RuntimeException("不正なアクセスです。");
				}
// * トークンが期限切れの場合
			else if (result == HerokuOAuthLogicDomain.OUT_OF_TIME)
				{
// * トークンの更新
				HerokuOauthTokenResponse authRes = this.apiDomain.refreshToken(oauth);
				accessToken = authRes.getAccessToken();
				this.logicDomain.upateDB(oauth, authRes);
				}
// * チェック期間を超えた場合
			else if (result == HerokuOAuthLogicDomain.OUT_OF_TERM)
				{
				HerokuOauthTokenResponse authRes = this.apiDomain.confirm(oauth);
				accessToken = authRes.getAccessToken();
				this.logicDomain.upateDB(oauth, authRes);
				}
			else if (result != 0)
				{
				throw new RuntimeException("未定義の状態です。");
				}

			Cookie cookie = new Cookie(CookieMaster.ACCESS_TOKEN, accessToken);
			cookie.setDomain(param.getDomain());
			response.addCookie(cookie);

			filterChain.doFilter(request, response);
			}
		catch (RuntimeException re)
			{
			this.sendRedirect(response, this.apiDomain.getHerokuLogin());
			return;
			}
		catch (Exception e)
			{
			response.sendError(401, "not authorized -> " + e.getMessage());
			}

		}

	@Override
	public void destroy()
		{
		}

	private void sendRedirect(HttpServletResponse response, String url) throws IOException
		{
		response.setStatus(HttpServletResponse.SC_OK);
		response.addHeader("Access-Control-Allow-Origin", oAuthParameters.getOriginUrl());
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