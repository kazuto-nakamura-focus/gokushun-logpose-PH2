package com.logpose.ph2.api.controller;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
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

import com.logpose.ph2.api.controller.dto.TargetParamDTO;
import com.logpose.ph2.api.dao.db.entity.Ph2RealGrowthFStageEntity;
import com.logpose.ph2.api.dto.EventDaysDTO;
import com.logpose.ph2.api.dto.FDataListDTO;
import com.logpose.ph2.api.dto.GrowthParamSetDTO;
import com.logpose.ph2.api.dto.ResponseDTO;
import com.logpose.ph2.api.dto.ValueDateDTO;
import com.logpose.ph2.api.dto.graph.ModelGraphDataDTO;
import com.logpose.ph2.api.dto.growth.FValuesDTO;
import com.logpose.ph2.api.service.GrowthService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@CrossOrigin(
		origins = { "http://localhost:8080", "http://localhost:3000", "https://gokushun-ph2-it.herokuapp.com", "https://gokushun-ph2-staging-e2e7adc0c3d1.herokuapp.com" },
		methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE},
		allowedHeaders ="*", exposedHeaders="*",
		allowCredentials = "true")
@RestController
@RequestMapping(path = "/api/growth")
public class GrowthController
	{
	// ===============================================
	// クラスメンバー
	// ===============================================
	@Autowired
	private GrowthService growthService;

	// ===============================================
	// パブリック関数群
	// ===============================================
	// --------------------------------------------------
	/**
	 * 生育推定モデルグラフデータ取得
	 *
	 * @param deviceId 圃場ID
	 * @param year 年度
	 * @return ResponseDTO(GraphDataDTO)
	 */
	// --------------------------------------------------
	@GetMapping("/graph")
	public ResponseDTO modelGraph(HttpServletRequest httpReq,
			HttpServletResponse res,
			@RequestParam("deviceId") Long deviceId,
			@RequestParam("year") Short year)
		{
		ResponseDTO as_dto = new ResponseDTO();
		try
			{
			ModelGraphDataDTO as_result = this.growthService.getModelGraph(deviceId,
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
	 * 生育推定モデルグラフデータパラメータセット指定取得
	 *
	 * @param deviceId 圃場ID
	 * @param year 年度
	 * @return ResponseDTO(GraphDataDTO)
	 */
	// --------------------------------------------------
/*	@GetMapping("/graph/byParamSet")
	public ResponseDTO modelGraphByParam(HttpServletRequest httpReq,
			@RequestParam("deviceId") Long deviceId,
			@RequestParam("year") Short year,
			@RequestParam("paramId") Long paramId)
		{
		ResponseDTO as_dto = new ResponseDTO();
		try
			{
			GraphDataDTO as_result = this.growthService
					.getSumilateGraph(deviceId, year, paramId);
			as_dto.setSuccess(as_result);
			}
		catch (Exception e)
			{
			as_dto.setError(e);
			}
		return as_dto;
		}*/

	// --------------------------------------------------
	/**
	 * 生育推定イベントデータ取得(未使用中)
	 *
	 * @param deviceId 圃場ID
	 * @param year 年度
	 * @param paramSetId パラメータセットID
	 * @return ResponseDTO(EventDaysDTO)
	 */
	// --------------------------------------------------
	@GetMapping("/event")
	public ResponseDTO event(HttpServletRequest httpReq,
			@RequestParam("deviceId") Long deviceId,
			@RequestParam("year") Short year)
		{
		ResponseDTO as_dto = new ResponseDTO();
		try
			{
			List<EventDaysDTO> as_result = this.growthService.getEvent(deviceId,
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
	 * 生育推定F値データ取得
	 *
	 * @param deviceId 圃場ID
	 * @param year 年度
	 * @return ResponseDTO(List<FDataDTO>)
	 */
	// --------------------------------------------------
	@GetMapping("/F/all")
	public ResponseDTO allF(HttpServletRequest httpReq,
			HttpServletResponse res,
			@RequestParam("deviceId") Long deviceId,
			@RequestParam("year") Short year)
		{
		ResponseDTO as_dto = new ResponseDTO();
		try
			{
			List<Ph2RealGrowthFStageEntity> result = this.growthService
					.getAllF(deviceId, year);
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
	 * 指定日より生育推定F値
	 *
	 * @param id  デバイスID
	 * @param date 実績日
	 * @return ResponseDTO(Double)
	 */
	// --------------------------------------------------
	@GetMapping("/F/date")
	public ResponseDTO checkdate(HttpServletRequest httpReq,
			HttpServletResponse res,
			@RequestParam("id") Long id,
			@RequestParam("date") String date)
		{
		ResponseDTO as_dto = new ResponseDTO();
		try
			{
			FValuesDTO result = this.growthService.checkFValueByDate(id, date);
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
	 * 生育推定実績値取得
	 *
	 * @param deviceId 圃場ID
	 * @param year 年度
	 * @param stageId ELステージID
	 * @param deviceId デバイスID
	 * @return ResponseDTO (as_result)
	 */
	// --------------------------------------------------
	@GetMapping("/F/data")
	public ResponseDTO getFData(HttpServletRequest httpReq,
			HttpServletResponse res,
			@RequestParam("date") 
			@DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date date,
			@RequestParam("deviceId") Long deviceId)
		{
		ResponseDTO as_dto = new ResponseDTO();
		try
			{
			ValueDateDTO as_result = this.growthService.getFData(deviceId, date);
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
	 * 生育推定実績値更新
	 *
	 * @param dto FDataListDTO
	 * @return ResponseDTO (null)
	 */
	// --------------------------------------------------
	@PutMapping("/F/data")
	public ResponseDTO updateFData(HttpServletRequest httpReq,
			@RequestParam("deviceId") Long deviceId,
			@RequestParam("year") Short year,
			@RequestBody @Validated FDataListDTO dto)
		{
		ResponseDTO as_dto = new ResponseDTO();
		try
			{
			this.growthService.updateFData(deviceId, year, dto);
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
	 * 基準パラメータセットの設定
	 *
	 * @param dto TargetParamDTO
	 */
	// --------------------------------------------------
	@PutMapping("/paramSet/default")
	public ResponseDTO setDefaut(HttpServletRequest httpReq,
			@RequestBody @Validated TargetParamDTO dto)
		{
		ResponseDTO as_dto = new ResponseDTO();
		try
			{
			this.growthService.setDefault(dto.getDeviceId(), dto.getYear(), dto.getParamId());
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
	 * 生育推定パラメータセット詳細取得
	 *
	 * @param paramSetId パラメータセットID
	 * @return ResponseDTO (GrowthParamSetDTO)
	 */
	// --------------------------------------------------
	@GetMapping("/paramSet/detail")
	public ResponseDTO detail(HttpServletRequest httpReq,
			HttpServletResponse res,
			@RequestParam("paramSetId") Long paramSetId)
		{
		ResponseDTO as_dto = new ResponseDTO();
		try
			{
			paramSetId = (paramSetId == 0) ? null : paramSetId;
			GrowthParamSetDTO as_result = this.growthService
					.getDetail(paramSetId);
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
	 * 生育推定パラメータセット更新
	 *
	 * @param dto GrowthParamSetDTO
	 * @return ResponseDTO (null)
	 */
	// --------------------------------------------------
	@PutMapping("/paramSet")
	public ResponseDTO put(HttpServletRequest httpReq,
			@RequestBody @Validated GrowthParamSetDTO dto)
		{
		ResponseDTO as_dto = new ResponseDTO();
		try
			{
			this.growthService.updateParamSet(dto);
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
	 * 生育推定パラメータセット追加
	 *
	 * @param dto GrowthParamSetDTO
	 * @return ResponseDTO (null)
	 */
	// --------------------------------------------------
	@PostMapping("/paramSet")
	public ResponseDTO post(HttpServletRequest httpReq,
			@RequestBody @Validated GrowthParamSetDTO dto)
		{
		ResponseDTO as_dto = new ResponseDTO();
		try
			{
			Long newId =this.growthService.addParamSet(dto);
			as_dto.setSuccess(newId);
			}
		catch (Exception e)
			{
			as_dto.setError(e);
			}
		return as_dto;
		}

	}
