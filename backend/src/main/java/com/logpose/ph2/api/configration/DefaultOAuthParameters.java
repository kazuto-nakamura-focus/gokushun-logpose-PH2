package com.logpose.ph2.api.configration;

import lombok.Data;

@Data
public class DefaultOAuthParameters
	{
	private String apiKey;
	private String url;
	private String userUrl;
	private String authUrl;
	private String tokenGrantType;
	private String refreshTokenGrantType;
	private String cleintId;
	private String cleintSecret;
	private String forgery;
	private String originUrl;
	private String logoutUrl;
	
	private String domain;
	private String createAuthUrl;

	public String getAuthrizeURL()
		{
		return this.createAuthUrl + "?client_id=" + this.cleintId 
				+ "&response_type=code&scope=identity&state=" + this.forgery;
		}
	}
