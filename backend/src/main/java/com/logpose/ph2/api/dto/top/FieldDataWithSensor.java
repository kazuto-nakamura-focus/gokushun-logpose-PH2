package com.logpose.ph2.api.dto.top;

import com.logpose.ph2.api.dto.element.FieldData;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * フィールドデータのDTOクラス
 * @since 2024/01/03
 * @version 1.0
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class FieldDataWithSensor extends FieldData
	{
// * センサー名
	private String sensorName;
	}
