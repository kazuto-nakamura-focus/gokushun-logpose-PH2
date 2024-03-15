package com.logpose.ph2.api.domain.common;

import lombok.Getter;

public class MaxValue
	{
	@Getter
	private Double max = Double.MIN_VALUE;

	public void setMax(Double value)
		{
		if (value != null)
			{
			if (value.doubleValue() > this.max)
				{
				this.max = value.doubleValue();
				}
			}
		}
	}
