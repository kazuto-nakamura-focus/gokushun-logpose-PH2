package com.logpose.ph2.api.controller;

import java.util.List;

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

import com.logpose.ph2.api.dao.db.entity.Ph2DashBoardDisplayEntity;
import com.logpose.ph2.api.dto.ResponseDTO;
import com.logpose.ph2.api.dto.dashboard.DashBoardSensorsDTO;
import com.logpose.ph2.api.dto.dashboard.DashboardSensorMenuDTO;
import com.logpose.ph2.api.dto.dashboard.DashboardTarget;
import com.logpose.ph2.api.service.DashboardService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@CrossOrigin(
		origins = { "http://localhost:8080", "http://localhost:3000", "https://gokushun-ph2-it.herokuapp.com" },
		methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE},
		allowedHeaders ="*", exposedHeaders="*",
		allowCredentials = "true")
@RestController
@RequestMapping(path = "/api/dashboard")
public class DashboardController
	{
	// ===============================================
	// クラスメンバー
	// ===============================================
	@Autowired
	private DashboardService dashboardService;

	// ===============================================
	// パブリック関数群
	// ===============================================
	// --------------------------------------------------
	/**
	 * 設定対象となるデバイスリストを取得する
	 *
	 * @return ResponseDTO(DashboardTarget)
	 */
	// --------------------------------------------------
	@GetMapping("/devices")
	public ResponseDTO getDeviceTarget(HttpServletRequest httpReq, HttpServletResponse res)
		{
		ResponseDTO as_dto = new ResponseDTO();
		try
			{
			List<DashboardTarget> as_result = this.dashboardService.getFieldData();
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
	 * 指定されたデバイスデータのセンサー情報を取得する
	 *
	 * @param detectId デバイスID
	 * @return ResponseDTO(DashBoardSensorsDTO)
	 */
	// --------------------------------------------------
	@GetMapping("/sensors")
	public ResponseDTO getSensorList(HttpServletRequest httpReq, HttpServletResponse res,
			@RequestParam("deviceId") Long deviceId)
		{
		ResponseDTO as_dto = new ResponseDTO();
		try
			{
			DashboardSensorMenuDTO as_result = this.dashboardService.getSensorList(deviceId);
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
	 * デバイスの表示情報の変更
	 * @param dto
	 */
	// --------------------------------------------------
	@PostMapping("/device")
	public ResponseDTO updateDisplay(HttpServletRequest httpReq, @RequestBody @Validated Ph2DashBoardDisplayEntity dto)
		{
		ResponseDTO as_dto = new ResponseDTO();
		try
			{
			this.dashboardService.updateDisplay(dto);
			as_dto.setSuccess(null);
			}
		catch (Exception e)
			{
			as_dto.setError(e);
			}
		return as_dto;
		}
	// --------------------------------------------------
	/**
	 * センサー情報を更新する
	 * @param dto
	 */
	// --------------------------------------------------
	@PostMapping("/sensors")
	public ResponseDTO updateSettings(HttpServletRequest httpReq, @RequestBody @Validated DashBoardSensorsDTO dto)
		{
		ResponseDTO as_dto = new ResponseDTO();
		try
			{
			this.dashboardService.updateSettings(dto);
			as_dto.setSuccess(null);
			}
		catch (Exception e)
			{
			as_dto.setError(e);
			}
		return as_dto;
		}
	}
