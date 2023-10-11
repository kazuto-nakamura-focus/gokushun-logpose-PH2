package com.logpose.ph2.api.controller;

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

import com.logpose.ph2.api.dto.FruitValuesByDevice;
import com.logpose.ph2.api.dto.FruitValuesDTO;
import com.logpose.ph2.api.dto.ResponseDTO;
import com.logpose.ph2.api.master.EventMaster;
import com.logpose.ph2.api.service.FruitsService;

import jakarta.servlet.http.HttpServletRequest;

@CrossOrigin(
		origins = { "http://localhost:8080", "https://gokushun-ph2-it.herokuapp.com:8080" },
		methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE},
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
	/// --------------------------------------------------
	/**
	 * 各圃場着果量着果負担詳細取得
	 *
	 * @param deviceId
	 * @param year
	 * @return ResponseDTO(FruitValuesDTO)
	 */
	/// --------------------------------------------------
	@GetMapping("/values")
	public ResponseDTO detail(HttpServletRequest httpReq, @RequestParam("deviceId") Long deviceId)
		{
		ResponseDTO as_dto = new ResponseDTO();
		try
			{
			FruitValuesDTO as_result = this.fruitService.getFruitValues(deviceId);
			as_dto.setSuccess(as_result);
			}
		catch (Exception e)
			{
			as_dto.setError(e);
			}
		return as_dto;
		}

	/// --------------------------------------------------
	/**
	 * 着生後芽かき処理時実績値更新
	 *
	 * @param FruitAmountDTO    
	 * @return ResponseDTO(null)
	 */
	/// --------------------------------------------------
	@PostMapping("/value/sproutTreatment")
	public ResponseDTO updateWhenSproutTreatment(HttpServletRequest httpReq,
			@RequestBody @Validated FruitValuesByDevice dto)
		{
		ResponseDTO as_dto = new ResponseDTO();
		try
			{
			dto.setEventId(EventMaster.SPROUT_TREATMENT);
			this.fruitService.setFruitValues(dto);
			as_dto.setSuccess(null);
			}
		catch (Exception e)
			{
			as_dto.setError(e);
			}
		return as_dto;
		}

	/// --------------------------------------------------
	/**
	 * E-L 27～31の生育ステージ時実績値更新処理
	 *
	 * @param FruitAmountDTO 
	 * @return ResponseDTO(FieldDetailDTO)
	 */
	/// --------------------------------------------------
	@PostMapping("/value/ELStage")
	public ResponseDTO updateWhenELStage(HttpServletRequest httpReq,
			@RequestBody @Validated FruitValuesByDevice dto)
		{
		ResponseDTO as_dto = new ResponseDTO();
		try
			{
			dto.setEventId(EventMaster.ELSTAGE27);
			this.fruitService.setFruitValues(dto);
			as_dto.setSuccess(null);
			}
		catch (Exception e)
			{
			as_dto.setError(e);
			}
		return as_dto;
		}

	/// --------------------------------------------------
	/**
	 * 袋かけ時実績値更新処理
	 *
	 * @param FruitValuesByDevice
	 * @return ResponseDTO (null)
	 */
	/// --------------------------------------------------
	@PostMapping("/value/bagging")
	public ResponseDTO updateWhenBagging(HttpServletRequest httpReq,
			@RequestBody @Validated FruitValuesByDevice dto)
		{
		ResponseDTO as_dto = new ResponseDTO();
		try
			{
			dto.setEventId(EventMaster.BAGGING);
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
