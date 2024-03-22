package com.logpose.ph2.api.dto.device;

import java.util.Date;

import lombok.Data;

@Data
public class DeviceTransitInfoDTO
	{
	// * 引継ぎ元デバイス
	private Long prevDeviceId;
	// * 引継ぎ年度
	private Short year;
	// * 移転開始日
	private Date startDate;
	// * 移転終了日
	private Date endDate;
	}
