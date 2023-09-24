package com.logpose.ph2.api.dto;

import java.util.List;

import com.logpose.ph2.api.dto.element.DeviceInfo;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=false)
public class FieldDetailDTO extends FieldInfoDTO //* 圃場情報
	{
	//* デバイス情報リスト
	private List<DeviceInfo> deviceList;
	}
