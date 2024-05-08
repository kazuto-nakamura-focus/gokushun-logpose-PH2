package com.logpose.ph2.api.dto;

import lombok.Data;

/**
 * 履歴情報
 */
@Data
public class HistoryDTO
	{
	//* 日時
	private String date;
	//* 変更者ID
	private Long userId;
	//* 変更者名
	private String name;
	//* コメント
	private String comment;
	}
