package com.logpose.ph2.api.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.logpose.ph2.api.dto.FruitValuesByDevice;
import com.logpose.ph2.api.dto.FruitValuesDTO;
import com.logpose.ph2.api.dto.ResponseDTO;
import com.logpose.ph2.api.dto.bearing.BearingDTO;
import com.logpose.ph2.api.dto.bearing.RealFruitesValues;
import com.logpose.ph2.api.service.FruitsService;

import jakarta.servlet.http.HttpServletRequest;

@CrossOrigin(
		origins = { "http://localhost:8080", "http://localhost:3000", "https://gokushun-ph2-it.herokuapp.com", "https://gokushun-ph2-staging-e2e7adc0c3d1.herokuapp.com" },
		methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE},
		allowedHeaders ="*", exposedHeaders="*",
		allowCredentials = "true")
@RestController
@RequestMapping(path = "/api/fruit")
public class FruitController
	{
	// ===============================================
	// クラスメンバー
	// ===============================================
	@Autowired
	private FruitsService fruitService;

	// ===============================================
	// パブリック関数群
	// ===============================================
	// --------------------------------------------------
	/**
	 * 着果実績値取得
	 *
	 * @param deviceId
	 * @param targetDate
	 * @param eventId
	 * @return Ph2RealFruitsDataEntity
	 */
	// --------------------------------------------------
	@GetMapping("/value")
	public ResponseDTO getValue(HttpServletRequest httpReq,
			@RequestParam("deviceId") Long deviceId,
			@RequestParam("year") Short year)
		{
		ResponseDTO as_dto = new ResponseDTO();
		try
			{
			RealFruitesValues as_result = this.fruitService.getRealFruitsData(deviceId,
					year);
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
	 * 実績値のクリア
	 *
	 * @return Ph2RealFruitsDataEntity
	 */
	// --------------------------------------------------
	@DeleteMapping("/value")
	public ResponseDTO deleteValue(HttpServletRequest httpReq,
			@RequestParam("deviceId") Long deviceId,
			@RequestParam("year") Short year,
			@RequestParam("eventId") Short eventId)
		{
		ResponseDTO as_dto = new ResponseDTO();
		try
			{
			 this.fruitService.deletetRealFruitsData(deviceId,year, eventId);
			 as_dto.setSuccess(null);
			}
		catch (Exception e)
			{
			as_dto.setError(e);
			}
		return as_dto;
		}

	//--------------------------------------------------
	/**
	 * 各圃場着果量着果負担詳細取得
	 *
	 * @param deviceId
	 * @param year
	 * @return ResponseDTO(FruitValuesDTO)
	 */
	//--------------------------------------------------
	@GetMapping("/values")
	public ResponseDTO detail(HttpServletRequest httpReq, 
			@RequestParam("deviceId") Long deviceId,
			@RequestParam("year") Short year)
		{
		ResponseDTO as_dto = new ResponseDTO();
		try
			{
			FruitValuesDTO as_result = this.fruitService.getFruitValues(deviceId, year);
			as_dto.setSuccess(as_result);
			}
		catch (Exception e)
			{
			as_dto.setError(e);
			}
		return as_dto;
		}
	//--------------------------------------------------
	/**
	 * 各圃場着果量着果負担詳細取得ver2
	 *
	 * @param deviceId
	 * @param year
	 * @return ResponseDTO(BearingDTO)
	 */
	//--------------------------------------------------
	@GetMapping("/details")
	public ResponseDTO getDetail2(HttpServletRequest httpReq, 
			@RequestParam("deviceId") Long deviceId,
			@RequestParam("year") Short year)
		{
		ResponseDTO as_dto = new ResponseDTO();
		try
			{
			BearingDTO as_result = this.fruitService.getFruitValues2(deviceId, year);
			as_dto.setSuccess(as_result);
			}
		catch (Exception e)
			{
			as_dto.setError(e);
			}
		return as_dto;
		}
	//--------------------------------------------------
	/**
	 * 実績値更新
	 *
	 * @param FruitAmountDTO    
	 * @return ResponseDTO(null)
	 */
	//--------------------------------------------------
	@PostMapping("/value")
	public ResponseDTO updateValue(HttpServletRequest httpReq,
			@RequestBody @Validated FruitValuesByDevice dto)
		{
		ResponseDTO as_dto = new ResponseDTO();
		try
			{
			this.fruitService.setFruitValues(dto);
			as_dto.setSuccess(null);
			}
		catch (Exception e)
			{
			as_dto.setError(e);
			}
		return as_dto;
		}
	
	}
