package com.logpose.ph2.api.domain;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.logpose.ph2.api.algorythm.DeviceDayAlgorithm;
import com.logpose.ph2.api.dao.db.entity.Ph2SensorsEntity;
import com.logpose.ph2.api.dao.db.entity.Ph2SensorsEntityExample;
import com.logpose.ph2.api.dao.db.mappers.Ph2SensorsMapper;
import com.logpose.ph2.api.dao.db.mappers.joined.TopDomainMapper;
import com.logpose.ph2.api.dto.DataSummaryDTO;
import com.logpose.ph2.api.dto.ModelTargetDTO;
import com.logpose.ph2.api.dto.element.FieldData;
import com.logpose.ph2.api.dto.rawData.RawData;
import com.logpose.ph2.api.dto.rawData.RawDataList;

@Component
public class TopDomain
	{
	@Autowired
	private Ph2SensorsMapper ph2SensorsMapper;
	@Autowired
	private TopDomainMapper topDomainMapper;
	@Autowired
	private DeviceDayAlgorithm deviceDayAlgorithm;

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
			if (data.size() > 0)
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
	public List<ModelTargetDTO> getModelTargets(boolean isModel)
		{
		if (isModel)
			{
			return this.topDomainMapper.selectModelTargets();
			}
		else
			{
			return this.topDomainMapper.selectRawDataTargets();
			}
		}

	// --------------------------------------------------
	/**
	 * 生データを取得する
	 */
	// --------------------------------------------------
	public RawDataList getRawData(Date startDate, Date endDate, Long deviceId)
		{
		Calendar cal = Calendar.getInstance();
		cal.setTime(startDate);
		deviceDayAlgorithm.setTimeZero(cal);
		startDate = cal.getTime();

		cal.setTime(endDate);
		deviceDayAlgorithm.setTimeZero(cal);
		cal.add(Calendar.DATE, 1);
		endDate = cal.getTime();

		RawDataList results = new RawDataList();
// * センサー情報をコンテンツID順に取得する。
		Ph2SensorsEntityExample exm = new Ph2SensorsEntityExample();
		exm.createCriteria().andDeviceIdEqualTo(deviceId);
		exm.setOrderByClause("sensor_content_id asc");
		List<Ph2SensorsEntity> sensors = this.ph2SensorsMapper.selectByExample(exm);
// * MAPの生成
		Map<Long, RawData> sensorMap = new LinkedHashMap<>();
		for (Ph2SensorsEntity item : sensors)
			{
			sensorMap.put(item.getId(), null);
			results.getHeaders().add(item.getSensorContentId());
			}
// * センサー情報を日時順に取得
		List<RawData> records = this.topDomainMapper.selectRawData(startDate, endDate, deviceId);
		if (records.size() == 0)
			return results;
		Date prevTime = records.get(0).getCastedAt();
		List<RawData> tmp = new ArrayList<>();
		for (RawData elem : records)
			{
			// 時刻が違うデータの場合
			if (prevTime.getTime() != elem.getCastedAt().getTime())
				{
				results.getData().add(createRowData(prevTime, tmp, sensorMap));
				tmp.clear();
				// * 次に取得するデータの時間グループ
				prevTime = elem.getCastedAt();
				}
			tmp.add(elem);
			}
		if (tmp.size() > 0)
			{
			results.getData().add(createRowData(prevTime, tmp, sensorMap));
			}
		return results;
		}

	private List<String> createRowData(Date time, List<RawData> tmp, Map<Long, RawData> sensorMap)
		{
		List<String> data = new ArrayList<>();
// * 時刻
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss:SSS");
		data.add(dateFormat.format(time));
// * データのMAPへの埋め込み
		for (RawData item : tmp)
			{
			sensorMap.put(item.getSensorId(), item);
			}
// * Mapからの取出し
		for (Map.Entry<Long, RawData> entry : sensorMap.entrySet())
			{
			RawData rd = entry.getValue();
			if ((rd == null) || (time.getTime() != rd.getCastedAt().getTime()))
				{
				data.add("-");
				}
			// 時刻が一致している場合
			else
				{
				String value = rd.getValue();
				data.add(value);
				}

			}
		return data;
		}
	}
