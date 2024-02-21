package com.logpose.ph2.api.maintenance;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.logpose.ph2.api.algorythm.DeviceDayAlgorithm;
import com.logpose.ph2.api.bulk.domain.DeviceStatusDomain;
import com.logpose.ph2.api.configration.DefaultWeatherlAPIParameters;
import com.logpose.ph2.api.dao.db.mappers.Ph2DeviceDayMapper;
import com.logpose.ph2.api.dao.db.mappers.Ph2FieldsMapper;
import com.logpose.ph2.api.dao.db.mappers.Ph2WeatherDailyMasterMapper;

import lombok.Getter;

@Component
@Getter
public class WeatherAPIDaoPackage
	{
	@Autowired
	private DeviceDayAlgorithm deviceDayAlgorithm;
	@Autowired
	private DefaultWeatherlAPIParameters params;
	@Autowired
	private DeviceStatusDomain deviceStatusDomain;
	@Autowired
	private Ph2DeviceDayMapper ph2DeviceDayMapper;
	@Autowired
	private Ph2FieldsMapper ph2FieldsMapper;
	@Autowired
	private Ph2WeatherDailyMasterMapper ph2WeatherDailyMasterMapper;
	}
