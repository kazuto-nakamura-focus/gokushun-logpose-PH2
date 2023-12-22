package com.logpose.ph2.api.dto;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 〇〇〇グラフデータ取得
 *
 */
@Data
@EqualsAndHashCode(callSuper=false)
public class RealModelGraphDataDTO extends GraphDataDTO
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
	}
