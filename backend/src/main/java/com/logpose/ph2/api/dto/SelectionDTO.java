package com.logpose.ph2.api.dto;

import java.util.ArrayList;
import java.util.List;

import com.logpose.ph2.api.dto.element.Label;

import lombok.Data;

/**
 * モデル選択情報取得
 */
@Data
public class SelectionDTO
	{
	//* 生育ステージなど、各推計モデルリスト
	private List<Label> models = new ArrayList<>();
	private List<ModelTargetDTO> targets;
	}
