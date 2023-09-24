package com.logpose.ph2.api.dao.db.entity.joined;

import com.logpose.ph2.api.dto.element.Label;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=false)
public class SensorItemDTO extends Label
	{
	private Long contentId;
	}
