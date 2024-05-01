package com.logpose.ph2.api.dto.leaf;

import com.logpose.ph2.api.dto.device.DeviceTermDTO;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @新梢数登録処理
 *
 */
@Data
@EqualsAndHashCode(callSuper=false)
public class LeafShootDTO extends DeviceTermDTO
	{
	//* 実施日
	private String date;
	//* 新梢数
	private Integer count;
	}
