package com.logpose.ph2.batch.formula;

import java.util.Calendar;

import com.logpose.ph2.common.dto.SensorDataDTO;

public class Formula
	{
	// ===============================================
	// 公開関数群
	// ===============================================
	// --------------------------------------------------
	/**
	 * 抵抗値を算出する
	 *
	 * @param x:電圧
	 * @return double:温度
	 */
	// --------------------------------------------------
	public static double toResitence(double x)
		{
		return 10000 * x / (5000 - x);
		}

	// --------------------------------------------------
	/**
	 * 温度を算出する
	 *
	 * @param x:電圧
	 * @return double:温度
	 */
	// --------------------------------------------------
	public static double toTemperature(double x)
		{
		double rs = toResitence(x);
		double y = 1 / (Math.log(rs / 10000) / 3900 + (1 / 298.15)) - 273.15;
		return y * 0.82 + 4.65;
		}

	// --------------------------------------------------
	/**
	 * 湿度を算出する
	 *
	 * @param x:電圧
	 * @return double:湿度
	 */
	// --------------------------------------------------
	public static double toHumidity(double x)
		{
		return (x - 141) / 28.2;
		}

	// --------------------------------------------------
	/**
	 * 日射強度を算出する
	 *
	 * @param x:電圧
	 * @return double:日射強度
	 */
	// --------------------------------------------------
	public static double toSunLight(double x)
		{
		return 5 * x;
		}

	// --------------------------------------------------
	/**
	 * 樹液流量を算出する
	 *
	 * @param x1:電圧1、x２:電圧２、x3:電圧3、x4:電圧４
	 * @return double:樹液流量 NULL値は無効
	 */
	// --------------------------------------------------
	public static Double toSapFlow(
			double x1, double x2, double x3, double x4,
			SensorDataDTO sensor,
			Calendar castedAt)
		{
		double sm = sensor.getSm();
		double kst = sensor.getKst();
		double dxu = sensor.getDxu();
		double dxd = sensor.getDxd();
		double rs = sensor.getRs();

		// * 抵抗値
		x1 = toResitence(x1);
		x2 = toResitence(x2);
		x3 = toResitence(x3);
		x4 = toResitence(x4);
		// * 湿度
		x1 = 1 / (Math.log(x1 / 10000) / 3435 + (1 / 298.15)) - 273.15;
		x2 = 1 / (Math.log(x2 / 10000) / 3435 + (1 / 298.15)) - 273.15;
		x3 = 1 / (Math.log(x3 / 10000) / 3435 + (1 / 298.15)) - 273.15;
		x4 = 1 / (Math.log(x4 / 10000) / 3435 + (1 / 298.15)) - 273.15;
		// * 各種変数
		double ch1 = x2 - x4;
		double tr1 = x2 - x4;
		double td1 = x2 - x3;
		double tu1 = x2 - x1;
		double tf1 = x1 - x3;
		if (0 == tr1)
			{
			return null;
			}
		double qu1 = kst * Math.pow((sm / 2), 2) * Math.PI * tu1 / dxu;
		double qd1 = kst * Math.pow((sm / 2), 2) * Math.PI * td1 / dxd;
		double qin = Math.pow(5, 2) / rs;
		double k1 = 0;
		// if ((ch1 != 0) && (qu1 != 0) && (qd1 != 0) && (x2 - x1) != 0)
		if ((ch1 != 0) && (qu1 != 0) && (qd1 != 0) && tr1 != 0)
			{
			k1 = (qin - (qu1 + qd1)) / tr1;
			}
		else
			{
			return null;
			}
		int time = castedAt.get(Calendar.HOUR_OF_DAY);
		double mink1 = k1;
		Double pre_mink1 = (double) 1; // TODO 確認
		if (time == 0)
			{
			pre_mink1 = (double) 1;
			}
		else if (time < 8)
			{
			if ((k1 > 0) && (pre_mink1 > k1))
				{
				pre_mink1 = k1;
				}
			}
		else
			{
			mink1 = pre_mink1;
			}
		double qr1 = ch1 * mink1;
		// $f 樹液流量
		if (tf1 != 0)
			{
			return (qin - (qu1 + qd1) - qr1) / 4.18 / tf1 * 3600;
			}
		return null;
		}

	// --------------------------------------------------
	/**
	 * 茎の半径の変位量を算出する
	 *
	 * @param x:電圧
	 * @return double:茎の半径の変位量
	 */
	// --------------------------------------------------
	public static double toDendro(double x)
		{
		return 11000 * (x / 5000) / (1 + Math.sqrt(2));
		}

	// --------------------------------------------------
	/**
	 * 土壌水分 量を算出する
	 *
	 * @param x:電圧
	 * @return double:土壌水分 
	 */
	// --------------------------------------------------
	public static double toMoisture(double x)
		{
		double y = 85.357 * x / 1000;
		return Math.log10(y * 10.197);
		}

	// --------------------------------------------------
	/**
	 * PARを算出する
	 *
	 * @param x:瞬間日射量
	 * @return PAR
	 */
	// --------------------------------------------------
	public static double toPAR(double x)
		{
		return x * 600;
		}
	}
