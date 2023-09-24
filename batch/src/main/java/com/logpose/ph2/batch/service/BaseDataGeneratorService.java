package com.logpose.ph2.batch.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.logpose.ph2.batch.dao.db.entity.MessagesEnyity;
import com.logpose.ph2.batch.dao.db.entity.Ph2SystemStatusEntity;
import com.logpose.ph2.batch.dao.db.mappers.MessagesMapper;
import com.logpose.ph2.batch.domain.BaseDataDomain;
import com.logpose.ph2.batch.domain.BaseDataGenerator;
import com.logpose.ph2.batch.domain.BaseDataGeneratorModules;
import com.logpose.ph2.batch.domain.DataListModel;
import com.logpose.ph2.batch.domain.StatusDomain;

@Service
public class BaseDataGeneratorService
	{
	// ===============================================
	// クラスメンバー
	// ===============================================
	private static Logger LOG = LogManager.getLogger(BaseDataGeneratorService.class);
	@Autowired
	private StatusDomain statusDomain;
	@Autowired
	private MessagesMapper messagesMapper;
	@Autowired
	private BaseDataGeneratorModules modules;
	@Autowired
	private BaseDataDomain baseDataDomain;
	@Autowired
	private BaseDataGenerator baseDataGenerator;

	// ===============================================
	// 公開関数群
	// ===============================================
	@Transactional(rollbackFor = Exception.class)
	public boolean doService()
		{
		// * システムステータステーブル(ph2_system_status)からmessageレコードの
		// * 本処理での最終処理レコードの受信時刻を得る。
		Ph2SystemStatusEntity status = this.statusDomain.getSystemStatus("messages");
		Date takenDataTime = status.getLastTime();
		// * デバイス-データマップ
		Map<Long, DataListModel> deviceMap = new HashMap<>();
		// *Messageテーブル(messages)から受信時刻が上記の受信時間より後の
		// *レコードを未処理のレコードとして取得する。
		List<MessagesEnyity> messages = this.messagesMapper.selectByCastedAt(takenDataTime);
		LOG.info("以下の日付からログデータの取得" + takenDataTime.toString());
		if (10 >  messages.size())
			{
			return false;
			}
		// *取得したレコードリストから以下のデバイス-データマップを作成する。
		this.modules.createDeviceDataMap(messages, deviceMap);

		// * 基礎データテーブルの受信時刻移行のデータ削除
		this.baseDataDomain.delete(status.getLastTime());
		this.baseDataGenerator.deleteDashboard(status.getLastTime());

		for (MessagesEnyity message : messages)
			{
			// * デバイス-データマップからdevice_idと一致するデータリストを取得する。
			DataListModel messageData = deviceMap.get(message.getDeviceId());
			// * レコード内のrawデータを３文字づつ抽出し、文字リストを作成する。
			List<String> data = splitByLength(message.getRaw(), 3);
			// * データリストの設定を行う
			messageData = this.modules.setData(message.getCastedAt(), data, messageData);
			// * データリストそのものが更新される場合があるので、デバイス-データマップを更新
			deviceMap.put(message.getDeviceId(), messageData);

			// * データリストのカウントが16に達した時、BAT-BDC-02基礎データ作成処理#計算処理
			// * とDBへの登録 を実行する。
			if (messageData.getCount() == 16)
				{
				Date lasttime = this.baseDataGenerator.generate(message.getDeviceId(), messageData);
				// 取得レコードのcasted_atを最後の受信時刻に設定する。
				status.setLastTime(lasttime);
				}
			}

		// * データに問題がある場合
		if (status.getLastTime() == null)
			{
			status.setLastTime(messages.get(messages.size() - 1).getCastedAt());
			}
		else if (takenDataTime.getTime() == status.getLastTime().getTime())
			{
			status.setLastTime(messages.get(messages.size() - 1).getCastedAt());
			}
		else
			{
			Collection<DataListModel> dataList = deviceMap.values();
			for (DataListModel model : dataList)
				{
				if ((model.getCount() < 16) && (model.isStatus()))
					{
					if (status.getLastTime().getTime() > model.getCastedAt().getTime())
						{
						// * ３０秒以内なら途中かも
						long diff = status.getLastTime().getTime() - model.getCastedAt().getTime();
						if (diff < 30000)
							{
							status.setLastTime(model.getCastedAt());
							}
						}
					}
				}
			}

		this.statusDomain.update(status);
		return true;
		}

	// ===============================================
	// プライベート関数群
	// ===============================================
	public List<String> splitByLength(String str, int length)
		{
		List<String> strs = new ArrayList<>();
		for (int i = 0; i < StringUtils.length(str); i += length)
			{
			strs.add(StringUtils.substring(str, i, i + length));
			}
		return strs;
		}
	}
