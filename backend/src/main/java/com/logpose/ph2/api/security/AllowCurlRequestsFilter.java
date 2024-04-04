package com.logpose.ph2.api.security;

import java.io.IOException;

import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class AllowCurlRequestsFilter extends OncePerRequestFilter
	{

	private static final String CURL_USER_AGENT = "curl";

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException
		{
		String userAgent = request.getHeader("User-Agent");
		if (CURL_USER_AGENT.equalsIgnoreCase(userAgent))
			{
			// Curlリクエストを許可
			filterChain.doFilter(request, response);
			}
		else
			{
			// 次のフィルターにリクエストを渡す
			response.setStatus(HttpServletResponse.SC_FORBIDDEN);
			response.getWriter().write("Access denied for non-Curl requests");
			response.getWriter().flush();
			}
		}
	}