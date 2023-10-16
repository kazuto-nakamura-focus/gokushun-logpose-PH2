package com.logpose.ph2.api.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 〇〇〇パラメータセット
 */
@Data
@EqualsAndHashCode(callSuper=false)
public class ParamSetExtendDTO
	{
	//* パラメータID
	private Long id;
	//* 対象年度
	private Short year;
	//* パラメータ名
	private String parameterName;
	//* デフォルトフラグ
	private Boolean defaultFlg;
	//* デバイスID
	private Long deviceId;
	//* デバイス名
	private String deviceName;
	}
