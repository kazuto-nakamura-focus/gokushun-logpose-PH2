package com.logpose.ph2.api.dao.db.entity.joined;

import java.util.Date;

import com.logpose.ph2.api.dao.db.entity.Ph2ModelDataEntity;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=false)
public class ModelDataEntity extends Ph2ModelDataEntity
	{
	private Date date;
	private Short lapseDay;
	private Boolean isReal;
	}
