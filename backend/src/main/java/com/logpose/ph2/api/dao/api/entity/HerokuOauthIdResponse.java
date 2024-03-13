package com.logpose.ph2.api.dao.api.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class HerokuOauthIdResponse
	{
	@JsonProperty("id") 
	private String id;
	@JsonProperty("expires_in") 
	private String expiresIn;
	}
