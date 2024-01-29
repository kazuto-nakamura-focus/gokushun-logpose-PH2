package com.logpose.ph2.api.dao.api.entity;

import java.util.List;

import lombok.Data;

@Data
public class SigFoxDataEntity
	{
	private SigFoxDeviceEntity device;
	private long time;
	private String data;
	private int rolloverCounter;
	private int seqNumber;
	private List<Object> rinfos;
	private List<Object> satInfos;
	private String nbFrames;
	private String operator;
	private String country;
	private List<SigFoxComputedLocation> computedLocation;
	private int lqi;
	}
