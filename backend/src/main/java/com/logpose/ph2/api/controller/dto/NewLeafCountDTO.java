package com.logpose.ph2.api.controller.dto;

import com.logpose.ph2.api.dto.leaf.LeafShootDTO;

import lombok.Data;
/**
 * コントローラ用のDTOクラス
 * @since 2024/01/03
 * @version 1.0
 */
@Data
public class NewLeafCountDTO
	{
//* デバイスID
	private Long deviceId;
//* 年度
	private Short year;
// * 登録データ
	private LeafShootDTO value;
	}
