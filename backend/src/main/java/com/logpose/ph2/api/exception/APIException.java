package com.logpose.ph2.api.exception;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.ResponseEntity;

public class APIException extends Exception
	{
	private String url;
	private String body;

	public APIException(String url, Exception e)
		{
		super(e);
		this.url = url;
		this.body = null;
		}
	public <T> APIException(String url, ResponseEntity<T> response)
		{
		super("ステータスコード  " + response.getStatusCode() + " を受取ました。");
		this.url = url;
		this.body = response.getBody().toString();
		}
	public List<String> getCauseText()
		{
		List<String> errs = new ArrayList<>();
		errs.add("#" + this.url + "への問い合わせに失敗しました。");
		errs.add("#" + super.getMessage());
		if (null != this.body) errs.add("#" + this.body);
		return errs;
		}
	
	}
