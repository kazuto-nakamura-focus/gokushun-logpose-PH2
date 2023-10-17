package com.logpose.ph2.api.domain;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.logpose.ph2.api.dao.db.mappers.joined.TopDomainMapper;
import com.logpose.ph2.api.dto.DataSummaryDTO;
import com.logpose.ph2.api.dto.ModelTargetDTO;
import com.logpose.ph2.api.dto.element.FieldData;

@Component
public class TopDomain
	{
	@Autowired
	private TopDomainMapper topDomainMapper;

	// ===============================================
	// パブリック関数
	// ===============================================
	// --------------------------------------------------
	/**
	 * トップ画面で圃場データを取得する
	 */
	// --------------------------------------------------
	public List<DataSummaryDTO> getFieldData()
		{
		// * デバイスリストの取得
		List<DataSummaryDTO> devices = this.topDomainMapper
				.selectFieldDeviceList();
		// * 取得する日付
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.MONTH, -3);
		Date date = cal.getTime();

		List<DataSummaryDTO> result = new ArrayList<>();
		for (DataSummaryDTO item : devices)
			{
			List<FieldData> data = this.topDomainMapper
					.selectFieldDataList(item.getDeviceId(), date);
			if(data.size() > 0)
				{
				item.setDataList(data);
				result.add(item);
				}
			}
		return result;
		}
	// --------------------------------------------------
	/**
	 * トップ画面で項目に対してデバイスのデータのリストを取得する
	 */
	// --------------------------------------------------
	public List<FieldData> getDeviceDataList(Long contentId)
		{
		// * 取得する日付
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.MONTH, -1);
		Date date = cal.getTime();
		// * モデルデータが存在するデバイス
		return this.topDomainMapper.selectDeviceDataList(contentId, date);
		}
	// --------------------------------------------------
	/**
	 * モデルの対象リストを取得する
	 */
	// --------------------------------------------------
	public List<ModelTargetDTO> getModelTargets()
		{
		return this.topDomainMapper.selectModelTargets();
		}
	// --------------------------------------------------
	/**
	 * 生データを取得する
	 */
	// --------------------------------------------------
	public List<String[]> getRawData(Date startDate, Date endDate, Long deviceId)
		{
		List<String[]> result = new ArrayList<>();
		List<FieldData> records = this.topDomainMapper.selectRawData(startDate, endDate, deviceId);
		Date prevTime  = new Date(0);
		String[] rawData = null;
		for(FieldData elem : records)
			{
			// 時刻が違うデータの場合
			if(prevTime.getTime() != elem.getCastedAt().getTime())
				{
				// * 既に取得済みのデータがある場合は返却データに登録する
				if(null != rawData) result.add(rawData);
				// * 取得用のデータの初期化
				rawData = new String[12];
				rawData[0] = elem.getCastedAt().toLocaleString();
				// * 次に取得するデータの時間グループ
				prevTime = elem.getCastedAt();
				}
			rawData[elem.getId().intValue()] = elem.getValue();
			}
		if(null != rawData) result.add(rawData);
		return result;
		}
	}
