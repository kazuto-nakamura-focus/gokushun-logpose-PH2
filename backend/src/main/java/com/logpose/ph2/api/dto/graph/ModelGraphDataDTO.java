package com.logpose.ph2.api.dto.graph;

import java.util.ArrayList;
import java.util.List;

import com.logpose.ph2.api.dto.EventDaysDTO;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 〇〇〇グラフデータ取得
 *
 */
@Data
@EqualsAndHashCode(callSuper=false)
public class ModelGraphDataDTO extends GraphDataDTO
	{
	//* モデル値のリスト
	private List<Double> predictValues = new ArrayList<>();
	// * 実測値のリスト
	private List<Double> meauredValues = new ArrayList<>();
	// * グラフのアノテーションデータ
	private List<EventDaysDTO> annotations;
	// * カテゴリー
	private List<String> category = new ArrayList<>();
	// * コメント
	private String comment;
	// * 本日の推定値
	private Double estimated;
	}
