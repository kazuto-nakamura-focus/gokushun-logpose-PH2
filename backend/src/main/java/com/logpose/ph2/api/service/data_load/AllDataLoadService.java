package com.logpose.ph2.api.service.data_load;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.logpose.ph2.api.bulk.domain.DeviceStatusDomain;
import com.logpose.ph2.api.bulk.service.S0Initializer;
import com.logpose.ph2.api.dao.db.entity.Ph2DevicesEntity;

import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

/**
 * データロードを実行するサービス
 */
@Service
public class AllDataLoadService
	{
	// ===============================================
	// クラスメンバー
	// ===============================================
	private static Logger LOG = LogManager.getLogger(AllDataLoadService.class);
	@Autowired
	private DeviceStatusDomain statusDomain;
	@Autowired
	private S0Initializer s0Initializer;
	@Autowired
	private DataLoadService dataLoadService;

	// ===============================================
	// 公開関数群
	// ===============================================
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

	// --------------------------------------------------
	/**
	 * 全てのデバイスのメッセージテーブルからまだロードされていないデータ
	 * を各テーブルに加工して取り込む
	 * @throws IOException
	 * @throws ParseException
	 */
	// --------------------------------------------------
	public Mono<Void> createAllData()
		{
		return Mono.fromRunnable(() ->
			{
			// * 処理済みハッシュリスト
			Map<Long, Ph2DevicesEntity> finishList = new HashMap<>();
			// * 全てのデバイス情報を取得し、各デバイスに対して処理を行う
			List<Ph2DevicesEntity> devices = this.statusDomain.selectAll();
			// * 現在の時刻を取得する
			for (Ph2DevicesEntity device : devices)
				{
				try
					{
					// * 既に処理済みの場合、終了
					if (finishList.containsKey(device.getId())) continue;
					// * コーディネーターを引数にデータロードを実行する
					dataLoadService.loadDevices(true, device, finishList);
					}
				catch (Exception e)
					{
					LOG.error(device.getId() + "のロードに失敗しました。");
					e.printStackTrace();
					}
				}
			});
		}

	// --------------------------------------------------
	/**
	 * 指定されたデバイスIDに対してデータローディング処理を行う
	 * @param deviceId
	 * @throws Exception 
	 */
	// --------------------------------------------------
	public Mono<Object> createData(Long deviceId)
		{
		return Mono.defer(() ->
			{
			// * 処理済みハッシュリスト
			Map<Long, Ph2DevicesEntity> finishList = new HashMap<>();
			Ph2DevicesEntity device = this.s0Initializer.getDeviceInfo(deviceId);
			try
				{
				// * コーディネーターを引数にデータロードを実行する
				dataLoadService.loadDevices(true, device, finishList);
				}
			catch (Exception e)
				{
				LOG.error(device.getId() + "のロードに失敗しました。", e);
				e.printStackTrace();
				}
			return Mono.empty();
			}).subscribeOn(Schedulers.boundedElastic()); // 非同期で実行されるようにスケジューラを指定
		}
	}
