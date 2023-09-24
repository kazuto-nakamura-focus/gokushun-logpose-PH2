package com.logpose.ph2.batch.dto;

import lombok.Data;

@Data
public class ResponseDTO
	{
	//* 処理結果
	private Integer status;
	//* 処理メッセージ
	private String message;
	//* レスポンスデータ
	private Object data;
	
	public void setSuccess(Object data)
		{
		this.setMessage(null);
		this.setData(data);
		this.setStatus(0);
		}
	public void setError(Exception error)
		{
		// TODO
		this.setMessage("処理中にエラーが発生しました。");
		this.setData(null);
		this.setStatus(1);
		// TODO
		error.printStackTrace();
		}
	}
