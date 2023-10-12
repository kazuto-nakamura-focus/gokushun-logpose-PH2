package com.logpose.ph2.api.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.logpose.ph2.api.dto.HistoryDTO;
import com.logpose.ph2.api.dto.ParamSetDTO;
import com.logpose.ph2.api.dto.ResponseDTO;
import com.logpose.ph2.api.service.ParamSetService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@CrossOrigin(
<<<<<<< HEAD
		origins = { "http://localhost:8080", "http://localhost:3000", "https://gokushun-ph2-it.herokuapp.com" },
=======
		origins = { "http://localhost:8080", "https://gokushun-ph2-it.herokuapp.com" },
>>>>>>> main
		methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE},
		allowCredentials = "true")
@RestController
@RequestMapping(path = "/api/paramSet")
public class ParamSetController
	{
	// ===============================================
	// クラスメンバー
	// ===============================================
	@Autowired
	private ParamSetService paramSetService;

	// ===============================================
	// パブリック関数群
	// ===============================================
	/// --------------------------------------------------
	/**
	 * パラメータセットリスト取得処理
	 *
	 * @param modelID
	 * @param year
	 * @return ResponseDTO(FruitValuesDTO)
	 */
	/// --------------------------------------------------
	@GetMapping("/list")
	public ResponseDTO getParameterSetList(HttpServletRequest httpReq,
			HttpServletResponse res,
			@RequestParam("modelId") Integer modelId)
		{
		ResponseDTO as_dto = new ResponseDTO();
		try
			{
			List<ParamSetDTO> as_result = this.paramSetService
					.getParamSetList(modelId);
			as_dto.setSuccess(as_result);
			}
		catch (Exception e)
			{
			as_dto.setError(e);
			}
		return as_dto;
		}

	@GetMapping("/defaultId")
	public ResponseDTO getDefaultParam(HttpServletRequest httpReq,
			HttpServletResponse res,
			@RequestParam("modelId") Integer modelId,
			@RequestParam("deviceId") Long deviceId,
			@RequestParam("year") Short year)
		{
		ResponseDTO as_dto = new ResponseDTO();
		try
			{
			Long result = this.paramSetService
					.getDefaultParam(modelId, deviceId, year);
			as_dto.setSuccess(result);
			}
		catch (Exception e)
			{
			as_dto.setError(e);
			}
		return as_dto;
		}

	/// --------------------------------------------------
	/**
	 * パラメータセット更新履歴取得
	 *
	 * @param paramSetId パラメータセットID    
	 * @return ResponseDTO (List<HistoryDTO>)
	 */
	/// --------------------------------------------------
	@GetMapping("/history")
	public ResponseDTO history(HttpServletRequest httpReq,
			HttpServletResponse res,
			@RequestParam("paramSetId") Long paramSetId)
		{
		ResponseDTO as_dto = new ResponseDTO();
		try
			{
			List<HistoryDTO> result = this.paramSetService
					.getHistory(paramSetId);
			as_dto.setSuccess(result);
			}
		catch (Exception e)
			{
			as_dto.setError(e);
			}
		return as_dto;
		}
	/// --------------------------------------------------
	/**
	 * パラメータセット削除
	 *
	 * @param paramSetId パラメータセットID    
	 * @return ResponseDTO (List<HistoryDTO>)
	 */
	/// --------------------------------------------------
	@DeleteMapping("/delete")
	public ResponseDTO delete(HttpServletRequest httpReq,
			HttpServletResponse res,
			@RequestParam("paramSetId") Long paramSetId)
		{
		ResponseDTO as_dto = new ResponseDTO();
		try
			{
			this.paramSetService.removeParamSet(paramSetId);
			as_dto.setSuccess(null);
			}
		catch (Exception e)
			{
			as_dto.setError(e);
			}
		return as_dto;
		}
	}
