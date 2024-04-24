package com.logpose.ph2.api.controller.dto;

import java.util.List;

import com.logpose.ph2.api.dto.leaf.LeafAreaValuesDTO;

import lombok.Data;
/**
 * コントローラ用のDTOクラス
 * @since 2024/01/03
 * @version 1.0
 */
@Data
public class LeafAreaAndCountDTO
	{
//* デバイスID
	private Long deviceId;
//* 年度
	private Short year;
	
	private List<LeafAreaValuesDTO> values;
	}
