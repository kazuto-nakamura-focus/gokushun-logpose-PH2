package com.logpose.ph2.api.bulk.domain;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.logpose.ph1.api.dao.db.entity.MessagesEnyity;
import com.logpose.ph1.api.dao.db.entity.MessagesEnyityExample;
import com.logpose.ph1.api.dao.db.mappers.MessagesMapper;
import com.logpose.ph2.api.configration.DefaultSigFoxParameters;
import com.logpose.ph2.api.dao.api.SigFoxAPI;
import com.logpose.ph2.api.dao.api.entity.SigFoxDataEntity;
import com.logpose.ph2.api.dao.api.entity.SigFoxMessagesEntity;
import com.logpose.ph2.api.dao.db.entity.Ph2DevicesEnyity;

@Component
public class SigFoxDomain
	{
	@Autowired
	private DefaultSigFoxParameters parameters;
	@Autowired
	private MessagesMapper messagesMapper;

	@Transactional(rollbackFor = Exception.class)
	public void createMessages(Ph2DevicesEnyity device, SigFoxAPI api)
		{
// * sigfoxIdが無い場合は対象外
		String sigfoxId = device.getSigfoxDeviceId();
		if(null == sigfoxId) return;
// * メッセージテーブルからそのデバイスIDの最後の受信時間を得る
		Date lastTime = this.messagesMapper.selectLastCastedTime(device.getId());
// * 最後の受信時刻が無い場合、lastTimeを初期化する。
		lastTime.setTime(0);
// * timezoneから認証を設定する。
		String timeZone = device.getTz();
		String baseAuth = parameters.getBaseAuthTK();
		if(timeZone.equals("Pacific/Auckland"))
			{
			baseAuth = parameters.getBaseAuthNZ();
			}
// * 問合せを実行する
		String nextUrl;
		SigFoxMessagesEntity data = api.getMessages(baseAuth, sigfoxId, lastTime.getTime());
		nextUrl = this.insertMessages(device.getId(), data);
		while(null != nextUrl)
			{
			data = api.getMessages(nextUrl, baseAuth);
			nextUrl = this.insertMessages(device.getId(), data);
			}
		}
	@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRES_NEW)
	public String insertMessages(Long deviceId, SigFoxMessagesEntity message) 
		{
		List<SigFoxDataEntity> data = message.getData();
		for(SigFoxDataEntity item : data)
			{
			MessagesEnyity entity = new MessagesEnyity();
			Date castedAt = new Date();
			castedAt.setTime(item.getTime());
			entity.setCastedAt(castedAt);
			entity.setCreatedAt(new Timestamp(System.currentTimeMillis()));
			entity.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
			entity.setDeviceId(deviceId);
			entity.setRaw(item.getData());
			entity.setSeq(item.getSeqNumber());
			MessagesEnyityExample exe = new MessagesEnyityExample();
			exe.createCriteria().andDeviceIdEqualTo(deviceId).andCastedAtEqualTo(castedAt);
			long pastRecords = this.messagesMapper.countByExample(exe);
			if( 0 == pastRecords)
				{
				this.messagesMapper.insert(entity);
				}
			}
		if( null == message.getPaging()) return null;
		if( null == message.getPaging().getNext()) return null;
		return message.getPaging().getNext();
		}
	}
