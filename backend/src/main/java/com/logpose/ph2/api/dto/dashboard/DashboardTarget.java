package com.logpose.ph2.api.dto.dashboard;

import java.util.ArrayList;
import java.util.List;

import com.logpose.ph2.api.dto.element.Label;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=false)
public class DashboardTarget extends Label
	{ 
	private List<DashBoardDevicesDTO> devices = new ArrayList<>();
	}
