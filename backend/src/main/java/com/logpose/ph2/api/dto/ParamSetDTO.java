package com.logpose.ph2.api.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 〇〇〇パラメータセット
 */
@Data
@EqualsAndHashCode(callSuper=false)
public class ParamSetDTO extends HistoryDTO
	{
	//* パラメータID
	private Long id;
	//* デバイスID
	private Long deviceId;
	//* 対象年度
	private Short year;
	//* パラメータ名
	private String parameterName;
	//* デフォルトフラグ
	private Boolean defaultFlg;
	}
