package com.logpose.ph2.api.controller;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.logpose.ph2.api.controller.dto.DeviceDateDTO;
import com.logpose.ph2.api.controller.dto.TargetParamDTO;
import com.logpose.ph2.api.dto.PhotosynthesisParamSetDTO;
import com.logpose.ph2.api.dto.PhotosynthesisValueDTO;
import com.logpose.ph2.api.dto.RealModelGraphDataDTO;
import com.logpose.ph2.api.dto.ResponseDTO;
import com.logpose.ph2.api.service.PhotosynthesisService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@CrossOrigin(
		origins = { "http://localhost:8080", "http://localhost:3000", "https://gokushun-ph2-it.herokuapp.com" },
		methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE},
		allowCredentials = "true")
@RestController
@RequestMapping(path = "/api/photosynthesis")
public class PhotosynthesisController
	{
	// ===============================================
	// クラスメンバー
	// ===============================================
	@Autowired
	private PhotosynthesisService photosynthesisService;

	// ===============================================
	// パブリック関数群
	// ===============================================
	// --------------------------------------------------
	/**
	 * 葉面積・葉枚数モデルデータ作成(バッチよりコール)
	 *
	 * @param DeviceDateDTO デバイスIDと日付
	 * @return ResponseDTO(GraphDataDTO)
	 */
	// --------------------------------------------------
	@PutMapping("/graph/updateModelData")
	public ResponseDTO updateModelData(HttpServletRequest httpReq,
			HttpServletResponse res,
			@RequestBody @Validated DeviceDateDTO dto)
		{
		ResponseDTO as_dto = new ResponseDTO();
		try
			{
			this.photosynthesisService.updateDateModel(dto.getDeviceId(),
					dto.getYear(),
					dto.getDate());
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
	 * 光合成推定グラフデータ取得
	 *
	 * @param fieldId 圃場ID
	 * @param year 年度
	 * @return ResponseDTO(GraphDataDTO)
	 */
	// --------------------------------------------------
	@GetMapping("/graph/byParamSet")
	public ResponseDTO modelGraph(HttpServletRequest httpReq,
			HttpServletResponse res,
			@RequestParam("deviceId") Long fieldId,
			@RequestParam("year") Short year)
		{
		ResponseDTO as_dto = new ResponseDTO();
		try
			{
			RealModelGraphDataDTO as_result = this.photosynthesisService
					.GetModelGraphData(fieldId, year);
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
	 * 光合成推定実績値取得
	 *
	 * @param dto PhotosynthesisValueDTO
	 * @return ResponseDTO (null)
	 */
	// --------------------------------------------------
	@GetMapping("/values")
	public ResponseDTO getValues(HttpServletRequest httpReq,
			HttpServletResponse res,
			@RequestParam("deviceId") Long deviceId,
			@RequestParam("year") Short year,
			@RequestParam("date") Date date)
		{
		ResponseDTO as_dto = new ResponseDTO();
		try
			{
			PhotosynthesisValueDTO result = this.photosynthesisService
					.getRealValues(deviceId, year, date);
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
	 * 光合成推定実績値更新
	 *
	 * @param dto PhotosynthesisValueDTO
	 * @return ResponseDTO (null)
	 */
	// --------------------------------------------------
	@PutMapping("/values")
	public ResponseDTO updateValues(HttpServletRequest httpReq,
			HttpServletResponse res,
			@RequestBody @Validated PhotosynthesisValueDTO dto)
		{
		ResponseDTO as_dto = new ResponseDTO();
		try
			{
			this.photosynthesisService.setRealValue(dto);
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
	 * 光合成推定パラメータセット詳細取得
	 *
	 * @param paramSetId パラメータセットID
	 * @return ResponseDTO (PhotosynthesisParamSetDTO)
	 */
	// --------------------------------------------------
	@GetMapping("/paramSet/detail")
	public ResponseDTO detail(HttpServletRequest httpReq,
			@RequestParam("paramSetId") Long paramSetId)
		{
		ResponseDTO as_dto = new ResponseDTO();
		try
			{
			paramSetId = (paramSetId == 0) ? null : paramSetId;
			PhotosynthesisParamSetDTO as_result = this.photosynthesisService
					.getDetailParamSet(paramSetId);
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
	 * 光合成推定パラメータセット更新
	 *
	 * @param dto PhotosynthesisParamSetDTO
	 * @return ResponseDTO (null)
	 */
	// --------------------------------------------------
	@PutMapping("/paramSet")
	public ResponseDTO put(HttpServletRequest httpReq,
			HttpServletResponse res,
			@RequestBody @Validated PhotosynthesisParamSetDTO dto)
		{
		ResponseDTO as_dto = new ResponseDTO();
		try
			{
			this.photosynthesisService.updateParamSet(dto);
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
	 * 光合成推定パラメータセット追加
	 *
	 * @param dto PhotosynthesisParamSetDTO
	 * @return ResponseDTO (null)
	 */
	// --------------------------------------------------
	@PostMapping("/paramSet")
	public ResponseDTO post(HttpServletRequest httpReq,
			HttpServletResponse res,
			@RequestBody @Validated PhotosynthesisParamSetDTO dto)
		{
		ResponseDTO as_dto = new ResponseDTO();
		try
			{
			Long newId = this.photosynthesisService.addParamSet(dto);
			as_dto.setSuccess(newId);
			}
		catch (Exception e)
			{
			as_dto.setError(e);
			}

		return as_dto;
		}
	// --------------------------------------------------
	/**
	 * 基準パラメータセットの設定
	 *
	 * @param dto TargetParamDTO
	 */
	// --------------------------------------------------
	@PutMapping("/paramSet/default")
	public ResponseDTO setDefaut(HttpServletRequest httpReq,
			HttpServletResponse res,
			@RequestBody @Validated TargetParamDTO dto)
		{
		ResponseDTO as_dto = new ResponseDTO();
		try
			{
			this.photosynthesisService.setDefault(dto.getDeviceId(), dto.getYear(),
					dto.getParamId());
			as_dto.setSuccess(null);
			}
		catch (Exception e)
			{
			as_dto.setError(e);
			}

		return as_dto;
		}
	}
