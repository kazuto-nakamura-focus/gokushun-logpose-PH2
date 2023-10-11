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

import com.logpose.ph2.api.dto.DataSummaryDTO;
import com.logpose.ph2.api.dto.ResponseDTO;
import com.logpose.ph2.api.dto.SelectionDTO;
import com.logpose.ph2.api.dto.element.FieldData;
import com.logpose.ph2.api.service.TopService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@CrossOrigin(
		origins = { "http://localhost:8080", "https://gokushun-ph2-it.herokuapp.com" },
		methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE},
		allowCredentials = "true")
@RestController
@RequestMapping(path = "/api")
public class TopController
	{
	// ===============================================
	// クラスメンバー
	// ===============================================
	@Autowired
	private TopService topSerivce;

	// ===============================================
	// パブリック関数群
	// ===============================================
	// --------------------------------------------------
	/**
	 * 全圃場サマリーデータ取得
	 *
	 * @return ResponseDTO(DataSummaryDTO)
	 */
	// --------------------------------------------------
	@GetMapping("/fields")
	public ResponseDTO fields(HttpServletRequest httpReq, HttpServletResponse res)
		{
		ResponseDTO as_dto = new ResponseDTO();
		try
			{
			List<DataSummaryDTO> as_result = this.topSerivce.getFieldData();
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
	 * 検知データ圃場別取得
	 *
	 * @param detectId 検知データID
	 * @return ResponseDTO(FieldsByDataDTO)
	 */
	// --------------------------------------------------
	@GetMapping("/sum")
	public ResponseDTO sum(HttpServletRequest httpReq,HttpServletResponse res,
			@RequestParam("contentId") Long contentId)
		{
		ResponseDTO as_dto = new ResponseDTO();
		try
			{
			List<FieldData> as_result = this.topSerivce
					.getSummaryByFields(contentId);
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
	 * 生データ取得
	 *
	 * @return ResponseDTO(SelectionDTO)
	 */
	// --------------------------------------------------
	@GetMapping("/rawData")
	public ResponseDTO getRawData(HttpServletRequest httpReq,
			HttpServletResponse res,
			@DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date startDate,
			@DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date endDate,
			@RequestParam("deviceId") Long deviceId)
	{
	ResponseDTO as_dto = new ResponseDTO();
	try
		{
		List<String[]> result = this.topSerivce.getRawData(startDate, endDate, deviceId);
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
	 * モデル選択情報取得
	 *
	 * @return ResponseDTO(SelectionDTO)
	 */
	// --------------------------------------------------
	@GetMapping("/models")
	public ResponseDTO getModels(HttpServletRequest httpReq,HttpServletResponse res)
		{
		ResponseDTO as_dto = new ResponseDTO();
		try
			{
			SelectionDTO result = this.topSerivce.getModels();
			as_dto.setSuccess(result);
			}
		catch (Exception e)
			{
			as_dto.setError(e);
			}

		return as_dto;
		}
	}
