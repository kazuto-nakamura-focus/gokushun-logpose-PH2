package com.logpose.ph2.api.filter;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import com.logpose.ph2.api.configration.DefaultOAuthParameters;
import com.logpose.ph2.api.dao.api.entity.HerokuOauthTokenResponse;
import com.logpose.ph2.api.dao.db.entity.Ph2OauthEntity;
import com.logpose.ph2.api.domain.auth.HerokuOAuthAPIDomain;
import com.logpose.ph2.api.domain.auth.HerokuOAuthLogicDomain;
import com.logpose.ph2.api.domain.auth.RedirectDomain;
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
	private RedirectDomain redirectDomain;
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

// * 認証パスの場合は無視
		if (request.getServletPath().startsWith("/api/auth/"))
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
				if ((null == cookie.getPath()) || (!cookie.getPath().equals("/")))
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
		if ((null == appId) || (null == accessToken))
			{
			this.redirectDomain.sendRedirect(response, this.apiDomain.getHerokuLogin(), null);
			return;
			}

// * トークン情報の取得
		Ph2OauthEntity oauth;
		try
			{
			oauth = this.logicDomain.getOauthInfo(appId);
			if(null == oauth) throw new RuntimeException("ユーザーではありません。");
			}
		catch (Exception e)
			{
			this.redirectDomain.sendRedirect(response, this.apiDomain.getHerokuLogin(), null);
			return;
			}

		try
			{
// * トークンのチェック
			int result = this.logicDomain.checkUser(accessToken, oauth);
// * トークンが違っている場合
			 if (result == HerokuOAuthLogicDomain.TOKEN_ERR)
				{
				throw new RuntimeException("不正なアクセスです。");
				}
// * ログアウト中
			 if (result == HerokuOAuthLogicDomain.IN_LOGOUT)
				 {
				 throw new RuntimeException("ログアウト中");
				 }
// * トークンが期限切れの場合
			else if ((result == HerokuOAuthLogicDomain.OUT_OF_TIME))
				{
// * トークンの更新
				HerokuOauthTokenResponse authRes = this.apiDomain.refreshToken(oauth);
				accessToken = authRes.getAccessToken();
				this.logicDomain.upateDB(oauth, authRes);
// * Cookieの更新
				this.redirectDomain.setTokenCookie(response, CookieMaster.ACCESS_TOKEN);
				}
// * 正常時の呼び出し実行
			filterChain.doFilter(request, response);
			}
		catch (RuntimeException re)
			{
			this.redirectDomain.sendRedirect(response, this.apiDomain.getHerokuLogin(), null);
			return;
			}
		catch (Exception e)
			{
			e.printStackTrace();
			response.sendError(401, "not authorized -> " + e.getMessage());
			}

		}

	@Override
	public void destroy()
		{
		}

	}