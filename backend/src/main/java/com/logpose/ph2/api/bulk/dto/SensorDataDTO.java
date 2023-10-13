package com.logpose.ph2.api.bulk.dto;

import lombok.Data;

@Data
public class SensorDataDTO
	{
	private Long deviceId;
	private Long sensorId;
	private Integer channel;
	private Long sensorContentId;
	private Integer channelSize;
	private Double kst;
	private Double sm;
	private Integer size;
	private Double dxd;
	private Double dxu;
	private Integer rs;
	private String code; 
	}
