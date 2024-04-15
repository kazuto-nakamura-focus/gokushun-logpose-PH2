package com.logpose.ph2.api.dto;

import com.logpose.ph2.api.dto.graph.GraphDataDTO;

import lombok.Data;

/**
 * 〇〇〇グラフデータ取得
 *
 */
@Data
public class LeafGraphDataDTO
	{
	private GraphDataDTO modelGraph;
	private GraphDataDTO realGraph;
	}
