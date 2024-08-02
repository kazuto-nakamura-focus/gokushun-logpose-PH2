package com.logpose.ph2.api.service.data_load;

import java.io.IOException;
import java.text.ParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.logpose.ph2.api.bulk.domain.DeviceStatusDomain;
import com.logpose.ph2.api.dao.db.entity.Ph2DevicesEntity;

/**
 * データロードを実行するサービス
 */
@Service
public class DataUpdateService
	{
	// ===============================================
	// クラスメンバー
	// ===============================================
	private static Logger LOG = LogManager.getLogger(DataLoadService.class);
	@Autowired
	private DeviceStatusDomain statusDomain;
	@Autowired
	private DataLoadService dataLoadService;

	// ===============================================
	// 公開関数群
	// ===============================================
	// --------------------------------------------------
	/**
	 * 全てのデバイスのデータを最新の状態に更新する
	 * @throws IOException
	 * @throws ParseException
	 */
	// --------------------------------------------------
	public void updateData() throws IOException, ParseException
		{
// * 処理済みハッシュリスト
		Map<Long, Ph2DevicesEntity> finishList = new HashMap<>();
// * 全てのデバイス情報を取得し、各デバイスに対して処理を行う
		List<Ph2DevicesEntity> devices = this.statusDomain.selectAll();
		for (Ph2DevicesEntity device : devices)
			{
			try
				{
// * 既に処理済みの場合、終了
				if (finishList.containsKey(device.getId())) continue;
// * 全ロードモードかどうか
				boolean isAll = this.statusDomain.isAll(device);
// * データロードを実行する
				dataLoadService.loadDevices(isAll, device, finishList);
				}
			catch (Exception e)
				{
				LOG.error(device.getId() + "のロードに失敗しました。");
				e.printStackTrace();
				}
			}
		}

	
	}
