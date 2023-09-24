package com.logpose.ph2.api.dto.element;

import lombok.Data;

@Data
public class SelectedTarget
	{
	//* 選択されたモデル
	private Label model;
	//* 選択された圃場
	private Label field;
	//* 選択されたデバイス
	private Label device;
	//* 選択された年度
	private Integer year;
	}
