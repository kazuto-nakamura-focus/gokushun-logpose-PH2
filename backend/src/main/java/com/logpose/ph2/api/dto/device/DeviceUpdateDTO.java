package com.logpose.ph2.api.dto.device;

import java.util.List;

import com.logpose.ph2.api.dto.element.Label;
import com.logpose.ph2.api.dto.senseor.SensorUnitUpdate;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=false)
public class DeviceUpdateDTO extends Label
	{
	//* 圃場ID
	private Long fieldId;
	//* 圃場名
	private String field;
	//* 品種
	private String brand;
	//* 基準日表示用
	private String baseDateShort;
	//* タイムゾーン
	private String timeZone;
	// SigFox device id
	private String sigFoxDeviceId;
	// * 引継ぎ元デバイス
	private Long prevDeviceId;
	// * 運用開始日
	private String opStartShort;
	// * 運用終了日
	private String opEndShort;
	
	private List<SensorUnitUpdate> sensorItems;
	}
