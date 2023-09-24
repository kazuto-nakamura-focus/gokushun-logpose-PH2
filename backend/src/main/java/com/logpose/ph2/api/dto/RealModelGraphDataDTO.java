package com.logpose.ph2.api.dto;

import java.util.ArrayList;
import java.util.List;

import com.logpose.ph2.api.dao.db.entity.joined.AnnotationDTO;

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
	// * グラフのアノテーションデータ
	private List<AnnotationDTO> annotations;
	}
