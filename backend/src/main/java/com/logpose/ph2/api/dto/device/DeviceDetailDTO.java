package com.logpose.ph2.api.dto.device;

import java.util.List;

import com.logpose.ph2.api.dto.DeviceInfoDTO;
import com.logpose.ph2.api.dto.senseor.SensorUnitReference;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=false)
public class DeviceDetailDTO extends DeviceInfoDTO
	{
	private String sigFoxDeviceId;
	
	private List<SensorUnitReference> sensorItems;
	}
