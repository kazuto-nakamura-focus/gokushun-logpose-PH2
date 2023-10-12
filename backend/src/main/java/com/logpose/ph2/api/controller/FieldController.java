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

import com.logpose.ph2.api.dto.FieldDetailDTO;
import com.logpose.ph2.api.dto.FieldInfoDTO;
import com.logpose.ph2.api.dto.ResponseDTO;
import com.logpose.ph2.api.service.FIeldService;

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
@RequestMapping(path = "/api/field")
public class FieldController
	{
	// ===============================================
	// クラスメンバー
	// ===============================================
	@Autowired
	private FIeldService  fIeldService;
	// ===============================================
	// パブリック関数群
	// ===============================================
	// --------------------------------------------------
	/**
	 * 圃場一覧取得
	 *
	 * @return ResponseDTO(List<FIeldInfoDTO>)
	 */
	// --------------------------------------------------
	@GetMapping( "/list")
	public ResponseDTO list(HttpServletRequest httpReq, HttpServletResponse res)
		{
		ResponseDTO	as_dto = new ResponseDTO();
		try
			{
			List<FieldInfoDTO> result = this.fIeldService.list();
			as_dto.setSuccess(result);
			}
		catch(Exception e)
			{
			as_dto.setError(e);
			}
		return as_dto;
		}
	// --------------------------------------------------
	/**
	 * 圃場削除
	 *
	 * @param fieldId 圃場ID
	 * @return ResponseDTO(null)
	 */
	// --------------------------------------------------
	@DeleteMapping( "/info")
	public ResponseDTO delete(HttpServletRequest httpReq, @RequestParam("fieldId") Long fieldId)
		{
		ResponseDTO	as_dto = new ResponseDTO();
		try
			{
			this.fIeldService.delete(fieldId);
			as_dto.setSuccess(null);
			}
		catch(Exception e)
			{
			as_dto.setError(e);
			}
		return as_dto;
		}
	// --------------------------------------------------
	/**
	 * 圃場情報詳細取得
	 *
	 * @param fieldId 圃場ID
	 * @return ResponseDTO(FieldDetailDTO)
	 */
	// --------------------------------------------------
	@GetMapping( "/info")
	public ResponseDTO get(HttpServletRequest httpReq, @RequestParam("fieldId") Long fieldId)
		{
		ResponseDTO	as_dto = new ResponseDTO();
		try
			{
			FieldDetailDTO as_result = this.fIeldService.getInfo(fieldId);
			as_dto.setSuccess(as_result);
			}
		catch(Exception e)
			{
			as_dto.setError(e);
			}
		return as_dto;
		}
	// --------------------------------------------------
	/**
	 * 圃場情報追加
	 *
	 * @param dto FieldInfoDTO
	 * @return ResponseDTO (null)
	 */
	// --------------------------------------------------
	@PostMapping( "/info")
	public ResponseDTO post(HttpServletRequest httpReq, @RequestBody @Validated FieldInfoDTO  dto)
		{
		ResponseDTO	as_dto = new ResponseDTO();
		try
			{
			Long id = this.fIeldService.addInfo(dto);
			as_dto.setSuccess(id);
			}
		catch(Exception e)
			{
			as_dto.setError(e);
			}
		return as_dto;
		}
	// --------------------------------------------------
	/**
	 * 圃場情報更新
	 *
	 * @param dto FieldInfoDTO
	 * @return ResponseDTO (null)
	 */
	// --------------------------------------------------
	@PutMapping( "/info")
	public ResponseDTO put(HttpServletRequest httpReq, @RequestBody @Validated FieldInfoDTO  dto)
		{
		ResponseDTO	as_dto = new ResponseDTO();
		try
			{
			this.fIeldService.updateInfo(dto);
			as_dto.setSuccess(null);
			}
		catch(Exception e)
			{
			as_dto.setError(e);
			}
		return as_dto;
		}
	}
