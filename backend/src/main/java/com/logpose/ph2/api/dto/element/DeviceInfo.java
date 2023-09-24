package com.logpose.ph2.api.dto.element;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=false)
public class DeviceInfo extends Label //* デバイスIDとデバイス名
	{
	//* 品種名
	private String brand;
	}
