package com.logpose.ph2.api.bulk.service;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.logpose.ph2.api.bulk.domain.ModelDataDomain;
import com.logpose.ph2.api.dao.db.entity.Ph2DeviceDayEntity;

@Service
public class S6ModelDataApplyrService
	{
	// ===============================================
	// クラスメンバー
	// ===============================================
	private static Logger LOG = LogManager
			.getLogger(S6ModelDataApplyrService.class);
	@Autowired
	private ModelDataDomain modelDataDomain;

	// ===============================================
	// 公開関数群
	// ===============================================
	// --------------------------------------------------
	/**
	 * 各モデルデータ生成のモジュールを呼び出す
	 * @param deviceId 処理対象となるデバイスID
	 * @param deviceDays 年度初日のデータで実測値があるもの
	 * @throws ParseException
	 */
	// --------------------------------------------------
	public void doService(Long deviceId, List<Ph2DeviceDayEntity> deviceDays) throws ParseException
		{
		LOG.info("モデルデータの作成を開始します。", deviceId);
		// * 年度の開始日まで移動
		List<Ph2DeviceDayEntity> tmp = new ArrayList<>();
		for (Ph2DeviceDayEntity entity : deviceDays)
			{
			this.modelDataDomain.doService(deviceId, entity.getYear(), entity.getDate());
			}
		LOG.info("モデルデータの作成が終了しました。", deviceId);
		}
	}
