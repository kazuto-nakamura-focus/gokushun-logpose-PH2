package com.logpose.ph2.api.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=false)
public class GrowthParamSetDTO extends ParamSetDTO
	{
	//* 萌芽前d値
	private Double bd;
	//* 萌芽前e値
	private Double be;
	//* 萌芽後d値
	private Double ad;
	//* 萌芽後e値
	private Double ae;
	}
