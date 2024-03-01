package com.logpose.ph2.api.bulk.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.cursor.Cursor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.logpose.ph2.api.algorythm.DeviceDayAlgorithm;
import com.logpose.ph2.api.bulk.domain.BaseDataGenerator;
import com.logpose.ph2.api.bulk.domain.BaseDataGeneratorModules;
import com.logpose.ph2.api.bulk.domain.DataListModel;
import com.logpose.ph2.api.bulk.vo.LoadCoordinator;
import com.logpose.ph2.api.dao.db.cache.MinutesCacher;
import com.logpose.ph2.api.dao.db.entity.Ph2DevicesEntity;
import com.logpose.ph2.api.dao.db.entity.Ph2MessagesEntity;
import com.logpose.ph2.api.dao.db.mappers.Ph2BaseDataMapper;
import com.logpose.ph2.api.dao.db.mappers.Ph2InsolationDataMapper;
import com.logpose.ph2.api.dao.db.mappers.Ph2MessagesMapper;
import com.logpose.ph2.api.dao.db.mappers.Ph2RawDataMapper;
import com.logpose.ph2.api.dao.db.mappers.Ph2RelBaseDataMapper;
import com.logpose.ph2.api.dto.SensorDataDTO;

/**
 * Sigfoxのメッセージテーブルから値を抽出し、全チャンネルに達したら、各値を計算し、各種DBに登録する。
 */
@Service
public class S3RawDataLoaderService
	{
	// ===============================================
	// クラスメンバー
	// ===============================================
	@Autowired
	private Ph2MessagesMapper Ph2messagesMapper;
	@Autowired
	private DeviceDayAlgorithm deviceDayAlgorithm;
	@Autowired
	private BaseDataGeneratorModules modules;
	@Autowired
	private BaseDataGenerator baseDataGenerator;
	@Autowired
	private Ph2RelBaseDataMapper ph2RelBaseDataMapper;
	@Autowired
	private Ph2RawDataMapper rawDataMapper;
	@Autowired
	private Ph2BaseDataMapper ph2BaseDataMapper;
	@Autowired
	private Ph2InsolationDataMapper ph2InsolationDataMapper;

	// ===============================================
	// 公開関数群
	// ===============================================
	// --------------------------------------------------
	/**
	 * SigFoxデータをデバイス単位に加工して各種テーブルに格納する。
	 * @param LoadCoordinator
	 */
	// --------------------------------------------------
	@Transactional(rollbackFor = Exception.class)
	public Date loadMessages(LoadCoordinator coordinator) throws IOException
		{
// * DBへの一括登録高速化のためのアクセスキャッシュを生成する
// * START --------------------------------------
// * RelBaseDataの最大IDを取得し、レコードを追加するときのID付与の準備をする
		while (!MinutesCacher.lock())
			{
			try
				{
				Thread.sleep(1000);
				}
			catch (Exception e)
				{
				}
			}
		try
			{
			Long id = this.ph2RelBaseDataMapper.selectMaxId();
			if (null == id) id = Long.valueOf(1);
// * アクセスキャッシュの生成
			MinutesCacher cache = new MinutesCacher(id, ph2RelBaseDataMapper,
					ph2BaseDataMapper, rawDataMapper, ph2InsolationDataMapper, null);
// * END --------------------------------------
// * 指定デバイスから指定タイムゾーンでの指定時刻からのメッセージテーブルのデータを取得する。
			Ph2DevicesEntity device = coordinator.getDevice();
			// * メッセージデータの抽出開始日
			Date op_start_date = device.getOpStart();
			// * もしメッセージデータの抽出開始日が無いか指定抽出開始日より古い場合は、抽出開始日を優先する。
			Date firstDate = coordinator.getLastHadledDate();
			if (null != firstDate)
				{
				firstDate = deviceDayAlgorithm.addMilliscond(firstDate);
				if (null != op_start_date)
					{
					if (op_start_date.getTime() < firstDate.getTime())
						{
						op_start_date = firstDate;
						}
					}
				else
					{
					op_start_date = firstDate;
					}
				}
// * メッセージデータの抽出終了日
			Date op_end_date = device.getOpEnd();
// * メッセージデータを5000件ごとに抽出して、各種テーブルデータの作成とロードを行う
			try (Cursor<Ph2MessagesEntity> messageCorsor = this.Ph2messagesMapper
					.selectByCastedAt(device.getSigfoxDeviceId(), device.getTz(), op_start_date, op_end_date))
				{
				Iterator<Ph2MessagesEntity> messages = messageCorsor.iterator();
				DataListModel messageData = new DataListModel();
				while (messages.hasNext())
					{
					Ph2MessagesEntity message = messages.next();
					messageData = this.createTables(device, coordinator.getSensors(), messageData,
							message, cache);
					}
// * ローディング情報に最後の生データ登録時刻を返却する。
				cache.flush();
				return cache.getLastCastedDate();
				}
			}
		finally
			{
			MinutesCacher.unlock();
			}
		}

	// --------------------------------------------------
	/**
	 * メッセージから値を抽出し、全チャンネルに達したら、各値を計算し、各種DBに登録する。
	 * @param device
	 * @param sensors
	 * @param dataListModel
	 * @param message
	 * @param cache
	 * @return DataListModel
	 */
	// --------------------------------------------------
	@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRES_NEW)
	public DataListModel createTables(
			Ph2DevicesEntity device,
			List<SensorDataDTO> sensors,
			DataListModel dataListModel,
			Ph2MessagesEntity message,
			MinutesCacher cache)
		{
// * レコード内のrawデータを３文字づつ抽出し、文字リストを作成する。
		List<String> data = splitByLength(message.getRaw(), 3);
// * データリストの設定を行う
		dataListModel = this.modules.setData(message.getCastedAt(), data, dataListModel);
// * データリストのカウントが16に達した時、計算処理とDBへの登録 を実行する。
		if (dataListModel.getCount() == 16)
			{
			this.baseDataGenerator.generate(device.getId(), sensors, dataListModel, cache);
			}
		return dataListModel;
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
