package com.logpose.ph2.api.controller;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.logpose.ph2.api.dao.db.entity.joined.SensorItemDTO;
import com.logpose.ph2.api.dto.ResponseDTO;
import com.logpose.ph2.api.dto.sensorData.SensorDataDTO;
import com.logpose.ph2.api.service.SensorDataService;

import jakarta.servlet.http.HttpServletRequest;

@CrossOrigin(origins = { "http://localhost:8080", "http://localhost:3000", "https://gokushun-ph2-it.herokuapp.com",
		"https://gokushun-ph2-staging-e2e7adc0c3d1.herokuapp.com" }, methods = { RequestMethod.GET, RequestMethod.POST,
				RequestMethod.PUT,
				RequestMethod.DELETE }, allowedHeaders = "*", exposedHeaders = "*", allowCredentials = "true")
@RestController
@RequestMapping(path = "/api/sensor/")
public class SensorDataController
	{
	// ===============================================
	// クラスメンバー
	// ===============================================
	@Autowired
	private SensorDataService sensorDataService;

	// ===============================================
	// パブリック関数群
	// ===============================================
	// --------------------------------------------------
	/**
	 * デバイスに属するセンサーの一覧を取得する。
	 * コンテンツ名・センサー名とIDを返す。
	 * 	
	 * @param deviceId-デバイスID
	 * @return List<SensorItemDTO>
	 */
	// --------------------------------------------------
	@GetMapping("list")
	public ResponseDTO getSensorItemsByDevice(HttpServletRequest httpReq,
			@RequestParam("deviceId") Long deviceId)
		{
		ResponseDTO as_dto = new ResponseDTO();
		try
			{
			List<SensorItemDTO> as_result = this.sensorDataService.getSensorItemsByDevice(deviceId);
			as_dto.setSuccess(as_result);
			}
		catch (Exception e)
			{
			as_dto.setError(e);
			}
		return as_dto;
		}

	// --------------------------------------------------
	/**
	 * ある期間内のセンサーのデータを返す。
	 * 	
	 * @param sensorId - センサーID
	 * @param startDate - 取得期間の開始日
	 * @paraｍ endDate - 取得期間の終了日
	 * @param interval - 取得時間間隔
	 * @return GraphDataDTO
	 */
	// --------------------------------------------------
	@GetMapping("graph")
	public ResponseDTO getSensorGraphData(
			HttpServletRequest httpReq,
			@RequestParam("deviceId") Long deviceId,
			@RequestParam("sensorId") Long sensorId,
			@DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date startDate,
			@DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date endDate,
			@RequestParam("interval") Long interval)
		{
		ResponseDTO as_dto = new ResponseDTO();
		try
			{
			SensorDataDTO as_result = this.sensorDataService.getSensorGraphDataByInterval(deviceId, sensorId, startDate, endDate,
					interval);
			as_dto.setSuccess(as_result);
			}
		catch (Exception e)
			{
			as_dto.setError(e);
			}
		return as_dto;

		}
	}
