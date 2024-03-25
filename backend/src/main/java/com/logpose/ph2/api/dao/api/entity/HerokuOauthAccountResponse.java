package com.logpose.ph2.api.dao.api.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class HerokuOauthAccountResponse
	{
	@JsonProperty("email") 
	private String email;
	@JsonProperty("id") 
	private String id;
	@JsonProperty("name") 
	private String name;
	}
