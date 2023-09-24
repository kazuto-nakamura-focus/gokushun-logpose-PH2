package com.logpose.ph2.api.domain;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.logpose.ph2.api.dao.db.entity.Ph2DashBoardEntity;
import com.logpose.ph2.api.dao.db.entity.Ph2DashBoardEntityExample;
import com.logpose.ph2.api.dao.db.entity.joined.SensorItemDTO;
import com.logpose.ph2.api.dao.db.mappers.Ph2DashBoardMapper;
import com.logpose.ph2.api.dao.db.mappers.joined.SensorJoinMapper;
import com.logpose.ph2.api.dto.sensorData.SenseorDataDTO;
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
	private Ph2DashBoardMapper ph2DashBoardMapper;

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
	public SenseorDataDTO getSensorGraphData(
			Long sensorId, Date startDate, Date endDate, Short type, short hour)
			throws ParseException
		{
		// * ダッシュボードテーブルからデータを取得する
		List<Ph2DashBoardEntity> records;
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
			records = this.ph2DashBoardMapper.selectForDate(sensorId, startDate, endDate, startTime,
					endTime);
			}
		// 時間単位での取得
		else
			{
			Ph2DashBoardEntityExample exm = new Ph2DashBoardEntityExample();
			exm.createCriteria().andSensorIdEqualTo(sensorId)
					.andCastedAtGreaterThanOrEqualTo(startDate)
					.andCastedAtLessThanOrEqualTo(endDate);
			records = this.ph2DashBoardMapper.selectByExample(exm);
			}
		// * 取得したレコードを返却用オブジェクトに代入する
		SenseorDataDTO dto = new SenseorDataDTO();
		double min = Double.MAX_VALUE;
		double max = Double.MIN_VALUE;
		int year = 0;
		int month = 0;
		int day = 0;
		Calendar cal = Calendar.getInstance();

		for (Ph2DashBoardEntity entity : records)
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
			day = cal.get(Calendar.MONTH);

			String rowVal = entity.getValue();
			Double value = Double.valueOf(rowVal);
			if (min > value)
				min = value;
			if (max < value)
				max = value;
			dto.getValues().add(value);
			// * カテゴリーの設定
			Date dateTime = entity.getCastedAt();
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
				if(isDayChanged)
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

	}
