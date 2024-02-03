package com.logpose.ph2.api.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.logpose.ph2.api.dto.ResponseDTO;
import com.logpose.ph2.api.service.MaintenanceService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@CrossOrigin(origins = { "http://localhost:8080", "http://localhost:3000",
		"https://gokushun-ph2-it.herokuapp.com" }, methods = { RequestMethod.GET,
				RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE }, allowCredentials = "true")
@RestController
@RequestMapping(path = "/api/mt")
public class MaintainanceController
	{
	@Autowired
	private MaintenanceService	maintenanceService;
	
	@GetMapping("/ph1MessagesToP2")
	public ResponseDTO ph1MessagesToP2(HttpServletRequest httpReq,
			HttpServletResponse res)
		{
		ResponseDTO as_dto = new ResponseDTO();
		try
			{
			this.maintenanceService.moveMessagesPh1toPh2();
			as_dto.setSuccess(null);
			}
		catch (Exception e)
			{
			as_dto.setError(e);
			}

		return as_dto;
		}
	}
