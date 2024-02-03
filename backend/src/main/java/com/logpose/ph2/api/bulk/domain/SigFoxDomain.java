package com.logpose.ph2.api.bulk.domain;

import java.sql.Timestamp;
import java.util.ArrayList;
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
import com.logpose.ph2.api.dao.api.entity.SigFoxDeviceEntity;
import com.logpose.ph2.api.dao.api.entity.SigFoxDeviceListEntity;
import com.logpose.ph2.api.dao.api.entity.SigFoxMessagesEntity;
import com.logpose.ph2.api.dao.db.entity.MessagesEntity;
import com.logpose.ph2.api.dao.db.entity.MessagesEntityExample;
import com.logpose.ph2.api.dao.db.entity.Ph2DevicesEnyity;
import com.logpose.ph2.api.dao.db.entity.Ph2MessagesEntity;
import com.logpose.ph2.api.dao.db.entity.Ph2MessagesEntityExample;
import com.logpose.ph2.api.dao.db.mappers.MessagesMapper;
import com.logpose.ph2.api.dao.db.mappers.Ph2MessagesMapper;

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
	@Autowired
	private Ph2MessagesMapper ph2MessagesMapper;

	// ===============================================
	// 公開クラス群
	// ===============================================
	// --------------------------------------------------
	/**
	 * Sigfoxのデバイスリストを取得する
	 *
	 * @param api 使用するAPIオブジェクト
	 * @return List<String>
	 */
	// --------------------------------------------------
	public List<String> getDeviceList(SigFoxAPI api, String baseAuth)
		{
		LOG.info("SigFoxIDリストの取得処理開始");
// * 返却用オブジェクトの生成
		List<String> sigfoxIds = new ArrayList<>();
// * Sigfox IDリストの取得
		this.getDeviceList(api, baseAuth, sigfoxIds);
// * 取得したSigfox IDリストの返却
		return sigfoxIds;
		}

	// --------------------------------------------------
	/**
	 * Sigfoxのデバイスリストを取得する
	 *
	 * @return List<DataSummaryDTO>
	 */
	// --------------------------------------------------
	@Transactional(rollbackFor = Exception.class)
	public List<String> getDeviceList(SigFoxAPI api, String baseAuth, List<String> sigfoxIds)
		{
		String nextUrl;
		try
			{
			SigFoxDeviceListEntity data = api.getDeviceList(baseAuth);
			nextUrl = this.setSigfoxIdList(data, sigfoxIds);
			while (null != nextUrl)
				{
				Thread.sleep(1000);
				data = api.getDeviceList(nextUrl, baseAuth);
				nextUrl = this.setSigfoxIdList(data, sigfoxIds);
				}
			Thread.sleep(1000);
			}
		catch (RuntimeException e)
			{
			e.printStackTrace();
			}
		catch (InterruptedException e)
			{
			e.printStackTrace();
			}
		return sigfoxIds;
		}

	private String setSigfoxIdList(SigFoxDeviceListEntity data, List<String> sigfoxIds)
		{
		for (SigFoxDeviceEntity entity : data.getData())
			{
			sigfoxIds.add(entity.getId());
			}
		if (null == data.getPaging()) return null;
		if (null == data.getPaging().getNext()) return null;
		return data.getPaging().getNext();
		}

	@Transactional(rollbackFor = Exception.class)
	public void createPh2Messages(SigFoxAPI api, String baseAuth, String sigfoxId) throws Exception
		{
		try
			{
			Ph2MessagesEntityExample exm = new Ph2MessagesEntityExample();
			exm.createCriteria().andSigfoxIdEqualTo(sigfoxId);
			long count = this.ph2MessagesMapper.countByExample(exm);
			if (count > 0) return;
// * 問合せを実行する
			String nextUrl;
			LOG.info(sigfoxId + "データの取り込み処理開始");
			SigFoxMessagesEntity data = api.getMessages(baseAuth, sigfoxId, 0);
			nextUrl = this.insertPh2Messages(sigfoxId, data);
			while (null != nextUrl)
				{
				Thread.sleep(1000);
				data = api.getMessages(nextUrl, baseAuth);
				nextUrl = this.insertPh2Messages(sigfoxId, data);
				}
			LOG.info(sigfoxId + "データの取り込み処理終了");
			}
		catch (Exception e)
			{
			e.printStackTrace();
			throw e;
			}
		finally
			{
			try
				{
				Thread.sleep(1000);
				}
			catch (InterruptedException e)
				{
				e.printStackTrace();
				throw e;
				}
			}
		}

	private String insertPh2Messages(String sigfoxId, SigFoxMessagesEntity data)
		{
		long timezone = TimeZone.getTimeZone("JST").getRawOffset();

		for (SigFoxDataEntity item : data.getData())
			{
			if (null == item.getData()) continue;
			Ph2MessagesEntity entity = new Ph2MessagesEntity();
			Date utc = new Date();
			utc.setTime(item.getTime() - timezone);
			entity.setCastedAt(utc);
			entity.setRaw(item.getData());
			entity.setSeq(item.getSeqNumber());
			entity.setSigfoxId(sigfoxId);
			ph2MessagesMapper.insert(entity);
			}
		if (null == data.getPaging()) return null;
		if (null == data.getPaging().getNext()) return null;
		return data.getPaging().getNext();
		}

	@Transactional(rollbackFor = Exception.class)
	public void createMessages(Ph2DevicesEnyity device, SigFoxAPI api)
		{
// * sigfoxIdが無い場合は対象外
		String sigfoxId = device.getSigfoxDeviceId();
		if (null == sigfoxId) return;
		if (!sigfoxId.matches("^[A-Z0-9]{6}$"))
			{
			LOG.warn(sigfoxId + "は正しいsigfox IDではありません。処理はスキップされました。");
			return;
			}
// * メッセージテーブルからそのデバイスIDの最後の受信時間を得る
		Date lastTime = this.messagesMapper.selectLastCastedTime(device.getId());
// * 最後の受信時刻が無い場合、lastTimeを初期化する。
		if (null == lastTime)
			{
			lastTime = new Date();
			lastTime.setTime(0);
			}
// * timezoneから認証を設定する。
		String timeZone = device.getTz();
		String baseAuth = parameters.getBaseAuthSigFoxTK();
		if (timeZone.equals("Pacific/Auckland"))
			{
			baseAuth = parameters.getBaseAuthSigFoxNZ();
			}
// * 問合せを実行する
		String nextUrl;
		try
			{
			LOG.info(sigfoxId + "データの取り込み処理開始");
			SigFoxMessagesEntity data = api.getMessages(baseAuth, sigfoxId, lastTime.getTime());
			nextUrl = this.insertMessages(device.getId(), data);
			while (null != nextUrl)
				{
				Thread.sleep(1000);
				data = api.getMessages(nextUrl, baseAuth);
				nextUrl = this.insertMessages(device.getId(), data);
				}
			Thread.sleep(1000);
			}
		catch (RuntimeException e)
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
		long timezone = TimeZone.getTimeZone("JST").getRawOffset();
		for (SigFoxDataEntity item : data)
			{
			MessagesEntity entity = new MessagesEntity();
			Date utc = new Date();
			utc.setTime(item.getTime() - timezone);
			entity.setCastedAt(utc);
			entity.setCreatedAt(new Timestamp(System.currentTimeMillis()));
			entity.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
			entity.setDeviceId(deviceId);
			entity.setRaw(item.getData());
			entity.setSeq(item.getSeqNumber());
			MessagesEntityExample exe = new MessagesEntityExample();
			exe.createCriteria().andDeviceIdEqualTo(deviceId).andCastedAtEqualTo(utc);
			long pastRecords = this.messagesMapper.countByExample(exe);
			if (0 == pastRecords)
				{
				this.messagesMapper.insert(entity);
				}
			}
		if (null == message.getPaging()) return null;
		if (null == message.getPaging().getNext()) return null;
		return message.getPaging().getNext();
		}
	}
