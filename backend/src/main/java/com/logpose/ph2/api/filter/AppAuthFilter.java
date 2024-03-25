package com.logpose.ph2.api.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.logpose.ph2.api.dao.api.entity.HerokuOauthTokenResponse;
import com.logpose.ph2.api.dao.db.entity.Ph2OauthEntity;
import com.logpose.ph2.api.domain.auth.HerokuOAuthAPIDomain;
import com.logpose.ph2.api.domain.auth.HerokuOAuthLogicDomain;
import com.logpose.ph2.api.master.CookieMaster;

@Component
public class AppAuthFilter implements Filter
	{
	// ===============================================
	// クラスメンバー
	// ===============================================
	@Autowired
	private HerokuOAuthLogicDomain logicDomain;
	@Autowired
	private HerokuOAuthAPIDomain apiDomain;

	// ===============================================
	// パブリック関数群
	// ===============================================
	public AppAuthFilter()
		{
		}

	@Override
	public void init(FilterConfig filterConfig) throws ServletException
		{
		// Do Nothing
		}

	@Override
	public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
			throws IOException, ServletException
		{
		HttpServletRequest request = (HttpServletRequest) servletRequest;
		if(request.getServletPath().startsWith("/auth/"))
			{
			filterChain.doFilter(servletRequest, servletResponse);
			return;
			}
		HttpServletResponse response = (HttpServletResponse) servletResponse;
// * Cookieから必要な情報を取り出す
		String accessToken = null;
		Long appId = null;
		Cookie[] cookies = request.getCookies();
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
// * Cookieに必要な情報が無い場合、ログイン画面へリダイレクトして終了
		if ((null == appId) || (null == accessToken))
			{
			response.sendRedirect(this.apiDomain.getHerokuLogin());
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
				this.logicDomain.upateDB(oauth, authRes);
				}
// * チェック期間を超えた場合
			else if (result == HerokuOAuthLogicDomain.OUT_OF_TERM)
				{
				HerokuOauthTokenResponse authRes = this.apiDomain.confirm(oauth);
				this.logicDomain.upateDB(oauth, authRes);
				}
			else if (result != 0)
				{
				throw new RuntimeException("未定義の状態です。");
				}
			request.setAttribute("UserId", appId);
			filterChain.doFilter(request, response);
			}
		catch (RuntimeException re)
			{
			response.sendRedirect("/login");
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

	}