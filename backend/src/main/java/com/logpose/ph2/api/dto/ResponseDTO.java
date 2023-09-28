package com.logpose.ph2.api.dto;

import lombok.Data;

@Data
public class ResponseDTO
	{
	// * 処理結果
	private Integer status;
	// * 処理メッセージ
	private String message;
	// * レスポンスデータ
	private Object data;

	public void setSuccess(Object data)
		{
		this.setMessage(null);
		this.setData(data);
		this.setStatus(0);
		}

	public void setError(Exception error)
		{
		String message;
		if (error instanceof RuntimeException)
			{
			message = error.getMessage();
			}
		else
			{
			message = "処理中にエラーが発生しました。";
			}
		this.setMessage(message);
		this.setData(null);
		this.setStatus(1);
		// TODO
		error.printStackTrace();
		}
	}
