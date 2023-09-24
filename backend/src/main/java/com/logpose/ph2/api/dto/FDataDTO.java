package com.logpose.ph2.api.dto;

import lombok.Data;

@Data
public class FDataDTO
	{
	//* ID
	private Integer id;
	//* 順番
	private Integer order;
	//* 生育期名
	private String name;
	//* ELStage
	private String elStage;
	//* F間隔
	private Integer interval;
	//* 累積
	private Integer cumulation;
	//* 実績日
	private String realDate;
	}
