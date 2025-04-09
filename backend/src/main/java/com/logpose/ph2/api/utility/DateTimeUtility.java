package com.logpose.ph2.api.utility;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;

public class DateTimeUtility
	{
	private static final ZoneId zoneId = ZoneId.of("Asia/Tokyo");

	public static Date getDateFromString(String dateString) throws ParseException
		{
		return new SimpleDateFormat("yyyy-MM-dd").parse(dateString);
		}

	public static String getStringFromDate(Date date) throws ParseException
		{
		return new SimpleDateFormat("yyyy-MM-dd").format(date);
		}

	public static String getStringFromDateTime(Date date) throws ParseException
		{
// DateをInstantに変換
		Instant instant = date.toInstant();
// InstantをLocalDateTimeに変換 (デフォルトのタイムゾーンを使用)
		LocalDateTime localDateTime = LocalDateTime.ofInstant(instant, zoneId);
// フォーマットを指定
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
// 時刻を文字列に変換
		return localDateTime.format(formatter);
		}

	public static Date getOneYeaAfter(Date date) throws ParseException
		{
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.add(Calendar.YEAR, 1);
		return cal.getTime();
		}

	public static Date getStartOfDay(Date date)
		{
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		// 時刻を0に設定
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		return cal.getTime();
		}
	}
