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
import com.logpose.ph2.api.dao.db.entity.Ph2WeatherForecastEntity;
import com.logpose.ph2.api.dao.db.entity.Ph2WeatherForecastEntityExample;
import com.logpose.ph2.api.dao.db.mappers.Ph2SensorsMapper;
import com.logpose.ph2.api.dao.db.mappers.Ph2WeatherForecastMapper;
import com.logpose.ph2.api.dao.db.mappers.joined.TopDomainMapper;
import com.logpose.ph2.api.dto.DataSummaryDTO;
import com.logpose.ph2.api.dto.ModelTargetDTO;
import com.logpose.ph2.api.dto.element.FieldData;
import com.logpose.ph2.api.dto.rawData.RawData;
import com.logpose.ph2.api.dto.rawData.RawDataList;
import com.logpose.ph2.api.dto.top.FieldDataWithSensor;

/**
 * 主要な処理を行うドメイン
 */
@Component
public class TopDomain
	{
	// ===============================================
	// クラスメンバー
	// ===============================================
	@Autowired
	private Ph2SensorsMapper ph2SensorsMapper;
	@Autowired
	private Ph2WeatherForecastMapper ph2WeatherForecastMapper;
	@Autowired
	private TopDomainMapper topDomainMapper;
	@Autowired
	private DeviceDayAlgorithm deviceDayAlgorithm;

	// ===============================================
	// パブリック関数群
	// ===============================================
	// --------------------------------------------------
	/**
	 * 全圃場サマリーデータ取得
	 *
	 * @return List<DataSummaryDTO>
	 */
	// --------------------------------------------------
	public List<DataSummaryDTO> getFieldData()
		{
// * デバイスリストの取得
		List<DataSummaryDTO> devices = this.topDomainMapper
				.selectFieldDeviceList();
// * 各デバイス毎にセンサーデータを取得する
		FieldData dammy = new FieldData();
		dammy.setValue(null);
		for (DataSummaryDTO item : devices)
			{
// * 表示用に１０セル分のデータをデフォルト作成
			List<FieldData> display = new ArrayList<>();
			for(int i=0;i<10;i++)
				{
				display.add(dammy);
				}
			item.setDataList(display);
// * セルデータを取得し、リストの表示番号-1に対して入れ替え
			List<FieldData> data = this.topDomainMapper
					.selectFieldDataList(item.getDeviceId());
			if (data.size() > 0)
				{
				for(final FieldData fieldData : data)
					{
					Short displayNo = fieldData.getDisplayNo();
					if(null != displayNo)
						{
						display.set(displayNo-1, fieldData);
						}
					else
						{
						display.set(fieldData.getId().shortValue()-1, fieldData);
						}
					};
				}
// * 気象情報の取得
			Ph2WeatherForecastEntityExample exm = new Ph2WeatherForecastEntityExample();
			exm.createCriteria().andDeviceIdEqualTo(item.getDeviceId());
			exm.setOrderByClause("time");
			List<Ph2WeatherForecastEntity> weather = this.ph2WeatherForecastMapper.selectByExample(exm);
			item.setForecastList(weather);
			}
		
		return devices;
		}

	// --------------------------------------------------
	/**
	 * 各デバイス毎の同一タイプのセンサーデータを取得する。
	 * 
	 * @param contentId 検知するデータタイプのID
	 * @return List<FieldDataWithSensor>
	 */
	// --------------------------------------------------
	public List<FieldDataWithSensor> getDeviceDataList(Long contentId)
		{
// * 各デバイス毎の同一タイプのセンサーデータを取得する。
		return this.topDomainMapper.selectDeviceDataList(contentId);
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
	 * 
	 * @param startDate 開始日
	 * @param endDate 終了日
	 * @param deviceId デバイスID
	 */
	// --------------------------------------------------
	public RawDataList getRawData(Date startDate, Date endDate, Long deviceId)
		{
// * 戻り値の初期化
		RawDataList results = new RawDataList();

// * 開始日の時刻をゼロに設定する
		startDate = deviceDayAlgorithm.getTimeZero(startDate);

// * 終了日は1日追加して時刻をゼロに設定する。
		Calendar cal = Calendar.getInstance();
		cal.setTime(endDate);
		cal.add(Calendar.DATE, 1);
		endDate = deviceDayAlgorithm.getTimeZero(cal);

// * センサー情報をコンテンツID順に取得する。
		Ph2SensorsEntityExample exm = new Ph2SensorsEntityExample();
		exm.createCriteria().andDeviceIdEqualTo(deviceId);
		exm.setOrderByClause("sensor_content_id asc");
		List<Ph2SensorsEntity> sensors = this.ph2SensorsMapper.selectByExample(exm);

// * コンテンツIDのMAPの生成
		Map<Long, RawData> sensorMap = new LinkedHashMap<>();
		for (Ph2SensorsEntity item : sensors)
			{
			sensorMap.put(item.getId(), null);
			results.getHeaders().add(item.getSensorContentId());
			}
// * センサー情報を日時順に取得
		List<RawData> records = this.topDomainMapper.selectRawData(startDate, endDate, deviceId);

// * データが無い場合は空のデータを返却する
		if (records.size() == 0)
			return results;

// * センサー情報に対して、時刻単位にデータを作成し、返却データに追加する
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
// * 最後の時間分のデータを作成し、返却データに追加する
		if (tmp.size() > 0)
			{
			results.getData().add(createRowData(prevTime, tmp, sensorMap));
			}
		return results;
		}

	// ===============================================
	// プライベート関数群
	// ===============================================
	// --------------------------------------------------
	/**
	 * 引数のtimeで取得されたデータをセンサー情報情報ごとに整理して、文字列リストにして返却する。
	 * @param time
	 * @param tmp
	 * @param sensorMap
	 * @return List<String>
	 */
	// --------------------------------------------------
	private List<String> createRowData(Date time, List<RawData> tmp, Map<Long, RawData> sensorMap)
		{
		List<String> data = new ArrayList<>();
// * 時刻
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss:SSS");
		data.add(dateFormat.format(time));
// * データのMAPへの埋め込み…①
		for (RawData item : tmp)
			{
			sensorMap.put(item.getSensorId(), item);
			}
// * Mapからの取出し
		for (Map.Entry<Long, RawData> entry : sensorMap.entrySet())
			{
			RawData rd = entry.getValue();
// * データが無い場合
			if (rd == null)
				{
				data.add("-");
				}
// * ①の処理で、未更新（古いデータが残っている）の場合
			else if (time.getTime() != rd.getCastedAt().getTime())
				{
				data.add("-");
				}
// * 時刻が一致している場合
			else
				{
				data.add(rd.getValue());
				}
			}
		return data;
		}
	}
