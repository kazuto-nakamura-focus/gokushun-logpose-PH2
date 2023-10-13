package com.logpose.ph2.api.formula;

import java.util.Calendar;

import com.logpose.ph2.api.bulk.dto.SensorDataDTO;
import com.logpose.ph2.api.dto.DailyBaseDataDTO;
import com.logpose.ph2.api.dto.LeafParamSetDTO;

public class Formula
	{
	// ===============================================
	// クラスめばー
	// ===============================================
	private static final double WA = 2.19;
	private static final double WB = 107.0;
	private static final double WL = 117.3;

	// ===============================================
	// 公開関数群
	// ===============================================
	// --------------------------------------------------
	/**
	 * F値を算出する
	 * @param x 平均気温
	 * @param d d値
	 * @param e e値
	 * @return F値
	 */
	// --------------------------------------------------
	public static double toFValue(double x, double d, double e)
		{
		return 1 / (1 + Math.exp(d * (x - e)));
		}

	// --------------------------------------------------
	/**
	 * ワイブル分布
	 * param double 萌芽日までの日数
	 * return ワイブル分布値
	 */
	// --------------------------------------------------
	public static double getWible(double x)
		{
		// ワイブル分布=(α/β((x/β)^(α−1))*(exp(−(x/β)^α))*λ
		// $waibull = ($waibull_a / $waibull_b * (($dab /
		// $waibull_b)**($waibull_a - 1)) * (exp(-1*($dab /
		// $waibull_b)**$waibull_a ) * $waibull_c));
		// waibull = WA / WB * ((x /WB)**(WA - 1)) * (exp(-1*(x /WB)**WA ) * WL);
		/*	return WA / WB * (Math.pow((x / WB), (WA - 1)))
		* (Math.exp(Math.pow(-1 * (x / WB), WA))) * WL;*/
		double tmp = Math.pow(-1 * (x / WB), WA)*10000;
		tmp = Math.exp(tmp)*WL;
		tmp = tmp/10000;
		return WA / WB * (Math.pow((x / WB), (WA - 1)))*tmp;
		}

	// --------------------------------------------------
	/**
	 * 葉枚数モデル式
	 * @param params 葉面積モデルパラメータ
	 * @param dayData 日にちデータ
	 * @return 葉枚数
	 */
	// --------------------------------------------------
	public static double toCountLeaf(LeafParamSetDTO params,
			DailyBaseDataDTO dayData)
		{
		double c = params.getCountC();
		double tm = dayData.getTm();
		double d = params.getCountD();
		// (葉の枚数/シュート)LAR =c*Tm + d
		return c * tm + d;
		}

	// --------------------------------------------------
	/**
	 * 樹冠葉面積モデル式
	 * @param days 萌芽日数
	 * @param count 実測新梢数
	 * @param params 葉面積モデルパラメータ
	 * @param dayData 日にちデータ
	 * @return 葉枚数
	 */
	// --------------------------------------------------
	public static double toLeafArea(long days,
			long count, LeafParamSetDTO params, DailyBaseDataDTO dayData)
		{
		double a = params.getAreaA();
		double b = params.getAreaB();
		double c = params.getAreaC();
		double cdd = dayData.getCdd();
		// LAｃ＝a/(1+exp(4c((b-CDD)/a)))*実測新梢数*ワイブル分布
		return a / (1 + Math.exp(4 * c * ((b - cdd) / a))) * count
				* Formula.getWible(days);
		}

	// --------------------------------------------------
	/**
	 * 推定積算樹冠光合成量
	 * @param f f値
	 * @param g g値
	 * @param prev 前の値
	 * @param leafArea 樹冠葉面積
	 * @param PAR PAR値
	 * @return 推定積算樹冠光合成量
	 */
	// --------------------------------------------------
	public static double toPsAmount(
			double f, double g,
			double prev, double leafArea, double PAR)
		{
		// Σ(f*exp(g*PAR)*PAR/2.02*600*樹冠葉面積/1000)*44/1000
		return prev +
				(f * (Math.exp(g * PAR)) * PAR / 2.02 * 600 * leafArea / 1000)
						* 44 / 1000;
		}
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
