package com.logpose.ph2.api.controller;

import java.io.IOException;
import java.text.ParseException;
import java.util.Date;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.logpose.ph2.api.bulk.service.S1DeviceDataLoaderService;
import com.logpose.ph2.api.bulk.service.S2DeviceDayService;
import com.logpose.ph2.api.bulk.service.S3DailyBaseDataGeneratorService;
import com.logpose.ph2.api.bulk.service.S4ModelDataApplyrService;
import com.logpose.ph2.api.dto.ResponseDTO;
import com.logpose.ph2.batch.dao.db.entity.Ph2DeviceDayEntity;
import com.logpose.ph2.batch.dao.db.entity.Ph2DevicesEnyity;

import jakarta.servlet.http.HttpServletRequest;

@CrossOrigin(origins = { "http://localhost:8080",
		"https://gokushun-ph2-it.herokuapp.com" }, methods = { RequestMethod.GET,
				RequestMethod.POST, RequestMethod.PUT,
				RequestMethod.DELETE }, allowCredentials = "true")
@RestController
@RequestMapping(path = "/api/bulk")
public class DeviceDataLoader
	{
	// ===============================================
	// クラスメンバー
	// ===============================================
	private static Logger LOG = LogManager.getLogger(DeviceDataLoader.class);

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
	@PostMapping("/masters")
	public ResponseDTO masters(HttpServletRequest httpReq,
			@RequestParam("deviceId") Long deviceId,
			@RequestParam("isAll") Boolean isAll,
			@DateTimeFormat(iso = DateTimeFormat.ISO.DATE) @RequestParam("startDate") Date startDate)
		{
		ResponseDTO as_dto = new ResponseDTO();
		try
			{
// * デバイスの指定が無い場合、全てのデバイスを対象とする
			if (null == deviceId)
				{
				List<Ph2DevicesEnyity> devices = this.s1deviceDataLoaderService.getDeviceAllInfo();
				for (Ph2DevicesEnyity device : devices)
					{
					this.loadDevice(device.getId(), isAll, startDate);
					}
				}
			else
				{
				this.loadDevice(deviceId, isAll, startDate);
				}
			as_dto.setSuccess(null);
			}
		catch (Exception e)
			{
			as_dto.setError(e);
			}
		return as_dto;
		}

	// ===============================================
	// 非公開関数群
	// ===============================================
	private void loadDevice(Long deviceId, boolean isAll, Date startDate) throws IOException, ParseException
		{
		LOG.info("デバイスデータのローディングを開始します。:" + deviceId);
// * 開始日の設定
		if (!isAll)
			{
			if (startDate == null)
				{
				// * 日付の指定が無い場合は、未処理の日付を取得する。
				startDate = this.s1deviceDataLoaderService.getLatest(deviceId);
				}
			}

		// * デバイス情報の取得
		Ph2DevicesEnyity device = this.s1deviceDataLoaderService.getDeviceInfo(deviceId);
		// * デバイス情報に開始日・タイムゾーン情報が無い場合終了
		if ((null == device.getBaseDate()) || (null == device.getTz()))
			{
			return;
			}

		// * メッセージから基本情報の取得
		startDate = s1deviceDataLoaderService.loadMessages(device, startDate);
		// * 日付をまたがった場合、以下の処理を行う
		if (null != startDate)
			{
			List<Ph2DeviceDayEntity> deviceDays = this.s2deviceDayService.initDeviceDay(device,
					startDate);
			this.s3dailyBaseDataGeneratorService.doService(deviceDays, startDate);
			this.s4modelDataApplyrService.doService(deviceDays, startDate);
			}

		LOG.info("デバイスデータのローディングが終了しました。");
		}
	}
