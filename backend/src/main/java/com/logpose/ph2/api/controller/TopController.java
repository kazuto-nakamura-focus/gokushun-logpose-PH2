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
import com.logpose.ph2.api.dto.rawData.RawDataList;
import com.logpose.ph2.api.dto.top.FieldDataWithSensor;
import com.logpose.ph2.api.service.TopService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
/**
 * トップ画面に提供されるAPI群
 * @since 2024/01/03
 * @version 1.0
 */
@CrossOrigin(
		origins = { "http://localhost:8080", "http://localhost:3000", "https://gokushun-ph2-it.herokuapp.com" },
		methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE},
		allowedHeaders ="*", exposedHeaders="*",
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
	 * 全圃場サマリーデータ取得API
	 *
	 * @return List<DataSummaryDTO>
	 */
	// --------------------------------------------------
	@GetMapping("/fields")
	public ResponseDTO getFields(HttpServletRequest request, HttpServletResponse response)
		{
		System.out.println(request.getHeaderNames());
		StringBuffer url = request.getRequestURL();
		System.out.println(url);
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
	 * 検知データ圃場別取得API
	 *
	 * @param contentId 検知するデータタイプのID
	 * @return List<FieldDataWithSensor>
	 */
	// --------------------------------------------------
	@GetMapping("/sum")
	public ResponseDTO getSum(HttpServletRequest request, HttpServletResponse response,
			@RequestParam("contentId") Long contentId)
		{
		ResponseDTO as_dto = new ResponseDTO();
		try
			{
			List<FieldDataWithSensor> as_result = this.topSerivce.getSummaryByFields(contentId);
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
	 * 生データ取得API
	 * 
	 * @param startDate 開始日
	 * @param endDate 終了日
	 * @param deviceId デバイスID
	 * @return RawDataList
	 */
	// --------------------------------------------------
	@GetMapping("/rawData")
	public ResponseDTO getRawData(HttpServletRequest request,
			@DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date startDate,
			@DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date endDate,
			@RequestParam("deviceId") Long deviceId)
		{
		ResponseDTO as_dto = new ResponseDTO();
		try
			{
			RawDataList result = this.topSerivce.getRawData(startDate, endDate, deviceId);
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
	 * データ選択情報取得API
	 *
	 * @param isModel モデルを対象としているなら、真
	 * @return SelectionDTO
	 */
	// --------------------------------------------------
	@GetMapping("/models")
	public ResponseDTO getModels(HttpServletRequest request, @RequestParam("isModel") boolean isModel)
		{
		ResponseDTO as_dto = new ResponseDTO();
		try
			{
			SelectionDTO result = this.topSerivce.getModels(isModel);
			as_dto.setSuccess(result);
			}
		catch (Exception e)
			{
			as_dto.setError(e);
			}
		return as_dto;
		}
	}
