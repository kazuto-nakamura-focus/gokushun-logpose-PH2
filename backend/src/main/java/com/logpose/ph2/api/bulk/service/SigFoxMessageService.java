package com.logpose.ph2.api.bulk.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.logpose.ph2.api.bulk.domain.SigFoxDomain;
import com.logpose.ph2.api.configration.DefaultSigFoxParameters;
import com.logpose.ph2.api.dao.api.SigFoxAPI;
import com.logpose.ph2.api.dao.db.entity.Ph2DevicesEnyity;

@Service
public class SigFoxMessageService
	{
	@Autowired
	private DefaultSigFoxParameters params;
	@Autowired
	private SigFoxDomain sigFoxDomain;
	
	public void doService(List<Ph2DevicesEnyity> devices)
		{
		SigFoxAPI api = new SigFoxAPI();
		api.setSigFoxUrl(this.params.getUrl());
		
		for(Ph2DevicesEnyity device : devices)
			{
			sigFoxDomain.createMessages(device, api);
			}
		}
	}
