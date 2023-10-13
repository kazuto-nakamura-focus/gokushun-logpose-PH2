package com.logpose.ph2.api.bulk.domain;

import java.util.Calendar;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.logpose.ph2.api.bulk.dto.DeviceDateDTO;
import com.logpose.ph2.api.bulk.dto.ResponseDTO;
import com.logpose.ph2.api.configration.DefaultUrlParameters;
import com.logpose.ph2.batch.dao.db.mappers.Ph2DailyBaseDataMapper;

@Component
public class ModelDataDomain
	{
	@Autowired
	DefaultUrlParameters params;
	@Autowired
	Ph2DailyBaseDataMapper ph2DailyBaseDataMapper;

	// --------------------------------------------------------------------
	public void setGrowthModel(Long deviceId, Short year, Date date)
		{
		DeviceDateDTO arg = new DeviceDateDTO();
		arg.setDate(date);
		arg.setYear(year);
		arg.setDeviceId(deviceId);
		ResponseDTO response = this.send(params.getGrowthUrl(), HttpMethod.PUT,
				arg);
		if (0 != response.getStatus())
			{
			throw new RuntimeException(response.getMessage());
			}
		}

	// --------------------------------------------------------------------
	public void setLeafModel(Long deviceId, Short year, Date date)
		{
		DeviceDateDTO arg = new DeviceDateDTO();
		arg.setDate(date);
		arg.setYear(year);
		arg.setDeviceId(deviceId);
		ResponseDTO response = this.send(params.getLeafUrl(), HttpMethod.PUT,
				arg);
		if (0 != response.getStatus())
			{
			throw new RuntimeException(response.getMessage());
			}
		}

	// --------------------------------------------------------------------
	public void setPsModel(Long deviceId, Short year, Date date)
		{
		DeviceDateDTO arg = new DeviceDateDTO();
		arg.setDate(date);
		arg.setYear(year);
		arg.setDeviceId(deviceId);
		ResponseDTO response = this.send(params.getPsUrl(), HttpMethod.PUT,
				arg);
		if (0 != response.getStatus())
			{
			throw new RuntimeException(response.getMessage());
			}
		}

	// --------------------------------------------------------------------
	private ResponseDTO send(String url, HttpMethod method, DeviceDateDTO arg)
		{
		System.out.println(arg.getDeviceId() + ":" + arg.getDate());
		// HTTPヘッダ作成
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<DeviceDateDTO> entity = new HttpEntity<>(arg, headers);

		RestTemplate restTemplate = new RestTemplate();
		ResponseEntity<ResponseDTO> response = restTemplate.exchange(url,
				method, entity, ResponseDTO.class);

		HttpStatusCode statusCode = response.getStatusCode();
		if (statusCode.is2xxSuccessful())
			{
			return response.getBody();
			}
		else
			{
			throw new RuntimeException("クエリ" + url + "は失敗しました。");
			}
		}

	public void doService(long deviceId, short year, Date startDate)
		{
		Calendar cal = Calendar.getInstance();
		Date today = cal.getTime();
		this.setGrowthModel(deviceId, year, startDate);
		this.setLeafModel(deviceId, year, startDate);
		this.setPsModel(deviceId, year, startDate);
		}

	}
