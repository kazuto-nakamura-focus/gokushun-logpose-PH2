package com.logpose.ph2.api.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 〇〇〇パラメータセット
 */
@Data
@EqualsAndHashCode(callSuper=false)
public class ParamSetExtendDTO extends ParamSetDTO
	{
	//* デバイス名
	private String deviceName;
	}
