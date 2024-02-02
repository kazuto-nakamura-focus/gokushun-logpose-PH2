package com.logpose.ph2.api.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.logpose.ph2.api.bulk.domain.SigFoxDomain;
import com.logpose.ph2.api.configration.DefaultSigFoxParameters;
import com.logpose.ph2.api.dao.api.SigFoxAPI;

@Service
public class MaintenanceService
	{
	@Autowired
	private SigFoxDomain sigFoxDomain;
	@Autowired
	private DefaultSigFoxParameters params;
	
	@Transactional(rollbackFor = Exception.class)
	public void moveMessagesPh1toPh2()
		{
		SigFoxAPI api = new SigFoxAPI();
		api.setSigFoxUrl(this.params.getListUrl());
		List<String> sigFoxId = this.sigFoxDomain.getDeviceList(api);
		for(String item : sigFoxId)
			{
			this.sigFoxDomain.createPh2Messages(item, api);
			}
		}
	}
