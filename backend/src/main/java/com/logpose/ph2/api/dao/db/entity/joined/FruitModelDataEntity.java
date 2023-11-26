package com.logpose.ph2.api.dao.db.entity.joined;

import java.util.Date;

import lombok.Data;

@Data
public class FruitModelDataEntity
	{
	private Float fruitsAverage;
	private Integer fruitsCount;
	private Float tm;
	private Integer leafCount;
	private Double realArea;
	private Double culmitiveCnopyPs;
	private Double crownLeafArea;
	private Date realDate;
	private Date dailyDate;
	}
