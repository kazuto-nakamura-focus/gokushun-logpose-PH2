package com.logpose.ph2.api.dto.sensorData;

import java.util.ArrayList;
import java.util.List;

import com.logpose.ph2.api.dto.graph.GraphDataDTO;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class SensorDataDTO extends GraphDataDTO
	{

	private long interval = 0;
	private List<String> category = new ArrayList<>();
	private List<Double> nullData = new ArrayList<>();

	private int lastNullIndex = -1;

	public void setValue(Double value)
		{
		this.getValues().add(value);
		this.nullData.add(null);
		if (value != null)
			{
// * 途中でNULLデータがある場合
			if ((lastNullIndex != -1) && (lastNullIndex > 0))
				{
				double lastValue = this.getValues().get(lastNullIndex - 1);
				double diffIndex = this.getValues().size() - lastNullIndex;
				double avediff = (value - lastValue) / diffIndex;
				this.nullData.set(lastNullIndex - 1, lastValue);
				for (int i = lastNullIndex; i < this.getValues().size(); i++)
					{
					lastValue += avediff;
					this.nullData.set(i, lastValue);
					}
				}
			lastNullIndex = -1;
			}
		else
			{
			if (lastNullIndex == -1)
				{
				lastNullIndex = this.getValues().size() - 1;
				}
			}
		}
	}
