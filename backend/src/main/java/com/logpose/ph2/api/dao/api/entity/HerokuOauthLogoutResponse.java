package com.logpose.ph2.api.dao.api.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class HerokuOauthLogoutResponse
	{

	
	@JsonProperty("access_token") 
	private String accessToken;
	@JsonProperty("authorization") 
	private String authorization;
	}
