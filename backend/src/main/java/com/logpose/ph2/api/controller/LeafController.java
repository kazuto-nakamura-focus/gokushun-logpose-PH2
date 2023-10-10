package com.logpose.ph2.api.controller;

import java.text.ParseException;
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

import com.logpose.ph2.api.controller.dto.DeviceDateDTO;
import com.logpose.ph2.api.controller.dto.TargetParamDTO;
import com.logpose.ph2.api.dto.LeafParamSetDTO;
import com.logpose.ph2.api.dto.LeafShootDTO;
import com.logpose.ph2.api.dto.LeafvaluesDTO;
import com.logpose.ph2.api.dto.RealModelGraphDataDTO;
import com.logpose.ph2.api.dto.ResponseDTO;
import com.logpose.ph2.api.service.LeafService;

import jakarta.servlet.http.HttpServletRequest;

@CrossOrigin(
		origins = { "http://localhost:8080" },
		methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE},
		allowCredentials = "true")
@RestController
@RequestMapping(path = "/api/leaf")
public class LeafController
	{
	// ===============================================
	// クラスメンバー
	// ===============================================
	@Autowired
	private LeafService leafService;

	// ===============================================
	// パブリック関数（検索系)
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
			@RequestBody @Validated DeviceDateDTO dto)
		{
		ResponseDTO as_dto = new ResponseDTO();
		try
			{
			this.leafService.updateDateModel(dto.getDeviceId(), dto.getYear(),
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
	 * 葉面積モデルグラフデータ取得
	 *
	 * @param deviceId デバイスID
	 * @param year 年度
	 * @param paramId パラメータID　Nullならデフォルト
	 * @return ResponseDTO(GraphDataDTO)
	 */
	// --------------------------------------------------
	@GetMapping("/graph/area/byParamSet")
	public ResponseDTO modelGraphOfArea(HttpServletRequest httpReq,
			@RequestParam("deviceId") Long deviceId,
			@RequestParam("year") Short year)
		{
		ResponseDTO as_dto = new ResponseDTO();
		try
			{
			List<RealModelGraphDataDTO> as_result = this.leafService
					.getLeaflGraphData(deviceId, year);
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
	 * 新梢数取得処理
	 *
	 * @param dto
	 *            LeafShootDTO
	 * @return ResponseDTO (null)
	 */
	// --------------------------------------------------
	@GetMapping("/value/shoot")
	public ResponseDTO getShoot(HttpServletRequest httpReq,
			@RequestParam("deviceId") Long deviceId,
			@RequestParam("date") String date)
		{
		ResponseDTO as_dto = new ResponseDTO();
		try
			{
			LeafShootDTO result = this.leafService.getShootCount(deviceId,
					date);
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
	 * 新梢辺り葉枚数・平均個葉面積検索処理
	 *
	 * @param deviceId
	 * @param date
	 * @throws ParseException
	 */
	// --------------------------------------------------
	@GetMapping("/value/areaAndCount")
	public ResponseDTO getAreaAndCount(HttpServletRequest httpReq,
			@RequestParam("deviceId") Long deviceId,
			@DateTimeFormat(iso = DateTimeFormat.ISO.DATE) @RequestParam("date") Date date)
		{
		ResponseDTO as_dto = new ResponseDTO();
		try
			{
			LeafvaluesDTO result = this.leafService.getAreaAndCount(deviceId,
					date);
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
	 * 葉面積・葉枚数パラメータセット詳細取得
	 *
	 * @param paramSetId パラメータセットID
	 * @return ResponseDTO (LeafParamSetDTO)
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
			LeafParamSetDTO as_result = this.leafService
					.getDetailParamSet(paramSetId);
			as_dto.setSuccess(as_result);
			}
		catch (Exception e)
			{
			as_dto.setError(e);
			}
		return as_dto;
		}

	// ===============================================
	// パブリック関数（更新系)
	// ===============================================
	// --------------------------------------------------
	/**
	 * 新梢数登録処理
	 *
	 * @param dto-LeafShootDTO
	 * @return ResponseDTO (null)
	 */
	// --------------------------------------------------
	@PostMapping("/value/shoot")
	public ResponseDTO addShoot(HttpServletRequest httpReq,
			@RequestBody @Validated LeafShootDTO dto)
		{
		ResponseDTO as_dto = new ResponseDTO();
		try
			{
			this.leafService.addShoot(dto);
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
			this.leafService.setDefault(dto.getDeviceId(), dto.getYear(),
					dto.getParamId());
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
	 * 新梢辺り葉枚数・平均個葉面積登録処理
	 *
	 * @param dto-LeafvaluesDTO
	 * @return ResponseDTO (null)
	 */
	// --------------------------------------------------
	@PostMapping("/value/areaAndCount")
	public ResponseDTO updateAreaAndCount(HttpServletRequest httpReq,
			@RequestBody @Validated LeafvaluesDTO dto)
		{
		ResponseDTO as_dto = new ResponseDTO();
		try
			{
			Double area = this.leafService.setAreaAndCount(dto);
			as_dto.setSuccess(area);
			}
		catch (Exception e)
			{
			as_dto.setError(e);
			}
		return as_dto;
		}

	// --------------------------------------------------
	/**
	 * 葉面積・葉枚数パラメータセット更新
	 *
	 * @param dto LeafParamSetDTO
	 * @return ResponseDTO (null)
	 */
	// --------------------------------------------------
	@PutMapping("/paramSet")
	public ResponseDTO put(HttpServletRequest httpReq,
			@RequestBody @Validated LeafParamSetDTO dto)
		{
		ResponseDTO as_dto = new ResponseDTO();
		try
			{
			this.leafService.updateParamSet(dto);
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
	 * 葉面積・葉枚数パラメータセット追加
	 *
	 * @param dto LeafParamSetDTO
	 * @return ResponseDTO (null)
	 */
	// --------------------------------------------------
	@PostMapping("/paramSet")
	public ResponseDTO post(HttpServletRequest httpReq,
			@RequestBody @Validated LeafParamSetDTO dto)
		{
		ResponseDTO as_dto = new ResponseDTO();
		try
			{
			Long newId = this.leafService.addParamSet(dto);
			as_dto.setSuccess(newId);
			}
		catch (Exception e)
			{
			as_dto.setError(e);
			}
		return as_dto;
		}
	}
