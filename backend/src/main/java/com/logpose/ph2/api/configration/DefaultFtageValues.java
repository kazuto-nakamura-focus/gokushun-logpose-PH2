package com.logpose.ph2.api.configration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import lombok.Data;

@Data
public class DefaultFtageValues
	{
	private List<String> name = 
			new ArrayList<>(Arrays.asList("萌芽期","開花日","べレーゾン","収穫日"));
	private List<Short> start;
	private List<Short> end;
	private List<Double> interval;
	private List<Double> sig;
	private List<String> colors;
	private short sprout;
	}
