package com.logpose.ph2.api.filter;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.logpose.ph2.api.dao.api.entity.HerokuOauthTokenResponse;
import com.logpose.ph2.api.dao.db.entity.Ph2OauthEntity;
import com.logpose.ph2.api.domain.auth.HerokuOAuthAPIDomain;
import com.logpose.ph2.api.domain.auth.HerokuOAuthLogicDomain;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
// @WebFilter(urlPatterns = { "/api/*", "/"})
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

	@Override
	public void init(FilterConfig filterConfig) throws ServletException
		{
		}

	@Override
	public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
			throws IOException, ServletException
		{
		HttpServletRequest request = (HttpServletRequest) servletRequest;
		if (request.getServletPath().startsWith("/auth/"))
		// if (request.getServletPath().startsWith("/"))
			{
			filterChain.doFilter(servletRequest, servletResponse);
			return;
			}
		HttpServletResponse response = (HttpServletResponse) servletResponse;
// * Cookieから必要な情報を取り出す
		String authTokenHeader = request.getHeader("Authorization");
		String accessToken = null;
		Long appId = null;
		String[] cookies = null;
		if (authTokenHeader != null)
			{
			cookies = authTokenHeader.split(" ");
			appId = Long.valueOf(cookies[0]);
			accessToken = cookies[1];
			}
// * Cookieに必要な情報が無い場合、ログイン画面へリダイレクトして終了
		if ((null == appId) || (null == accessToken))
			{
			response.sendError(400, this.apiDomain.getHerokuLogin());
			// response.sendRedirect(this.apiDomain.getHerokuLogin());
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
			response.sendRedirect(this.apiDomain.getHerokuLogin());
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