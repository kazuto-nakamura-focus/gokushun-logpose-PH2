package com.logpose.ph2.api.bulk.domain;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.logpose.ph2.api.configration.DefaultSecurityParameters;
import com.logpose.ph2.api.dao.api.SigFoxAPI;
import com.logpose.ph2.api.dao.api.entity.SigFoxDataEntity;
import com.logpose.ph2.api.dao.api.entity.SigFoxMessagesEntity;
import com.logpose.ph2.api.dao.db.entity.MessagesEntity;
import com.logpose.ph2.api.dao.db.entity.MessagesEntityExample;
import com.logpose.ph2.api.dao.db.entity.Ph2DevicesEnyity;
import com.logpose.ph2.api.dao.db.mappers.MessagesMapper;

@Component
public class SigFoxDomain
	{
	// ===============================================
	// クラスメンバー
	// ===============================================
	private static Logger LOG = LogManager.getLogger(SigFoxDomain.class);

	@Autowired
	private DefaultSecurityParameters parameters;
	@Autowired
	private MessagesMapper messagesMapper;

	// ===============================================
	// 公開クラス群
	// ===============================================
	@Transactional(rollbackFor = Exception.class)
	public void createMessages(Ph2DevicesEnyity device, SigFoxAPI api)
		{
// * sigfoxIdが無い場合は対象外
		String sigfoxId = device.getSigfoxDeviceId();
		if(null == sigfoxId) return;
		if(!sigfoxId.matches("^[A-Z0-9]{6}$"))
			{
			LOG.warn(sigfoxId+"は正しいsigfox IDではありません。処理はスキップされました。");
			return;
			}
// * メッセージテーブルからそのデバイスIDの最後の受信時間を得る
		Date lastTime = this.messagesMapper.selectLastCastedTime(device.getId());
// * 最後の受信時刻が無い場合、lastTimeを初期化する。
		if(null == lastTime)
			{
			lastTime = new Date();
			lastTime.setTime(0);
			}
// * timezoneから認証を設定する。
		String timeZone = device.getTz();
		String baseAuth = parameters.getBaseAuthSigFoxTK();
		if(timeZone.equals("Pacific/Auckland"))
			{
			baseAuth = parameters.getBaseAuthSigFoxNZ();
			}
// * 問合せを実行する
		String nextUrl;
		try {
		LOG.info(sigfoxId+"データの取り込み処理開始");
		SigFoxMessagesEntity data = api.getMessages(baseAuth, sigfoxId, lastTime.getTime());
		nextUrl = this.insertMessages(device.getId(), data);
		while(null != nextUrl)
			{
			Thread.sleep(1000);
			data = api.getMessages(nextUrl, baseAuth);
			nextUrl = this.insertMessages(device.getId(), data);
			}
		Thread.sleep(1000);
		}
		catch(RuntimeException e)
			{
			e.printStackTrace();
			}
		catch (InterruptedException e)
			{
			e.printStackTrace();
			}
		}
	@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRES_NEW)
	public String insertMessages(Long deviceId, SigFoxMessagesEntity message) 
		{
		List<SigFoxDataEntity> data = message.getData();
		long timezone = TimeZone.getTimeZone( "JST" ).getRawOffset();
		for(SigFoxDataEntity item : data)
			{
			MessagesEntity entity = new MessagesEntity();
			Date utc = new Date();
			utc.setTime(item.getTime()-timezone);
			entity.setCastedAt(utc);
			entity.setCreatedAt(new Timestamp(System.currentTimeMillis()));
			entity.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
			entity.setDeviceId(deviceId);
			entity.setRaw(item.getData());
			entity.setSeq(item.getSeqNumber());
			MessagesEntityExample exe = new MessagesEntityExample();
			exe.createCriteria().andDeviceIdEqualTo(deviceId).andCastedAtEqualTo(utc);
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
