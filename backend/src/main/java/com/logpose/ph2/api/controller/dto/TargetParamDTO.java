package com.logpose.ph2.api.controller.dto;

import lombok.Data;
/**
 * コントローラ用のDTOクラス
 * @since 2024/01/03
 * @version 1.0
 */
@Data
public class TargetParamDTO
	{
//* デバイスID
	private Long deviceId;
//* 年度
	private Short year;
// * パラメータセットID
	private Long paramId;
	}
