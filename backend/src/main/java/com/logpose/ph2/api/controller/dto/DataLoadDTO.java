package com.logpose.ph2.api.controller.dto;

import java.util.Date;

import lombok.Data;
/**
 * コントローラ用のDTOクラス
 * @since 2024/01/03
 * @version 1.0
 */
@Data
public class DataLoadDTO
	{
//* デバイスID
	private Long deviceId;
//* データ対象
	private	Boolean isAll;
//* データ取得開始日
	private Date startDate;
	}
