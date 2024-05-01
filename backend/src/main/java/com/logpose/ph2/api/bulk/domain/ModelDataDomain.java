package com.logpose.ph2.api.bulk.domain;

import java.text.ParseException;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.logpose.ph2.api.domain.growth.FValueDomain;
import com.logpose.ph2.api.domain.growth.GrowthDomain;
import com.logpose.ph2.api.domain.leaf.LeafDomain;
import com.logpose.ph2.api.domain.photosynthesis.PhotoSynthesisDomain;

@Service
public class ModelDataDomain
	{
	@Autowired
	private GrowthDomain growthDomain;
	@Autowired
	private FValueDomain fValueDomain;
	@Autowired
	private LeafDomain leafDomain;
	@Autowired
	private PhotoSynthesisDomain photoSynthesisDomain;

	@Transactional(rollbackFor = Exception.class)
	public void doService(long deviceId, short year, Date startDate) throws ParseException
		{
		this.growthDomain.updateModelTable(deviceId, year);
		this.fValueDomain.resetActualDate(deviceId, year);
		this.leafDomain.updateModelTable(deviceId, year);
		this.photoSynthesisDomain.updateModelTable(deviceId, year);
		}
	}
