package com.logpose.ph2.api.configration;

import lombok.Data;

@Data
public class DefaultOAuthParameters
	{
	private String url;
	private String userUrl;
	private String authUrl;
	private String tokenGrantType;
	private String refreshTokenGrantType;
	private String cleintId;
	private String cleintSecret;
	private String forgery;
	}
