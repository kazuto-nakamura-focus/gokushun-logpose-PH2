package com.logpose.ph2.api.dto.graph;

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
	//* Y軸最小値
	private Double YStart = (double) 0;
	//* Y軸最大値
	private Double YEnd = (double) 0;
	//* X軸最小値(起点日)
	private String XStart;
	//* X軸最大値(終点日)
	private String XEnd;
	//* 値のリスト
	private List<Double> values = new ArrayList<>();
	}
