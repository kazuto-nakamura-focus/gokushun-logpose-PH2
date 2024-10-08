package com.logpose.ph2.api.controller;

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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.logpose.ph2.api.controller.dto.TimeMessage;
import com.logpose.ph2.api.dao.db.entity.Ph2DevicesEntity;
import com.logpose.ph2.api.dto.ResponseDTO;
import com.logpose.ph2.api.dto.dataLoad.DataLoadInfo;
import com.logpose.ph2.api.service.data_load.DataLoadTopService;

import jakarta.servlet.http.HttpServletRequest;

@CrossOrigin(origins = { "http://localhost:8080", "http://localhost:3000", "https://gokushun-ph2-it.herokuapp.com",
		"https://gokushun-ph2-staging-e2e7adc0c3d1.herokuapp.com" }, methods = { RequestMethod.GET, RequestMethod.POST,
				RequestMethod.PUT,
				RequestMethod.DELETE }, allowedHeaders = "*", exposedHeaders = "*", allowCredentials = "true")
@RestController
@RequestMapping(path = "/api/bulk")
public class DeviceDataLoader
	{
	// ===============================================
	// クラスメンバー
	// ===============================================	
	private static Logger LOG = LogManager.getLogger(DeviceDataLoader.class);
	@Autowired
	private DataLoadTopService dataLoadTopService;

	// ===============================================
	// 公開関数群
	// ===============================================
	// --------------------------------------------------------
	/**
	 * デバイスのロード情報を得る
	 * @return List<String>
	 */
	// --------------------------------------------------------
	@GetMapping("/load/info")
	public ResponseDTO load(HttpServletRequest httpReq)
		{
		ResponseDTO as_dto = new ResponseDTO();
		try
			{
			List<DataLoadInfo> fisnishList = this.dataLoadTopService.getInfo();
			as_dto.setSuccess(fisnishList);
			}
		catch (Exception e)
			{
			as_dto.setError(e);
			}
		return as_dto;
		}
	// --------------------------------------------------------
	/**
	 * センサーロードのログ情報を得る
	 * @param デバイスID
	 * @return List<TimeMessage>
	 */
	// --------------------------------------------------------
	@GetMapping("/load/log")
	public ResponseDTO log(HttpServletRequest httpReq, 	@RequestParam("deviceId") Long deviceId,
			@RequestParam("type") Short type)
		{
		ResponseDTO as_dto = new ResponseDTO();
		try
			{
			List<TimeMessage> logData = this.dataLoadTopService.getLog(deviceId, type);
			as_dto.setSuccess(logData);
			}
		catch (Exception e)
			{
			as_dto.setError(e);
			}
		return as_dto;
		}
	// --------------------------------------------------------
	/**
	 * デバイス更新のリクエストを要求する
	 */
	// --------------------------------------------------------
	@PostMapping("/load/device")
	public ResponseDTO request(HttpServletRequest httpReq,
			@RequestBody @Validated Long  deviceId)
		{
		ResponseDTO as_dto = new ResponseDTO();
		try
			{
			this.dataLoadTopService.request(deviceId);
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
	 * 全デバイスの最新のセンサーデータを加工し、DBへロードする。
	 * モデルデータを生成し、DBに格納する。
	 */
	// --------------------------------------------------------
	@GetMapping("/update")
	public ResponseDTO update(HttpServletRequest httpReq)
		{
		LOG.info("/api/bulk/update の実行開始");
		ResponseDTO as_dto = new ResponseDTO();
		try
			{
			this.dataLoadTopService.update();
			as_dto.setSuccess(null);
			}
		catch (Exception e)
			{
			as_dto.setError(e);
			}
		LOG.info("/api/bulk/update の実行終了");
		return as_dto;
		}


	// --------------------------------------------------------
	/**
	 * デバイスのロードスケジュールを得る
	 */
	// --------------------------------------------------------
	@GetMapping("/load/schedule")
	public ResponseDTO schedule(HttpServletRequest httpReq)
		{
		LOG.info("/api/bulk/load の実行開始");
		ResponseDTO as_dto = new ResponseDTO();
		try
			{
			List<Ph2DevicesEntity> fisnishList = this.dataLoadTopService.getSchedule();
			as_dto.setSuccess(fisnishList);
			}
		catch (Exception e)
			{
			as_dto.setError(e);
			}
		LOG.info("/api/bulk/load の実行終了");
		return as_dto;
		}

	}
