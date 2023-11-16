package com.logpose.ph2.api.dto.dashboard;

import java.util.ArrayList;
import java.util.List;
import com.logpose.ph2.api.dto.element.Label;
import lombok.Data;

@Data
public class DashboardTarget
	{
	private List<Label> fields = new ArrayList<>();
	private List<DashBoardDevicesDTO> devices;
	}
