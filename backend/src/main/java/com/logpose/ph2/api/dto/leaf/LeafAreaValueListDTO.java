package com.logpose.ph2.api.dto.leaf;

import java.util.List;

import com.logpose.ph2.api.dto.device.DeviceTermDTO;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=false)
public class LeafAreaValueListDTO extends DeviceTermDTO
	{
	private List<LeafAreaValueDTO> values;
	}
