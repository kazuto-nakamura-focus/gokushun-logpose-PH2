package com.logpose.ph2.api.service.data_load;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.logpose.ph2.api.bulk.domain.DeviceLogDomain;
import com.logpose.ph2.api.bulk.domain.DeviceStatusDomain;
import com.logpose.ph2.api.bulk.service.S0Initializer;
import com.logpose.ph2.api.controller.dto.TimeMessage;
import com.logpose.ph2.api.dao.db.entity.Ph2DeviceLogEntity;
import com.logpose.ph2.api.dao.db.entity.Ph2DevicesEntity;
import com.logpose.ph2.api.dao.db.entity.joined.Ph2BatchLogEntityExtendDevices;
import com.logpose.ph2.api.dto.dataLoad.DataLoadInfo;
import com.logpose.ph2.api.utility.DateTimeUtility;

import lombok.val;

@Service
public class DataLoadTopService
	{
	// ===============================================
	// クラスメンバー
	// ===============================================
	private static Logger LOG = LogManager.getLogger(DataLoadTopService.class);
	@Autowired
	private DeviceLogDomain deviceLogDomain;
	@Autowired
	private DeviceStatusDomain statusDomain;
	@Autowired
	private S0Initializer s0Initializer;
	@Autowired
	private DataLoadService dataLoadService;

	// ===============================================
	// 公開関数群
	// ===============================================
	// --------------------------------------------------------
	/**
	 * デバイスのバッチ情報を得る
	 * @return List<ObjectStatus>
	 * @throws ParseException 
	 */
	// --------------------------------------------------------
	public List<DataLoadInfo> getInfo() throws ParseException
		{
		List<DataLoadInfo> results = new ArrayList<>();
		List<Ph2BatchLogEntityExtendDevices> records = this.deviceLogDomain.getLog();
		for (val item : records)
			{
			DataLoadInfo info = new DataLoadInfo();
			info.setId(item.getDeviceId());
			info.setName(item.getName());
			info.setStatus("不明");
			info.setDataStatus(this.statusDomain.getValue(item.getDataStatus()));

// * センサーロード待ち
			if (this.statusDomain.isAll(item.getDataStatus()))
				{
// *センサーロードの起動前 - 開始時間が無いか、終了時間がある
				if (null == item.getUploadBeginTime() || null != item.getUploadEndTime())
					{
					info.setStatus("センサーデータ再ロードの実行待ち");
					}
// * センサーロードの実行中
				else if (null != item.getUploadBeginTime() && null == item.getUploadEndTime())
					{
					info.setStatus("センサーデータ再ロードの実行中");
					}
				}
// * 通常
			else
				{
				Date now = new Date();
				String status = "";
				if (null != item.getUploadEndTime())
					{
					if (item.getUploadEndTime().getTime() * 120 * 60 * 1000 > now.getTime())
						{
						info.setStatus("センサーデータ再ロードの実行済み／");
						}
					}
				if (null != item.getUpdateBeginTime() && null == item.getUpdateEndTime())
					{
					info.setStatus(status + "更新バッチ実行中");
					}
				else if (null != item.getUpdateBeginTime() && null != item.getUpdateEndTime())
					{
					info.setStatus(status + "更新バッチ実行済み");
					}
				else if (null == item.getUpdateBeginTime())
					{
					info.setStatus(status + "更新バッチ実行待ち");
					}
				}
			Date uploadTime = item.getUploadEndTime();
			if (null == uploadTime) uploadTime = item.getUploadBeginTime();
			if (null == uploadTime) info.setLoadTime(null);
			else
				info.setLoadTime(DateTimeUtility.getStringFromDateTime(uploadTime));

			Date updateTime = item.getUpdateEndTime();
			if (null == updateTime) updateTime = item.getUpdateBeginTime();
			if (null == updateTime) info.setUpdateTime(null);
			else
				info.setUpdateTime(DateTimeUtility.getStringFromDateTime(updateTime));

			results.add(info);
			}
		return results;
		}

	// --------------------------------------------------------
	/**
	 * デバイスのログ情報を得る
	 * @param デバイスID
	 * @param タイプ
	 * @return List<TimeMessage>
	 * @throws ParseException 
	 */
	// --------------------------------------------------------
	public List<TimeMessage> getLog(Long deviceId, short type) throws ParseException
		{
		List<Ph2DeviceLogEntity> records = this.deviceLogDomain.getBatchStatus(deviceId, type);
		List<TimeMessage> log = new ArrayList<>();
		final String start = "***START";
		final String end = "***END";
		int inLog = 0;
		for (val item : records)
			{
			if (inLog == 0)
				{
				if (item.getMessage().startsWith(start))
					{
					inLog = 1;
					}
				else
					continue;
				}
			else
				{
				if (item.getMessage().startsWith(end))
					{
					inLog = 2;
					}
				}
			boolean isError = (null != item.getMessage()) &&(item.getMessage().length()>0)&&(item.getMessage().charAt(0)=='#');
			String message = (isError)?item.getMessage().substring(1):item.getMessage();
			TimeMessage mssg = TimeMessage.builder()
					.date(DateTimeUtility.getStringFromDateTime(item.getTime()))
					.status(!isError)
					.message(message).build();
			log.add(mssg);
			if (inLog == 2) break;
			}
		return log;
		}

	// --------------------------------------------------------
	/**
	 * 処理の受付を行う
	 * @param デバイスID
	 */
	// --------------------------------------------------------
	public void request(Long deviceId)
		{
// * デバイスの指定がある場合
		if (deviceId.longValue() >= 0)
			{
			this.statusDomain.prepareForUpdate(deviceId);
			}
		else
			{
			this.statusDomain.setAllStatusToAllLoaded();
			}
		}

	// --------------------------------------------------------
	/**
	 * バッチ処理を振り分ける
	 * @param デバイスID
	 */
	// --------------------------------------------------------
	public void update()
		{
// * デバイスリストを得る
		List<Ph2DevicesEntity> devices = this.statusDomain.selectAll();
		Map<Long, Ph2DevicesEntity> finishList = new HashMap<>();
// * 各デバイス毎に処置を行う
		for (val entity : devices)
			{
			if (this.statusDomain.isAll(entity))
				{
				try
					{
					if(finishList.containsKey(entity.getId()) ) continue;
					LOG.error(entity.getId() + "の全ロードを開始します。");
					this.dataLoadService.loadDevices(true, entity, finishList);
					}
				catch (Exception e)
					{
					LOG.error(entity.getId() + "の全ロードに失敗しました。");
					e.printStackTrace();
					}
				}
			}
		devices = this.statusDomain.selectAll();
		Calendar now = Calendar.getInstance();
		Calendar deviceUpdated = Calendar.getInstance();
		final long tenMinutesInMillis = 10 * 60 * 1000;
		for (val entity : devices)
			{
			deviceUpdated.setTime(entity.getDataStatusDate());

			// 2つのCalendarオブジェクトの時間の差を計算
			long differenceInMillis = Math.abs(now.getTimeInMillis() - deviceUpdated.getTimeInMillis());

			// 差が10分以上かどうかを判定
			if (differenceInMillis >= tenMinutesInMillis)
				{
				try
					{
					LOG.error(entity.getId() + "の更新を行います。");
					this.dataLoadService.loadDevices(false, entity, finishList);
					}
				catch (Exception e)
					{
					LOG.error(entity.getId() + "の更新に失敗しました。");
					e.printStackTrace();
					}
				}
			}
		}

	// --------------------------------------------------
	/**
	 * 全てのデバイスのメッセージテーブルからまだロードされていないデータ
	 * を各テーブルに加工して取り込む
	 * @throws IOException
	 * @throws ParseException
	 */
	// --------------------------------------------------
	public List<Ph2DevicesEntity> getSchedule()
		{
// * 処理済みハッシュリスト
		Map<Long, Ph2DevicesEntity> finishList = new LinkedHashMap<>();
		Map<Long, Ph2DevicesEntity> result = new LinkedHashMap<>();

// * 全てのデバイス情報を取得し、各デバイスに対して処理を行う
		List<Ph2DevicesEntity> devices = this.statusDomain.selectAll();

// * 現在の時刻を取得する
		for (Ph2DevicesEntity device : devices)
			{
// * 既に処理済みの場合、終了
			if (finishList.containsKey(device.getId())) continue;
// * リストに追加
			finishList.put(device.getId(), device);
			result.put(device.getId(), device);

// * 引継ぎ先デバイスがあれば、リストから削除する
			Long prevId = device.getPreviousDeviceId();
			while (null != prevId)
				{
				Ph2DevicesEntity curr = this.s0Initializer.getDeviceInfo(prevId);
				if (!finishList.containsKey(curr.getId()))
					{
					finishList.put(curr.getId(), device);
					}
				result.remove(curr.getId());
				prevId = curr.getPreviousDeviceId();
				}
			}
		return new ArrayList<>(result.values());
		}

	}
