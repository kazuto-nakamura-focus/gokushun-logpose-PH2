package com.logpose.ph2.api.domain;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.logpose.ph2.api.algorythm.DeviceDayAlgorithm;
import com.logpose.ph2.api.dao.db.entity.Ph2RawDataEntity;
import com.logpose.ph2.api.dao.db.entity.Ph2RawDataEntityExample;
import com.logpose.ph2.api.dao.db.entity.joined.SensorItemDTO;
import com.logpose.ph2.api.dao.db.mappers.Ph2RawDataMapper;
import com.logpose.ph2.api.dao.db.mappers.joined.SensorJoinMapper;
import com.logpose.ph2.api.dto.sensorData.SensorDataDTO;
import com.logpose.ph2.api.utility.DateTimeUtility;

/**
 * グラフページに対応する処理の集まり
 */
@Component
public class SensorDataDomain
	{
	// ===============================================
	// クラスメンバー
	// ===============================================
	@Autowired
	private SensorJoinMapper sensorJoinMapper;
	@Autowired
	private Ph2RawDataMapper ph2RawDataMapper;

	// ===============================================
	// 公開関数群
	// ===============================================
	// --------------------------------------------------
	/**
	 * デバイスに属するセンサーの一覧を取得する。
	 * コンテンツ名・センサー名とIDを返す。
	 * 	
	 * @param deviceId - デバイスID
	 * @return List<SensorItemDTO>
	 */
	// --------------------------------------------------
	public List<SensorItemDTO> getSensorItemsByDevice(Long deviceId)
		{
		return this.sensorJoinMapper.selectSensorList(deviceId);
		}

	// --------------------------------------------------
	/**
	 * ある期間内のセンサーのデータを返す。
	 * 	
	 * @param sensorId - センサーID
	 * @param startDate - 取得期間の開始日
	 * @paraｍ endDate - 取得期間の終了日
	 * @return GraphDataDTO
	 * @throws ParseException 
	 */
	// --------------------------------------------------
	public SensorDataDTO getSensorGraphData(
			Long sensorId, Date startDate, Date endDate, Short type, short hour)
			throws ParseException
		{
// * ダッシュボードテーブルからデータを取得する
		List<Ph2RawDataEntity> records;
// デイリーベースでの取得
		if (type.shortValue() == 0)
			{
			Calendar cal = Calendar.getInstance();
			cal.set(Calendar.HOUR_OF_DAY, hour);
			cal.set(Calendar.MINUTE, 0);
			Date startTime = cal.getTime();
			cal.set(Calendar.MINUTE, 20);
			Date endTime = cal.getTime();
			// * 時間条件の設定;
			records = this.ph2RawDataMapper.selectForDate(sensorId, startDate, endDate, startTime,
					endTime);
			}
		// 時間単位での取得
		else
			{
			Ph2RawDataEntityExample exm = new Ph2RawDataEntityExample();
			exm.createCriteria().andSensorIdEqualTo(sensorId)
					.andCastedAtGreaterThanOrEqualTo(startDate)
					.andCastedAtLessThanOrEqualTo(endDate);
			records = this.ph2RawDataMapper.selectByExample(exm);
			}
		// * 取得したレコードを返却用オブジェクトに代入する
		SensorDataDTO dto = new SensorDataDTO();
		double min = Double.MAX_VALUE;
		double max = Double.MIN_VALUE;
		int year = 0;
		int month = 0;
		int day = 0;
		Calendar cal = Calendar.getInstance();

		for (Ph2RawDataEntity entity : records)
			{
			cal.setTime(entity.getCastedAt());
			boolean isDayChanged = true;
			if ((cal.get(Calendar.YEAR) == year) && (cal.get(Calendar.MONTH) == month)
					&& (cal.get(Calendar.DATE) == day))
				{
				// ディリーベースの場合
				if (type == 0)
					continue;
				isDayChanged = false;
				}
			year = cal.get(Calendar.YEAR);
			month = cal.get(Calendar.MONTH);
			day = cal.get(Calendar.DATE);

			String rowVal = entity.getValue();
			Double value = Double.valueOf(rowVal);
			if (min > value)
				min = value;
			if (max < value)
				max = value;
			dto.getValues().add(value);

			// ディリーベースの場合
			String label;
			if (type == 0)
				{
				label = String.valueOf(cal.get(Calendar.MONTH) + 1)
						+ "/" + String.valueOf(cal.get(Calendar.DATE));
				}
			// 時間ベースの場合
			else
				{
				if (isDayChanged)
					{
					label = String.valueOf(cal.get(Calendar.MONTH) + 1)
							+ "/" + String.valueOf(cal.get(Calendar.DATE)) + " ";
					}
				label = String.valueOf(cal.get(Calendar.HOUR_OF_DAY))
						+ ":" + String.valueOf(cal.get(Calendar.MINUTE));
				}
			dto.getCategory().add(label);
			}
		dto.setXStart(DateTimeUtility.getStringFromDate(startDate));
		dto.setXEnd(DateTimeUtility.getStringFromDate(endDate));
		dto.setYStart(min);
		dto.setYEnd(max);
		return dto;
		}

	// --------------------------------------------------
	/**
	 * ある期間内のセンサーのデータを返す。
	 * 	
	 * @param deviceId - デバイスID
	 * @param sensorId - センサーID
	 * @param startDate - 取得期間の開始日
	 * @paraｍ endDate - 取得期間の終了日
	 * 	@param interval - 取得間隔(分)
	 * @return GraphDataDTO
	 * @throws ParseException 
	 */
	// --------------------------------------------------
	public SensorDataDTO getSensorGraphDataByInterval(Long deviceId,
			Long sensorId, Date startDate, Date endDate, long minutes)
			throws ParseException
		{
// * デバイス情報を得る
// Ph2DevicesEntity device = this.ph2DeviceMapper.selectByPrimaryKey(deviceId);
// ZoneId deviceZoneId = ZoneId.of(device.getTz());
// ZoneId tokyoeZoneId = ZoneId.of("Asia/Tokyo");
// 現在の日時を指定タイムゾーンで取得
// ZoneOffset deviceZoneOffset = ZonedDateTime.now(deviceZoneId).getOffset();
// ZoneOffset tokyoZoneOffset = ZonedDateTime.now(tokyoeZoneId).getOffset();
// オフセットを秒単位で取得
// long offsetInSeconds = (deviceZoneOffset.getTotalSeconds() -
// tokyoZoneOffset.getTotalSeconds()) * 1000;
		SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd HH:mm");
		SensorDataDTO results = new SensorDataDTO();// * 返却用データ
		double min = Double.MAX_VALUE;// * グラフのY軸最小値
		double max = Double.MIN_VALUE;// *グラフのY軸最大値
		DeviceDayAlgorithm deviceDayAlgorithm = new DeviceDayAlgorithm();

// * 検索の開始時刻の設定
		Calendar startTime = Calendar.getInstance();
		startTime.setTime(startDate);
		deviceDayAlgorithm.setTimeZero(startTime);
		results.setXStart(DateTimeUtility.getStringFromDate(startDate));// 表示用時刻

		long seek_time = startTime.getTimeInMillis(); // * 時差分遡る
		long min_time = seek_time - 1000 * 60 * 6;
		long max_time = seek_time + 1000 * 60 * 6;

// * 10分前に設定
		startTime.setTimeInMillis(seek_time - 10 * 60 * 1000);
// * 検索の終了時刻の設定
		Calendar endTime = Calendar.getInstance();
		endTime.setTime(endDate);
		endTime.add(Calendar.DATE, 1);
		deviceDayAlgorithm.setTimeZero(endTime);
		results.setXEnd(DateTimeUtility.getStringFromDate(endDate));// 表示用時刻
		long end_time = endTime.getTimeInMillis(); // * 時差分遡る

// * 開始日時から終了日時までのデータを取得する
		Ph2RawDataEntityExample exm = new Ph2RawDataEntityExample();
		exm.createCriteria().andSensorIdEqualTo(sensorId)
				.andCastedAtGreaterThanOrEqualTo(startTime.getTime())
				.andCastedAtLessThan(endTime.getTime());
		exm.setOrderByClause("casted_at");
		List<Ph2RawDataEntity> records = this.ph2RawDataMapper.selectByExample(exm);
		if (records.size() == 0) return results;

// * インターバルは分単位なので、millisecondsに変換する
		long interval = 60000 * minutes;
		Ph2RawDataEntity prev_data = records.get(0);

		Calendar cal = Calendar.getInstance();
		int index = 0;
		for (; seek_time < end_time; seek_time += interval, min_time += interval, max_time += interval)
			{
			Double value = null;
			for (; index < records.size(); index++)
				{
				Ph2RawDataEntity entity = records.get(index);
// * 検査するデータの取得時刻を得る
				long data_time = entity.getCastedAt().getTime();
// * 検査するデータが求める時刻の前後６分以内で無い場合、
// * ---- ６分前であるならば、次のデータを取り出す
				if (data_time < min_time)
					{
					prev_data = entity;
					continue;
					}
// * ---- ６分後であるならば、空のデータとして処理する
				else if (data_time > max_time)
					{
					prev_data = entity;
					break;
					}
				else
					{
// * 検査するデータが求める時刻の範囲以内の場合
// * --- 現在求める時間に対して、最も近い時刻のデータを選択する
					long prev_diff = seek_time - prev_data.getCastedAt().getTime();
					long next_diff = data_time - seek_time;
					Ph2RawDataEntity target = Math.abs(prev_diff) <= Math.abs(next_diff) ? prev_data : entity;
					// * 値の代入
					value = Double.valueOf(target.getValue());
					// * 最大値・最小値
					if (min > value) min = value;
					if (max < value) max = value;
					prev_data = entity;
					break;
					}
				}
// * 値の追加
			results.getValues().add(value);
// * 時刻フラグ
			cal.setTimeInMillis(seek_time);
			short flg = this.getFlag(cal);
			results.getFlags().add(flg);

// * カテゴリーの追加
			StringBuilder sb = new StringBuilder(dateFormat.format(cal.getTime()));
			results.getCategory().add(sb.toString());
			}

		results.setYStart(min);
		results.setYEnd(max);
		results.setInterval(minutes);
		return results;
		}

	// ===============================================
	// 非公開関数群
	// ===============================================
	private short getFlag(Calendar cal)
		{
		int minutes = cal.get(Calendar.MINUTE);
		if (0 != minutes)
			{
			if (30 != minutes) return 0;
			else
				return SensorDataDTO.THIRTY;
			}
		int flag = 6; // THIRTY|HOUR;
		int hour = cal.get(Calendar.HOUR_OF_DAY);
		if (hour == 0) // ０時
			{
			flag = (short) (flag | SensorDataDTO.HOUR_0);
			}
		// 0 時でない場合
		else
			{
			flag = flag | SensorDataDTO.HOUR;
			if (hour % 2 == 0)
				{
				flag = flag | SensorDataDTO.HOUR2;
				}
			if (hour % 4 == 0)
				{
				flag = flag | SensorDataDTO.HOUR4;
				}
			if (hour % 6 == 0)
				{
				flag = flag | SensorDataDTO.HOUR6;
				}
			if (hour % 12 == 0)
				{
				flag = flag | SensorDataDTO.HOUR12;
				}
			return (short) flag;
			}
		int date = cal.get(Calendar.DATE);
		if (date == 1) // １日
			{
			return (short) (flag | SensorDataDTO.DAY_1);
			}
		else
			{
			flag = flag | SensorDataDTO.DAY;
			if (date != 30)
				{
				if (date % 5 == 0)
					{
					flag = flag | SensorDataDTO.DAYS5;
					}
				if (date % 10 == 0)
					{
					flag = flag | SensorDataDTO.DAYS10;
					}
				if (date % 15 == 0)
					{
					flag = flag | SensorDataDTO.DAYS15;
					}
				}
			return (short) flag;
			}
		}
	}
