package com.logpose.ph2.api.bulk.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.cursor.Cursor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.logpose.ph2.api.bulk.domain.BaseDataGenerator;
import com.logpose.ph2.api.bulk.domain.BaseDataGeneratorModules;
import com.logpose.ph2.api.bulk.domain.DataListModel;
import com.logpose.ph2.api.bulk.vo.LoadCoordinator;
import com.logpose.ph2.api.dao.db.cache.MinutesCacher;
import com.logpose.ph2.api.dao.db.entity.MessagesEntity;
import com.logpose.ph2.api.dao.db.entity.Ph2DashBoardEntityExample;
import com.logpose.ph2.api.dao.db.entity.Ph2DevicesEnyity;
import com.logpose.ph2.api.dao.db.entity.Ph2RelBaseDataEntityExample;
import com.logpose.ph2.api.dao.db.mappers.MessagesMapper;
import com.logpose.ph2.api.dao.db.mappers.Ph2BaseDataMapper;
import com.logpose.ph2.api.dao.db.mappers.Ph2DashBoardMapper;
import com.logpose.ph2.api.dao.db.mappers.Ph2InsolationDataMapper;
import com.logpose.ph2.api.dao.db.mappers.Ph2RelBaseDataMapper;
import com.logpose.ph2.api.dao.db.mappers.joined.Ph2JoinedMapper;
import com.logpose.ph2.api.dto.SensorDataDTO;

@Service
public class S2DeviceDataLoaderService
	{
	@Autowired
	private MessagesMapper messagesMapper;
	@Autowired
	private BaseDataGeneratorModules modules;
	@Autowired
	private BaseDataGenerator baseDataGenerator;
	@Autowired
	private Ph2RelBaseDataMapper ph2RelBaseDataMapper;
	@Autowired
	private Ph2DashBoardMapper dashboardMapper;
	@Autowired
	private Ph2BaseDataMapper ph2BaseDataMapper;
	@Autowired
	private Ph2InsolationDataMapper ph2InsolationDataMapper;
	@Autowired
	private Ph2JoinedMapper ph2JoinedMapper;

	// --------------------------------------------------
	/**
	 * 
	 */
	// --------------------------------------------------
	@Transactional(rollbackFor = Exception.class)
	@CacheEvict(value="getFieldData",  allEntries = true)
	public void loadMessages(LoadCoordinator coordinator) throws IOException
		{
		Date firstDate = coordinator.getLastHadledDate();
		Date lastDate = null;
// * 指定された日付より後のレコードをクリアする
		this.deleteTables(coordinator.getDeviceId(), coordinator.getLastHadledDate());
// * DBへの一括登録高速化のためのアクセスキャッシュを生成する
// * START --------------------------------------
// * RelBaseDataの最大IDを取得し、レコードを追加するときのID付与の準備をする
		Long id = this.ph2RelBaseDataMapper.selectMaxId();
		if (null == id) id = Long.valueOf(1);
// * アクセスキャッシュの生成
		MinutesCacher cache = new MinutesCacher(id, ph2RelBaseDataMapper,
				ph2BaseDataMapper, dashboardMapper, ph2InsolationDataMapper);
// * END --------------------------------------
// * 指定デバイスから指定タイムゾーンでの指定時刻からのメッセージテーブルのデータを取得する。
		Ph2DevicesEnyity device = coordinator.getDevice();
		try (Cursor<MessagesEntity> messageCorsor = this.messagesMapper
				.selectByCastedAt(device.getId(), device.getTz(), firstDate))
			{
			Iterator<MessagesEntity> messages = messageCorsor.iterator();
			DataListModel messageData = new DataListModel();
			while (messages.hasNext())
				{
				MessagesEntity message = messages.next();
				lastDate = message.getCastedAt();
				messageData = this.createTables(device, coordinator.getSensors(), messageData,
						message, cache);
				}
			}
		catch (Exception e)
			{
			throw e;
			}
		cache.flush();
		}

	@Transactional(propagation = Propagation.REQUIRES_NEW)
	private DataListModel createTables(
			Ph2DevicesEnyity device,
			List<SensorDataDTO> records,
			DataListModel dataListModel,
			MessagesEntity message,
			MinutesCacher cache)
		{
// * レコード内のrawデータを３文字づつ抽出し、文字リストを作成する。
		List<String> data = splitByLength(message.getRaw(), 3);
// * データリストの設定を行う
		dataListModel = this.modules.setData(message.getCastedAt(), data, dataListModel);
// * データリストのカウントが16に達した時、BAT-BDC-02基礎データ作成処理#計算処理
// * とDBへの登録 を実行する。
		if (dataListModel.getCount() == 16)
			{
			this.baseDataGenerator.generate(message.getDeviceId(), records, dataListModel, cache);
			}
		return dataListModel;
		}

	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void deleteTables(long deviceId, Date date)
		{
		Ph2RelBaseDataEntityExample relexm = new Ph2RelBaseDataEntityExample();
		com.logpose.ph2.api.dao.db.entity.Ph2RelBaseDataEntityExample.Criteria criteria = relexm
				.createCriteria().andDeviceIdEqualTo(deviceId);
		if (null != date) criteria.andCastedAtGreaterThan(date);
		this.ph2RelBaseDataMapper.deleteByExample(relexm);
		// * 過去のダッシュボードの受信時刻移行のデータ削除
		Ph2DashBoardEntityExample exm = new Ph2DashBoardEntityExample();
		com.logpose.ph2.api.dao.db.entity.Ph2DashBoardEntityExample.Criteria criteria2 = exm
				.createCriteria().andDeviceIdEqualTo(deviceId);
		if (null != date) criteria2.andCastedAtGreaterThan(date);
		this.dashboardMapper.deleteByExample(exm);
		}

	// ===============================================
	// プライベート関数群
	// ===============================================
	private List<String> splitByLength(String str, int length)
		{
		List<String> strs = new ArrayList<>();
		for (int i = 0; i < StringUtils.length(str); i += length)
			{
			strs.add(StringUtils.substring(str, i, i + length));
			}
		return strs;
		}

	}
