package com.logpose.ph2.api.dao.db.cache;

import java.util.ArrayList;
import java.util.List;

import com.logpose.ph2.api.dao.db.entity.Ph2MessagesEntity;
import com.logpose.ph2.api.dao.db.entity.Ph2MessagesEntityExample;
import com.logpose.ph2.api.dao.db.mappers.Ph2MessagesMapper;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class MessagesCacher
	{
	// ===============================================
	// クラスメンバー
	// ===============================================
	private Ph2MessagesMapper ph2MessagesMapper;
	private final List<Ph2MessagesEntity> messagesData = new ArrayList<>();
	// ===============================================
	// 公開関数群
	// ===============================================
	// -----------------------------------------------------------------
	/**
	 * メッセージテーブルへの登録
	 * 
	 * @param Ph2MessagesEntity 登録データ
	 */
	// -----------------------------------------------------------------
	public void addMessage(Ph2MessagesEntity data)
		{
// * データが既存テーブルに無いことを確認する
		Ph2MessagesEntityExample exe = new Ph2MessagesEntityExample();
		exe.createCriteria().andSigfoxIdEqualTo(data.getSigfoxId()).andCastedAtEqualTo(data.getCastedAt());
		long pastRecords = this.ph2MessagesMapper.countByExample(exe);
		if (0 != pastRecords) return;
// * メッセージデータを一時的に保存する
		this.messagesData.add(data);
// * 1000件に到達したら、メッセージテーブルに登録する
		if (this.messagesData.size() == 1000)
			{
			this.ph2MessagesMapper.multiRowInsert(this.messagesData);
			this.messagesData.clear();
			}
		}
	// -----------------------------------------------------------------
	/**
	 * まだ、キャッシュ中のデータをテーブルに登録する 
	 */
	// -----------------------------------------------------------------
	public void flush()
		{
		if(this.messagesData.size() >0 ) 
			{
			this.ph2MessagesMapper.multiRowInsert(this.messagesData);
			this.messagesData.clear();
			}
		}
	}
