package com.logpose.ph2.api.dao.db.entity.joined;

import com.logpose.ph2.api.dao.db.entity.Ph2DailyBaseDataEntity;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=false)
public class ExtendDailyBaseDataEntity extends Ph2DailyBaseDataEntity
	{
	private Long lapseDay;
	}
