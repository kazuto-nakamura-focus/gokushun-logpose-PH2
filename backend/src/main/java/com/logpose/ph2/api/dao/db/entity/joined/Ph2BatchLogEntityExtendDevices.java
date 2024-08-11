package com.logpose.ph2.api.dao.db.entity.joined;

import com.logpose.ph2.api.dao.db.entity.Ph2BatchLogEntity;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=false)
public class Ph2BatchLogEntityExtendDevices extends Ph2BatchLogEntity
	{
	private String name;
	private Integer dataStatus;
	}
