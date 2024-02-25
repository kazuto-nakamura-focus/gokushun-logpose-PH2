package com.logpose.ph2.api.bulk.service;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.logpose.ph2.api.algorythm.DeviceDayAlgorithm;
import com.logpose.ph2.api.dao.db.cache.DailyBaseCacher;
import com.logpose.ph2.api.dao.db.entity.Ph2DailyBaseDataEntity;
import com.logpose.ph2.api.dao.db.entity.Ph2DeviceDayEntity;
import com.logpose.ph2.api.dao.db.entity.Ph2WeatherDailyMasterEntity;
import com.logpose.ph2.api.dao.db.entity.Ph2WeatherDailyMasterEntityExample;
import com.logpose.ph2.api.dao.db.mappers.Ph2DailyBaseDataMapper;
import com.logpose.ph2.api.dao.db.mappers.Ph2DeviceDayMapper;
import com.logpose.ph2.api.dao.db.mappers.Ph2WeatherDailyMasterMapper;

/**
 * 欠損データに対して過去の天気データを取り込んで補完する機能を持つ
 * @since 2024/02/20
 * @version 1.0
 * @author Fumiaki Takahashi
 */
@Service
public class S7WeatherHistoryImport
	{
	// ===============================================
	// クラスメンバー
	// ===============================================
	@Autowired
	private DeviceDayAlgorithm deviceDayAlgorithm;
	@Autowired
	private Ph2DeviceDayMapper ph2DeviceDayMapper;
	@Autowired
	private Ph2WeatherDailyMasterMapper ph2WeatherDailyMasterMapper;
	@Autowired
	private Ph2DailyBaseDataMapper ph2DailyBaseDataMapper;

	// ===============================================
	// 公開関数群
	// ===============================================
	// --------------------------------------------------
	/**
	 * データを取得する期間を設定する。
	 * @param deviceDays 処理対象となるデバイスディリスト
	 */
	// --------------------------------------------------
	@Transactional(rollbackFor = Exception.class)
	public void importFromMaster(List<Ph2DeviceDayEntity> deviceDays)
		{
// キャッシュの作成
		DailyBaseCacher cache = new DailyBaseCacher(this.ph2DailyBaseDataMapper);
		
// * 本日の日付設定
		Calendar cal = Calendar.getInstance();
		this.deviceDayAlgorithm.getTimeZero(cal);
		Date today = cal.getTime();
		
// * 以下、各デバイスからデータが無い期間のリストを取得し、マスターからデータのインポートを行う
		for(Ph2DeviceDayEntity deviceDay : deviceDays)
			{
// * 対象データの最終日は最大で本日までとする
			if(deviceDay.getDate().getTime() == today.getTime()) break;
			
// * データの無い場合
			if(false == deviceDay.getHasReal())
				{
				// マスターへの検索条件の作成
				Ph2WeatherDailyMasterEntityExample exm = new Ph2WeatherDailyMasterEntityExample();
				exm.createCriteria().andDeviceIdEqualTo(deviceDay.getDeviceId())//
					.andCastedAtEqualTo(deviceDay.getDate());
				
				// マスターDBへの問い合わせ
				List<Ph2WeatherDailyMasterEntity> tmp = this.ph2WeatherDailyMasterMapper.selectByExample(exm);
				if(0 == tmp.size()) continue;
				Ph2WeatherDailyMasterEntity weather = tmp.get(0);
				
				// マスターから日別テーブルへのデータの追加
				Ph2DailyBaseDataEntity entity = new Ph2DailyBaseDataEntity();
				entity.setDayId(deviceDay.getId());
				entity.setRawCdd(weather.getRawCdd());
				entity.setPar(weather.getPar());
				entity.setSunTime(weather.getSunTime());
				entity.setTm(weather.getTm());

// * キャッシュへの登録
				cache.addDailyBaseData(entity);
				
// * デバイスディの更新
				deviceDay.setHasReal(true);
				this.ph2DeviceDayMapper.updateByPrimaryKey(deviceDay);
				}
			}
		cache.flush(); // DBへの書き込み
		}
	}
