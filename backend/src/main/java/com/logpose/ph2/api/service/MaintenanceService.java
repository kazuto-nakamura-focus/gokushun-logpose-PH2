package com.logpose.ph2.api.service;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.logpose.ph2.api.dao.db.entity.Ph2DeviceDayEntityExample;
import com.logpose.ph2.api.dao.db.entity.Ph2DevicesEnyity;
import com.logpose.ph2.api.maintenance.WeatherAPIDaoPackage;
import com.logpose.ph2.api.maintenance.WeatherAPIDomain;

@Service
public class MaintenanceService
	{
	private static Logger LOG = LogManager.getLogger(MaintenanceService.class);
	@Autowired
	WeatherAPIDaoPackage daoPack;
	
	public void importFromAPI() throws Exception
		{
		List<Ph2DevicesEnyity> devices = this.daoPack.getDeviceStatusDomain().selectAll();
		Ph2DeviceDayEntityExample exm = new Ph2DeviceDayEntityExample();

		LOG.info("マスターデータをインポートします。");
		for (Ph2DevicesEnyity device : devices)
			{
			new WeatherAPIDomain(daoPack).importData(device);
			}
		LOG.info("マスターデータのインポートを終了します。");
		}
	}
