package com.logpose.ph2.api.dao.db.entity.joined;

import lombok.Data;

/**
 * 成長予測のグラフのアノテーション用データ
 */
@Data
public class AnnotationDTO
	{
	//* Fステージ名
	private String name;
	//* F値
	private Double value;
	}
