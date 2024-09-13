package com.logpose.ph2.api.dto.sensorData;

import java.util.ArrayList;
import java.util.List;

import com.logpose.ph2.api.dto.graph.GraphDataDTO;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=false)
public class SenseorDataDTO extends GraphDataDTO
	{
	public static short TEN = 1;	// １０分おき
	public static short THIRTY = 2;	// 3０分おき
	public static short HOUR = 4;	// 一時間おき
	public static short HOUR2 = 8;	 // 二時間おき
	public static short HOUR4 = 16;	 // 四時間おき
	public static short HOUR6 = 32; // 六時間おき
	public static short HOUR12 = 64; // 十二時間おき
	public static int HOUR_0 = HOUR|HOUR2|HOUR4|HOUR6|HOUR12;
	public static short DAY = 128; // 一日おき
	public static short DAYS5 = 256; // ５日間隔（３０日を除く）
	public static short DAYS10 = 512; // 10日間隔（３０日を除く）
	public static short DAYS15 = 1024; // 15日間隔（３０日を除く）
	public static short MONTH = 2048; // 1月間隔
	public static int DAY_1 = DAY|DAYS5|DAYS10|DAYS15|MONTH;
	
	private long interval = 0;
	private List<String> category = new ArrayList<>();
	private List<Short> flags = new ArrayList<>();
	}
