package com.logpose.ph2.api.dto.element;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * フィールドデータのDTOクラス
 * @since 2024/01/03
 * @version 1.0
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class FieldData extends Label
	{
// * 値
	private String value;
// * 値の単位
	private String unit;
// * 受信日時
	private String castedAt;
// * 表示順
	private Short displayNo;
	}
