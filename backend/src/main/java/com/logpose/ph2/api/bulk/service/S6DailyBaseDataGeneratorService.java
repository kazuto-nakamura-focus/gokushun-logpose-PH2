package com.logpose.ph2.api.bulk.service;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.logpose.ph2.api.bulk.domain.DailyBaseDataGenerator;
import com.logpose.ph2.api.bulk.domain.DeviceDayDataSelector;
import com.logpose.ph2.api.bulk.domain.DeviceLogDomain;
import com.logpose.ph2.api.bulk.vo.LoadCoordinator;
import com.logpose.ph2.api.dao.db.entity.Ph2DeviceDayEntity;
import com.logpose.ph2.api.dao.db.mappers.Ph2DeviceDayMapper;
import com.logpose.ph2.api.dto.DataSourceType;
import com.logpose.ph2.api.master.DeviceDayMaster;

import lombok.val;

@Service
public class S6DailyBaseDataGeneratorService
	{
	// ===============================================
	// クラスメンバー
	// ===============================================
	private static Logger LOG = LogManager
			.getLogger(S6DailyBaseDataGeneratorService.class);
	@Autowired
	private DeviceLogDomain deviceLogDomain;
	@Autowired
	private DailyBaseDataGenerator dailyBaseDataGenerator;
	@Autowired
	private Ph2DeviceDayMapper ph2DeviceDayMapper;

	// ===============================================
	// 公開関数群
	// ===============================================
	// --------------------------------------------------
	/**
	 * 10分単位データから日単位のデータを作成し、ディリーデータ・テーブルに登録する
	 * @param ldc
	 * @param deviceDays
	 */
	// --------------------------------------------------
	public void doService(LoadCoordinator ldc, List<Ph2DeviceDayEntity> deviceDays) throws ParseException
		{
// * センサーデータから日単位のデータに変換し、ディリーデータ・テーブルに登録する。
		List<Ph2DeviceDayEntity> allUnsetDevices = this.dailyBaseDataGenerator.loadData(deviceDays);
// * 作成されなかったディリーデータのリストを年度ごとに分割する
		List<List<Ph2DeviceDayEntity>> unsetDevicesByYear = this.createListByYears(allUnsetDevices);
// * 年度ごとに以下の処理を行う
		for (final List<Ph2DeviceDayEntity> unsetDevices : unsetDevicesByYear)
			{
			this.deviceLogDomain.log(LOG, ldc.getDeviceId(), getClass(),
					unsetDevices.get(0).getYear() + "年度のデータを処理します。");
// * 作成されなかった日付に対して、去年のデータを追加する
			List<Ph2DeviceDayEntity> remainList = this.dailyBaseDataGenerator.loadFromPreviousYear(ldc.getDevice(),
					unsetDevices);
// * 作成されなかった日付に対して、引継ぎ元データを追加する
			remainList = this.dailyBaseDataGenerator.loadFromPreviousDevice(ldc.getDevice(), remainList);
// * 作成されなかった日付に対して、天気予報マスターからデータを追加する
			remainList = this.dailyBaseDataGenerator.loadFromWheatherMaster(ldc.getDevice(), remainList);
// * 天気予報マスターに存在しなかったら、天気予報APIをコールし、天気予報マスターを作成する
			this.dailyBaseDataGenerator.createWheatherMaster(ldc.getDevice(), remainList);
// * 再度、作成されなかった日付に対して、天気予報マスターからデータを追加する
			this.dailyBaseDataGenerator.loadFromWheatherMaster(ldc.getDevice(), remainList);
// * データの状態を取得する
			List<DataSourceType> dataTypes = (new DeviceDayDataSelector(this.ph2DeviceDayMapper, ldc.getDeviceId(),
					unsetDevices.get(0).getYear())).createList();
			this.deviceLogDomain.log(LOG, ldc.getDeviceId(), getClass(), "以下の期間のモデルデータの参照元は以下の通りです。");
			for(val item : dataTypes)
				{
				short status = item.getStatus();
				String typeName = "データ無し";
				if(status == DeviceDayMaster.ORIGINAL)
					{
					typeName = "保有するセンサ―データ";
					}
				else if(status == DeviceDayMaster.PREVIOUS_DEVICE)
					{
					typeName = "引継ぎ元データからのセンサーデータ";
					}
				else if(status == DeviceDayMaster.PREVIOUS_YEAR)
					{
					typeName = "昨年度のセンサ―データ";
					}
				else if(status == DeviceDayMaster.WHEATHER)
					{
					typeName = "天気予報APIからのデータ";
					}
				final String logStart = this.deviceLogDomain.date(item.getStartDate(), "不明時刻");
				final String logEnd = this.deviceLogDomain.date(item.getEndDate(), "不明時刻");
				this.deviceLogDomain.log(LOG, ldc.getDeviceId(), getClass(),
						logStart+ "～"+ logEnd + " " + typeName);
				}
			}
		}

	// ===============================================F
	// プライベート関数群
	// ===============================================
	private List<List<Ph2DeviceDayEntity>> createListByYears(List<Ph2DeviceDayEntity> unsetDevices)
		{
		List<List<Ph2DeviceDayEntity>> result = new ArrayList<>();
		List<Ph2DeviceDayEntity> list = null;
		int year = 0;
		for (final Ph2DeviceDayEntity entity : unsetDevices)
			{
			if (entity.getYear().shortValue() != year)
				{
				list = new ArrayList<>();
				result.add(list);
				year = entity.getYear();
				}
			list.add(entity);
			}
		return result;
		}
	}
