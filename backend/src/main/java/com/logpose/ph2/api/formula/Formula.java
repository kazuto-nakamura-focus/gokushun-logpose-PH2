package com.logpose.ph2.api.formula;

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
	 * return ワイブル分布値
	 */
	// --------------------------------------------------
	public static double getWible(double x)
		{
		// ワイブル分布=(α/β((x/β)^(α−1))*(exp(−(x/β)^α))*λ
		return (WA / WB * (Math.pow((x / WB), (WA - 1))))
				* (Math.exp(-1 * Math.pow( (x / WB), WA))) * WL;
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
				(f *( Math.exp(g* PAR)) * PAR / 2.02 * 600 * leafArea / 1000)
						* 44 / 1000;
		}

	}
