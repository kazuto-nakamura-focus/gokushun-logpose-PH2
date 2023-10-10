package com.logpose.ph2.batch.alg;

import java.util.Calendar;
import java.util.Date;

import org.springframework.stereotype.Component;
@Component
public class DeviceDayAlgorithm
	{
	// ===============================================
	// 公開関数
	// ===============================================
	// --------------------------------------------------
	/**
	 *  デバイスの今年の基準日を設定する
	 * 
	 * @param base デバイスの基準日(月日のみ）
	 * @return デバイスの今年の基準日（西暦を含めたデバイス基準日)
	 */
	// --------------------------------------------------
	public Calendar getBaseDate(Date base)
		{
		Calendar baseDate = Calendar.getInstance();
		// * デバイスの基準日が未指定の場合
		if (base == null)
			{
			baseDate.set(Calendar.MONTH, 0);
			baseDate.set(Calendar.DATE, 1);
			}
		// * 指定されている場合
		else
			{
			Calendar deviceDay = Calendar.getInstance();
			deviceDay.setTime(base);
			baseDate.set(Calendar.MONTH, deviceDay.get(Calendar.MONTH));
			baseDate.set(Calendar.DATE, deviceDay.get(Calendar.DATE));
			}
		return baseDate;
		}
	// --------------------------------------------------
	/**
	 *  日付を00:00:00 000に設定する
	 * 
	 * @param date 設定対象の日付
	 */
	// --------------------------------------------------
	public void setTimeZero(Calendar date)
		{
		date.set(Calendar.HOUR_OF_DAY, 0);
		date.set(Calendar.MINUTE, 0);
		date.set(Calendar.SECOND, 0);
		date.set(Calendar.MILLISECOND, 0);
		}
	// --------------------------------------------------
	/**
	 *  当日の最初
	 */
	// --------------------------------------------------
	public Date getNextDayZeroHour(Date date)
		{
		Calendar deviceDay = Calendar.getInstance();
		deviceDay.setTime(date);
		deviceDay.add(Calendar.DATE, 1);
		this.setTimeZero(deviceDay);
		return deviceDay.getTime();
		}
	// --------------------------------------------------
	/**
	 *  前日の取得
	 */
	// --------------------------------------------------
	public Date getPreviousDay()
		{
		Calendar deviceDay = Calendar.getInstance();
		deviceDay.set(Calendar.DATE, -1);
		this.setTimeZero(deviceDay);
		return deviceDay.getTime();
		}
	}
