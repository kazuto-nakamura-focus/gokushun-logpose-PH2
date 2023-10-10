package com.logpose.ph2.batch.controller;

import java.io.IOException;
import java.text.ParseException;
import java.util.Date;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.logpose.ph2.batch.service.S1DeviceDataLoaderService;
import com.logpose.ph2.batch.service.S2DeviceDayService;
import com.logpose.ph2.batch.service.S3DailyBaseDataGeneratorService;
import com.logpose.ph2.batch.service.S4ModelDataApplyrService;
import com.logpose.ph2.common.dao.db.entity.Ph2DeviceDayEntity;
import com.logpose.ph2.common.dao.db.entity.Ph2DevicesEnyity;
import com.logpose.ph2.common.day.DateTimeUtility;
@Component
public class DeviceDataLoader
	{
	// ===============================================
	// クラスメンバー
	// ===============================================
	private static Logger LOG = LogManager.getLogger(DeviceDataLoader.class);
	
	//@Value("${deviceId}")
	private Long deviceId = (long) 31;
	//@Value("${isAll}")
	private boolean isAll = true;	
	//@Value("${date}")
	private String date = null;	
	
	@Autowired
	S1DeviceDataLoaderService s1deviceDataLoaderService;
	@Autowired
	S2DeviceDayService s2deviceDayService;
	@Autowired
	S3DailyBaseDataGeneratorService s3dailyBaseDataGeneratorService;
	@Autowired
	S4ModelDataApplyrService s4modelDataApplyrService;
	
	// ===============================================
	// 公開関数群
	// ===============================================
	// --------------------------------------------------------
	/**
	 * デバイスデータの生成を行う
	 * @param deviceId
	 * @param isAll
	 * @param date
	 */
	// --------------------------------------------------------
	//public void load(Long deviceId, boolean isAll, String date) 
	public void load() 
		{
// * デバイスの指定が無い場合、全てのデバイスを対象とする
		if(null == deviceId)
			{
			List<Ph2DevicesEnyity>  devices = this.s1deviceDataLoaderService.getDeviceAllInfo();
			for(Ph2DevicesEnyity device : devices)
				{
				this.loadDevice(device.getId(), this.isAll, this.date);
				}
			}
		else
			{
			this.loadDevice(this.deviceId, this.isAll, this.date);
			}
		}
	
	public void loadDevice(Long deviceId, boolean isAll, String date)
		{
//* 開始日の設定
		Date startDate = null;
		if(!isAll)
			{
			if(date == null)
				{
				// * 日付の指定が無い場合は、未処理の日付を取得する。
				startDate = this.s1deviceDataLoaderService.getLatest(deviceId);
				}
			else
				{
				try
					{
					startDate = DateTimeUtility.getDateFromString(date);
					}
				catch (ParseException e)
					{
					// TODO 自動生成された catch ブロック
					e.printStackTrace();
					}
				}
			}
		try
			{
			//* デバイス情報の取得
			Ph2DevicesEnyity device = this.s1deviceDataLoaderService.getDeviceInfo(deviceId);
			//* メッセージから基本情報の取得
			startDate = s1deviceDataLoaderService.loadMessages(device, startDate);
			// * 日付をまたがった場合、以下の処理を行う
			if(null != null )
				{
				List<Ph2DeviceDayEntity> deviceDays = this.s2deviceDayService.initDeviceDay(device, startDate);
				this.s3dailyBaseDataGeneratorService.doService(deviceDays, startDate);
				this.s4modelDataApplyrService.doService(deviceDays, startDate);
				}
			}
		catch (IOException | ParseException e)
			{
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
			return;
			}

		}
	}
