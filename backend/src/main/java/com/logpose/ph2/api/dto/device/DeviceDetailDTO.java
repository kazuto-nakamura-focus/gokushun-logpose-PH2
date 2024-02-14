package com.logpose.ph2.api.dto.device;

import java.util.Date;
import java.util.List;

import com.logpose.ph2.api.dto.DeviceInfoDTO;
import com.logpose.ph2.api.dto.senseor.SensorUnitReference;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=false)
public class DeviceDetailDTO extends DeviceInfoDTO
	{
	// * SigFox ID
	private String sigFoxDeviceId;
	// * 引継ぎ元デバイス
	private Long prevDeviceId;
	// * 運用開始日
	private Date opStart;
	// * 運用終了日
	private Date opEnd;
	// * 運用開始日
	private String opStartShort;
	// * 運用終了日
	private String opEndShort;
	
	private List<SensorUnitReference> sensorItems;
	}
