package com.logpose.ph2.batch.domain;

import java.util.Calendar;
import java.util.Date;

import lombok.Data;

@Data
public class YearDateModel
	{
	// ===============================================
	// クラスメンバー
	// ===============================================
	private int baseMonth;
	private int baseDate;

	// ===============================================
	// 公開関数群
	// ===============================================
	// --------------------------------------------------
	/**
	 * 基準日を算出する
	 *
	 * @param baseDate:基準日
	 */
	// --------------------------------------------------
	public void setBase(Date baseDate)
		{
		if(null == baseDate)
			{
			this.baseMonth = 0;
			this.baseDate = 1;

			}
		else 
			{
			Calendar cal = Calendar.getInstance();
			cal.setTime(baseDate);
			this.baseMonth = cal.get(Calendar.MONTH);
			this.baseDate = cal.get(Calendar.DATE);
			}
		}
	}
