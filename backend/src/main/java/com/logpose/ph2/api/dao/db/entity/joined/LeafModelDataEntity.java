package com.logpose.ph2.api.dao.db.entity.joined;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=false)
public class LeafModelDataEntity extends ModelDataEntity
	{
	private Double realArea;
	}
