package com.logpose.ph2.api.dao.api.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class HerokuOauthTokenResponse
	{
	@JsonProperty("access_token") 
	private String accessToken;
	@JsonProperty("expires_in") 
	private String expiresIn;
	@JsonProperty("refresh_token") 
	private String refresh_token;
	@JsonProperty("token_type") 
	private String tokenType;
	@JsonProperty("user_id") 
	private String userId;
	@JsonProperty("session_nonce") 
	private String sessionNonce;
	}
