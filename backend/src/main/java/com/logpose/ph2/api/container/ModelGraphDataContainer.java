package com.logpose.ph2.api.container;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import com.logpose.ph2.api.dto.graph.ModelGraphDataDTO;

public class ModelGraphDataContainer extends ModelGraphDataDTO
	{
	public static short DAY = 64; // 一日おき
	public static short DAYS2 = 128; // 2日間隔（３０日を除く）
	public static short DAYS5 = 256; // ５日間隔（３０日を除く）
	public static short DAYS10 = 512; // 10日間隔（３０日を除く）
	public static short DAYS15 = 1024; // 15日間隔（３０日を除く）
	public static short MONTH = 2048; // 1月間隔
	public static short DAY_1 = (short) (DAY | DAYS2 | DAYS5 | DAYS10 | DAYS15 | MONTH);

	private SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
	private Calendar calendar = Calendar.getInstance();

	// ===============================================
	// 公開関数群
	// ===============================================
	// --------------------------------------------------
	/**
	 *  グラフのカテゴリーと表示フラグを設定する
	 * 
	 * @param date データの日時
	 */
	// --------------------------------------------------
	public void addCategory(Date date)
		{
		this.calendar.setTime(date);
		short flag = this.$getFlag(this.calendar);
		this.getFlags().add(flag);
		this.getCategory().add(this.sdf.format(this.calendar.getTime()));
		}

	// --------------------------------------------------
	/**
	 *  カテゴリーから X軸の最大値と最小値を設定する
	 */
	// --------------------------------------------------
	public void setX()
		{
		// * 最小値・最大値の設定
		String first = this.getCategory().get(0);
		String last = this.getCategory().get(this.getCategory().size() - 1);
		this.setXStart(first);
		this.setXEnd(last);
		}

	// --------------------------------------------------
	/**
	 *  Y軸の最大値と最小値を設定する
	 *  @param min
	 *  @param max
	 */
	// --------------------------------------------------
	public void setY(double min, double max)
		{
		this.setYStart(min);
		this.setYEnd(max);
		}
	
	// ===============================================
	// 非公開関数群
	// ===============================================
	private short $getFlag(Calendar cal)
		{
		int date = cal.get(Calendar.DATE);
		if (date == 1)
			{
			return DAY_1;
			}
		else
			{
			short flag = DAY;
			if (date % 2 == 0)
				{
				flag = (short) (flag | DAYS2);
				}
			if (date != 30)
				{
				if (date % 5 == 0)
					{
					flag = (short) (flag | DAYS5);
					}
				if (date % 10 == 0)
					{
					flag = (short) (flag | DAYS10);
					}
				if (date % 15 == 0)
					{
					flag = (short) (flag | DAYS15);
					}
				}
			return (short) flag;
			}
		}

	}
