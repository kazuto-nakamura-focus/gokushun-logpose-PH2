package com.logpose.ph2.api.dto;

import lombok.Data;

/**
 * @新梢数登録処理
 *
 */
@Data
public class LeafShootDTO
	{
	//* デバイスID
	private Long deviceId;
	//* 実施日
	private String date;
	//* 新梢数
	private Integer count;
	}
