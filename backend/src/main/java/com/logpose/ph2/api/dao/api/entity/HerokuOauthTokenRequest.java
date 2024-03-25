package com.logpose.ph2.api.dao.api.entity;

import lombok.Data;

@Data
public class HerokuOauthTokenRequest
	{
	private String url;
	private String grantType;
	private String code;
	private String clienSecret;
	}
