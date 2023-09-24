package com.logpose.ph2.api.dto;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

/**
 * 〇〇〇グラフデータ取得
 *
 */
@Data
public class GraphDataDTO
	{
	//* 選択されたデータ
	//private SelectedTarget selected;
	//* Y軸最小値
	private Double YStart;
	//* Y軸最大値
	private Double YEnd;
	//* X軸最小値(起点日)
	private String XStart;
	//* X軸最大値(終点日)
	private String XEnd;
	//* 値のリスト
	private List<Double> values = new ArrayList<>();
	}
