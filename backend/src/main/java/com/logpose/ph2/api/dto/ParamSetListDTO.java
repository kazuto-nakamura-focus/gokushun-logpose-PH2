package com.logpose.ph2.api.dto;

import java.util.List;

import lombok.Data;

/**
 * 〇〇〇パラメータセットリスト取得
 */
@Data
public class ParamSetListDTO
	{
	//* パラメータのIDと名前のリスト
	private List<ParamSetDTO> list;
	}
