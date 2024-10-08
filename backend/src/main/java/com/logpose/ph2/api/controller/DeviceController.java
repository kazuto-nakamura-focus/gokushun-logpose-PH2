package com.logpose.ph2.api.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.logpose.ph2.api.dto.DeviceInfoDTO;
import com.logpose.ph2.api.dto.ResponseDTO;
import com.logpose.ph2.api.dto.device.DeviceDetailDTO;
import com.logpose.ph2.api.dto.device.DeviceMastersDTO;
import com.logpose.ph2.api.dto.device.DeviceShortDTO;
import com.logpose.ph2.api.dto.device.DeviceUpdateDTO;
import com.logpose.ph2.api.service.DeviceService;

import jakarta.servlet.http.HttpServletRequest;

@CrossOrigin(
		origins = { "http://localhost:8080", "http://localhost:3000", "https://gokushun-ph2-it.herokuapp.com", "https://gokushun-ph2-staging-e2e7adc0c3d1.herokuapp.com" },
		methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE},
		allowedHeaders ="*", exposedHeaders="*",
		allowCredentials = "true")
@RestController
@RequestMapping(path = "/api/device")
public class DeviceController
	{
	// ===============================================
	// クラスメンバー
	// ===============================================
	@Autowired
	private DeviceService deviceService;

	// ===============================================
	// パブリック関数群
	// ===============================================
	/** --------------------------------------------------
	 * デバイス関連のマスター情報を取得する。
	 *
	 * @return ResponseDTO (DeviceMasterDTO)
	 ------------------------------------------------------ */
	@GetMapping("/masters")
	public ResponseDTO masters(HttpServletRequest httpReq)
		{
		ResponseDTO as_dto = new ResponseDTO();
		try
			{
			DeviceMastersDTO result = this.deviceService.getMasters();
			as_dto.setSuccess(result);
			}
		catch (Exception e)
			{
			as_dto.setError(e);
			}
		return as_dto;		
		}
	/** --------------------------------------------------
	 * デバイス一覧取得
	 *
	 * @return ResponseDTO(List<DeviceInfoDTO>)
	 ------------------------------------------------------ */
	@GetMapping("/list")
	public ResponseDTO list(HttpServletRequest httpReq)
		{
		ResponseDTO as_dto = new ResponseDTO();
		try
			{
			List<DeviceInfoDTO> result = this.deviceService.list();
			as_dto.setSuccess(result);
			}
		catch (Exception e)
			{
			as_dto.setError(e);
			}
		return as_dto;
		}
	/** --------------------------------------------------
	 * デバイス簡易一覧取得
	 *
	 * @return ResponseDTO(List<DeviceShortDTO>)
	 ------------------------------------------------------ */
	@GetMapping("/list-short")
	public ResponseDTO listShort(HttpServletRequest httpReq)
		{
		ResponseDTO as_dto = new ResponseDTO();
		try
			{
			List<DeviceShortDTO> result = this.deviceService.listShort();
			as_dto.setSuccess(result);
			}
		catch (Exception e)
			{
			as_dto.setError(e);
			}
		return as_dto;
		}
	
	// --------------------------------------------------
	/**
	 * デバイス削除
	 *
	 * @param deviceId デバイスID
	 * @return ResponseDTO(null)
	 */
	// --------------------------------------------------
	@DeleteMapping("/info")
	public ResponseDTO delete(HttpServletRequest httpReq, @RequestParam("deviceId") Long deviceId)
		{
		ResponseDTO as_dto = new ResponseDTO();
		try
			{
			this.deviceService.delete(deviceId);
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
	 * デバイス情報詳細取得
	 *
	 * @param deviceId デバイスID
	 * @return ResponseDTO(DeviceDetailDTO)
	 */
	// --------------------------------------------------
	@GetMapping("/info")
	public ResponseDTO get(HttpServletRequest httpReq, @RequestParam("deviceId") Long deviceId)
		{
		ResponseDTO as_dto = new ResponseDTO();
		try
			{
			DeviceDetailDTO as_result = this.deviceService.getDetail(deviceId);
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
	 * デバイス情報追加
	 *
	 * @param dto DeviceDetailDTO
	 * @return ResponseDTO (null)
	 */
	// --------------------------------------------------
	@PostMapping("/info")
	public ResponseDTO post(HttpServletRequest httpReq, @RequestBody @Validated DeviceUpdateDTO dto)
		{
		ResponseDTO as_dto = new ResponseDTO();
		try
			{
			this.deviceService.addInfo(dto);
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
	 * デバイス情報更新
	 *
	 * @param dto DeviceDetailDTO
	 * @return ResponseDTO (null)
	 */
	// --------------------------------------------------
	@PutMapping("/info")
	public ResponseDTO put(HttpServletRequest httpReq, @RequestBody @Validated DeviceUpdateDTO dto)
		{
		ResponseDTO as_dto = new ResponseDTO();
		try
			{
			this.deviceService.updateInfo(dto);
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
	 * デバイス情報の引継ぎ
	 *
	 * @param dstId 引継ぎ先デバイスID
	 * @return ResponseDTO (null)
	 */
	// --------------------------------------------------
	@PutMapping("/parameters")
	public ResponseDTO updateParameters(HttpServletRequest httpReq,
			@RequestParam("deviceId") Long deviceId)
		{
		ResponseDTO as_dto = new ResponseDTO();
		try
			{
			this.deviceService.transitParameters(deviceId);
			as_dto.setSuccess(null);
			}
		catch (Exception e)
			{
			as_dto.setError(e);
			}
		return as_dto;
		}
	
	
	}
