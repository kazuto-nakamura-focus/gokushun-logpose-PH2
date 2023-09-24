package com.logpose.ph2.api.utility;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateTimeUtility
	{
	public static Date getDateFromString(String dateString) throws ParseException
		{
		return new SimpleDateFormat("yyyy-MM-dd").parse(dateString);
		}
	public static String getStringFromDate(Date date) throws ParseException
		{
		return new SimpleDateFormat("yyyy-MM-dd").format(date);
		}
	public static Date getOneYeaAfter(Date date) throws ParseException
		{
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.add(Calendar.YEAR, 1);
		return cal.getTime();
		}

	}
