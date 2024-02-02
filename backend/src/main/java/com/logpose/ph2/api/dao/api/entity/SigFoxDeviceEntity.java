package com.logpose.ph2.api.dao.api.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown=true)
public class SigFoxDeviceEntity
	{
	private String id;
	}
