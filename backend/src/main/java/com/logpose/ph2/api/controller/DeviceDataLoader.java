package com.logpose.ph2.api.controller;

import java.io.IOException;
import java.text.ParseException;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.logpose.ph2.api.bulk.service.S0Initializer;
import com.logpose.ph2.api.bulk.service.S1DeviceDataLoaderService;
import com.logpose.ph2.api.bulk.service.S2DeviceDayService;
import com.logpose.ph2.api.bulk.service.S3DailyBaseDataGeneratorService;
import com.logpose.ph2.api.bulk.service.S4ModelDataApplyrService;
import com.logpose.ph2.api.bulk.vo.LoadCoordinator;
import com.logpose.ph2.api.controller.dto.DataLoadDTO;
import com.logpose.ph2.api.dao.db.entity.Ph2DeviceDayEntity;
import com.logpose.ph2.api.dao.db.entity.Ph2DevicesEnyity;
import com.logpose.ph2.api.dto.ResponseDTO;

import jakarta.servlet.http.HttpServletRequest;

@CrossOrigin(origins = { "http://localhost:8080",
		"http://localhost:3000",
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
	S0Initializer s0Initializer;
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
	@GetMapping("/update")
	public ResponseDTO masters(HttpServletRequest httpReq)
		{
		ResponseDTO as_dto = new ResponseDTO();
		try
			{
// * 全てのデバイス情報を取得し、各デバイスに対して処理を行う
			List<Ph2DevicesEnyity> devices = this.s0Initializer.getDeviceAllInfo();
			for (Ph2DevicesEnyity device : devices)
				{
// * コーディネーターを生成する
				LoadCoordinator ldc = this.s0Initializer.initializeCoordinator(device,
						false,
						null);
// * コーディネーターを引数にデータロードを実行する
				this.loadDevice(ldc);
				}
			as_dto.setSuccess(null);
			}
		catch (Exception e)
			{
			as_dto.setError(e);
			}
		return as_dto;
		}

	// --------------------------------------------------------
	/**
	 * デバイスデータの生成を行う
	 * @param deviceId
	 * @param isAll
	 * @param date
	 */
	// --------------------------------------------------------
	@PostMapping("/load")
	public ResponseDTO masters(HttpServletRequest httpReq,
			@RequestBody @Validated DataLoadDTO dto)
		{
		ResponseDTO as_dto = new ResponseDTO();
		try
			{
// * デバイスの指定が無い場合、全てのデバイスを対象とする
			if (null == dto.getDeviceId())
				{
				List<Ph2DevicesEnyity> devices = this.s0Initializer.getDeviceAllInfo();
				for (Ph2DevicesEnyity device : devices)
					{
					LoadCoordinator ldc = this.s0Initializer.initializeCoordinator(device,
							dto.getIsAll(),
							dto.getStartDate());
					this.loadDevice(ldc);
					}
				}
			else
				{
				LoadCoordinator ldc = this.s0Initializer.initializeCoordinator(dto.getDeviceId(),
						dto.getIsAll(),
						dto.getStartDate());
				this.loadDevice(ldc);
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
	private void loadDevice(LoadCoordinator ldc) throws IOException, ParseException
		{
		if (!ldc.isLoadable()) return;
		LOG.info("デバイスデータのローディングを開始します。:" + ldc.getDeviceId());

// * メッセージから基本情報の取得
		s1deviceDataLoaderService.loadMessages(ldc);
// * 日付をまたがった場合、以下の処理を行う
		List<Ph2DeviceDayEntity> deviceDays = this.s2deviceDayService.initDeviceDay(ldc);
		if (deviceDays.size() > 0)
			{
			deviceDays =this.s3dailyBaseDataGeneratorService.doService(ldc, deviceDays);
			this.s4modelDataApplyrService.doService(ldc.getDeviceId(), deviceDays);
			}

		LOG.info("デバイスデータのローディングが終了しました。");
		}
	}
