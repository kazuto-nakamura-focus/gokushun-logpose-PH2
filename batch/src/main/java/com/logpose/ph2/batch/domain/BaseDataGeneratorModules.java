package com.logpose.ph2.batch.domain;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.logpose.ph2.common.dao.db.entity.MessagesEnyity;
@Component
public final class BaseDataGeneratorModules
	{
	// ===============================================
	// 公開関数群
	// ===============================================
	// --------------------------------------------------
	/**
	 * Messageレコードリストから以下のデバイス-データマップを作成する。
	 *
	 * @param messages-Messageレコードリスト
	 * @return デバイス-データマップ
	 */
	// --------------------------------------------------
	public void createDeviceDataMap(List<MessagesEnyity> messages, Map<Long, DataListModel> map)
		{	
		for (MessagesEnyity item : messages)
			{
			// * レコードが対象とするデバイスを取得する
			DataListModel dataList = map.get(item.getDeviceId());
			if( null == dataList)
				{
				dataList = new DataListModel();
				map.put(item.getDeviceId(), dataList);
				}
			}
		
		}
	// --------------------------------------------------
	/**
	 * データリストの設定
	 *
	 * @return データリスト
	 */
	// --------------------------------------------------
	public DataListModel setData(Date castedTime, List<String> rawData, DataListModel dataList)
		{
		// * チャンネル番号が'000'の場合、データリストの以下の初期化を実行する。
		if(rawData.get(0).equals("000") )
			{
			dataList = new DataListModel();
			dataList.setStatus(true);
			dataList.setCastedAt(castedTime);
			}
		else if(!dataList.isStatus())
			{
			return dataList;
			}
		boolean isChannel = true;		
		for (String item : rawData)
			{
			if(isChannel)
				{
				if (dataList.getNext().equals(item))
					{
					isChannel = false;
					}
				else
					{
					dataList = new DataListModel();
					dataList.setStatus(false);
					return dataList;
					}
				}
			else
				{
				isChannel = true;
				Integer value  = Integer.parseInt(item, 16);
				double voltage = value.intValue() * 5000 / 4095;
				dataList.addDataAndIncrement(voltage);
				}
			}
		return dataList;
		}
	
	}
