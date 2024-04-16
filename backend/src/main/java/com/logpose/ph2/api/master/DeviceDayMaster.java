package com.logpose.ph2.api.master;

public class DeviceDayMaster
	{
	public static final short ORIGINAL = 1; // デバイスの持つセンサーデータ由来
	public static final short PREVIOUS_DEVICE = 2; // 引継ぎ元データからのセンサーデータ由来
	public static final short PREVIOUS_YEAR = 4; // 引継ぎ元データからのセンサーデータ由来
	public static final short WHEATHER = 8;
	public static final short MEASURED = 16;
	}
