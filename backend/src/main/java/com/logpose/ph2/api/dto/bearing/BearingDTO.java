package com.logpose.ph2.api.dto.bearing;

import lombok.Data;

@Data
public class BearingDTO
	{
// * 実測日
	private String date;
// * 収穫時樹冠葉面積(m^2)
	private float harvestCrownLeafArea;
// * 積算樹冠光合成量(kgCO2vine^-1)
	private float culminatedCrownPhotoSynthesysAmount;
// * 果実総重量
	private Float weightSum = null;
// * 着果負担（果実総重量/収穫時樹冠葉面積）(g/m^2)
	private float bearingWeight;
// * 積算樹冠光合成量あたりの着果量（果実総重量/積算樹冠光合成量）(g/kgCO2 vine^-1)
	private float bearingPerPhotoSynthesys;
// * 実測着果数/収穫時樹冠葉面積(房数/m^2)
	private float bearingCount;
	}
