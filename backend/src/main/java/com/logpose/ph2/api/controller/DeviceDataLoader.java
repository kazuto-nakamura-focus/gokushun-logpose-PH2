package com.logpose.ph2.api.controller;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.logpose.ph2.api.controller.dto.DataLoadDTO;
import com.logpose.ph2.api.dao.db.entity.Ph2DevicesEntity;
import com.logpose.ph2.api.dto.ResponseDTO;
import com.logpose.ph2.api.dto.element.ObjectStatus;
import com.logpose.ph2.api.service.DataLoadService;

import jakarta.servlet.http.HttpServletRequest;

@CrossOrigin(origins = { "http://localhost:8080", "http://localhost:3000",
		"https://gokushun-ph2-it.herokuapp.com" }, methods = { RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT,
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
	private DataLoadService dataLoadService;

	// ===============================================
	// 公開関数群
	// ===============================================
	// --------------------------------------------------------
	/**
	 * 全デバイスの最新のセンサーデータを加工し、DBへロードする。
	 * モデルデータを生成し、DBに格納する。
	 */
	// --------------------------------------------------------
	@GetMapping("/update")
	public ResponseDTO masters(HttpServletRequest httpReq)
		{
		LOG.info("/api/bulk/update の実行開始");
		ResponseDTO as_dto = new ResponseDTO();
		try
			{
			this.dataLoadService.updateData();
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
	 * 全デバイスの全てのセンサーデータを加工し、DBへロードする。
	 * モデルデータを生成し、DBに格納する。
	 * @param dto パラメータ
	 */
	// --------------------------------------------------------
	@PostMapping("/load")
	public ResponseDTO masters(HttpServletRequest httpReq,
			@RequestBody @Validated DataLoadDTO dto)
		{
		LOG.info("/api/bulk/load の実行開始");
		ResponseDTO as_dto = new ResponseDTO();
		try
			{
			this.dataLoadService.createAllData();
			as_dto.setSuccess(null);
			}
		catch (Exception e)
			{
			as_dto.setError(e);
			}
		LOG.info("/api/bulk/load の実行終了");
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
			List<Ph2DevicesEntity> fisnishList = this.dataLoadService.getSchedule();
			as_dto.setSuccess(fisnishList);
			}
		catch (Exception e)
			{
			as_dto.setError(e);
			}
		LOG.info("/api/bulk/load の実行終了");
		return as_dto;
		}

	// --------------------------------------------------------
	/**
	 * 指定されたデバイスの全てのセンサーデータを加工し、DBへロードする。
	 * モデルデータを生成し、DBに格納する。
	 */
	// --------------------------------------------------------
	@GetMapping("/load/device/{deviceId}")
	public ResponseDTO load(HttpServletRequest httpReq,
			@PathVariable Long deviceId)
		{
		LOG.info("/api/bulk/load の実行開始");
		ResponseDTO as_dto = new ResponseDTO();
		try
			{
			List<ObjectStatus> fisnishList = this.dataLoadService.createData(deviceId);
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
