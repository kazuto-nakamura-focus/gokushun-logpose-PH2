package com.logpose.ph2.api.dao.api.entity;

import lombok.Data;

@Data
public class HerokuOauthRefreshTokenRequest
	{
	private String url;
	private String grantType;
	private String refreshToken;
	private String clienSecret;
	}
