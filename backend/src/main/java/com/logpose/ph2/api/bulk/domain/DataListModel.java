package com.logpose.ph2.api.bulk.domain;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import lombok.Data;

/**
 * DataListModel
 */
@Data
public final class DataListModel
	{
	// ===============================================
	// クラスメンバー
	// ===============================================
	private boolean status = false;
	private List<Double> voltages = new ArrayList<>(16);
	private String next = "000";
	private int count = 0;
	private Date castedAt = null;

	// ===============================================
	// 公開関数群
	// ===============================================
	public DataListModel()
		{
		for(int i = 0; i < 16; i++)
			{
			voltages.add(null);
			}
		}

	// ===============================================
	// 公開関数群
	// ===============================================
	public void addDataAndIncrement(double voltage)
		{
		voltages.set(count, voltage);
		count++;
		next = getChannnelNo(count);
		}

	// ===============================================
	// プライベート関数群
	// ===============================================
	private String getChannnelNo(int i)
		{
		return String.format("%02x", i) + "0";
		}
	}
