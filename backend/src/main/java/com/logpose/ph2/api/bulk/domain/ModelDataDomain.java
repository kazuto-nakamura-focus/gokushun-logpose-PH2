package com.logpose.ph2.api.bulk.domain;

import java.text.ParseException;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.logpose.ph2.api.domain.GrowthDomain;
import com.logpose.ph2.api.domain.LeafDomain;
import com.logpose.ph2.api.domain.PhotoSynthesisDomain;

@Component
public class ModelDataDomain
	{
	@Autowired
	private GrowthDomain growthDomain;
	@Autowired
	private LeafDomain leafDomain;
	@Autowired
	private PhotoSynthesisDomain photoSynthesisDomain;

	public void doService(long deviceId, short year, Date startDate) throws ParseException
		{
		this.growthDomain.updateModelTable(deviceId, year, startDate);
		this.leafDomain.updateModelTable(deviceId, year, startDate);
		this.photoSynthesisDomain.updateModelTable(deviceId, year, startDate);
		}

	}
