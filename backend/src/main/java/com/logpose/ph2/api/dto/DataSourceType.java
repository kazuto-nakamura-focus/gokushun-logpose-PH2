package com.logpose.ph2.api.dto;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
public class DataSourceType
	{
	// ===============================================
	// クラスメンバー
	// ===============================================
	private Date startDate;
	@Setter
	private Date endDate;
	private short status;
	}
