package com.logpose.ph2.api.bulk.domain;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.logpose.ph2.api.algorythm.DeviceDayAlgorithm;
import com.logpose.ph2.api.dao.api.SigFoxAPI;
import com.logpose.ph2.api.dao.api.entity.SigFoxDataEntity;
import com.logpose.ph2.api.dao.api.entity.SigFoxDeviceEntity;
import com.logpose.ph2.api.dao.api.entity.SigFoxDeviceListEntity;
import com.logpose.ph2.api.dao.api.entity.SigFoxMessagesEntity;
import com.logpose.ph2.api.dao.db.cache.MessagesCacher;
import com.logpose.ph2.api.dao.db.entity.Ph2MessagesEntity;
import com.logpose.ph2.api.dao.db.mappers.Ph2MessagesMapper;

@Component
public class SigFoxDomain
	{
	// ===============================================
	// クラスメンバー
	// ===============================================
	private static Logger LOG = LogManager.getLogger(SigFoxDomain.class);
	@Autowired
	private DeviceLogDomain deviceLogDomain;
	@Autowired
	private DeviceDayAlgorithm deviceDayAlgorithm;
	@Autowired
	private Ph2MessagesMapper ph2MessagesMapper;

	// ===============================================
	// 公開クラス群
	// ===============================================
	// --------------------------------------------------
	/**
	 * Sigfoxのデバイスリストを取得する
	 *
	 * @param api SigFoxへのAPIオブジェクト
	 * @return List<String>
	 * @throws Exception 
	 */
	// --------------------------------------------------
	public List<String> getDeviceList(Long deviceId, SigFoxAPI api) throws Exception
		{
		LOG.info("SigFoxIDリストの取得処理開始");
// * 返却用オブジェクトの生成
		List<String> sigfoxIds = new ArrayList<>();
// * Sigfox IDリストの取得
		this.getDeviceList(api, sigfoxIds);
// * 取得したSigfox IDリストの返却
		return sigfoxIds;
		}

	// ----------------------------------------s----------
	/**
	 * Sigfoxのデバイスリストを取得する
	 *	@param api SigFoxへのAPIオブジェクト
	 * @param sigfoxIds
	 * @return List<String>
	 * 	@throws Exception
	 */
	// --------------------------------------------------
	@Transactional(rollbackFor = Exception.class)
	public List<String> getDeviceList(SigFoxAPI api, List<String> sigfoxIds) throws Exception
		{
		String nextUrl;

		SigFoxDeviceListEntity data = api.getDeviceList();
		nextUrl = this.setSigfoxIdList(data, sigfoxIds);
		while (null != nextUrl)
			{
			Thread.sleep(1000);
			data = api.getDeviceList(nextUrl);
			nextUrl = this.setSigfoxIdList(data, sigfoxIds);
			}
		Thread.sleep(1000);
		return sigfoxIds;
		}

	// ----------------------------------------s----------
	/**
	 * SigfoxのデバイスデータからSigfoxのIDを抽出する
	 * @param data
	 * @param sigfoxIds
	 * @return String 次の問合せ先
	 */
	// --------------------------------------------------
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
	// --------------------------------------------------
	/**
	 * Sigfoxのセンサーデータをメッセージテーブルに代入する
	 * @param device
	 * @param api
	 * @throws InterruptedException 
	 */
	// --------------------------------------------------
	@Transactional(rollbackFor = Exception.class)
	public void createMessages(Long deviceId, String sigfoxId, SigFoxAPI api) throws InterruptedException
		{
		if (null == sigfoxId) 
			{
			this.deviceLogDomain.log(LOG, deviceId, getClass(), "Sigfox IDが定義されていません。処理はスキップされました。");
			return;
			}
// * sigfoxIdが正しいフォーマットか確認
		if (!sigfoxId.matches("^[A-Z0-9]{6}$"))
			{
			this.deviceLogDomain.log(LOG, deviceId, getClass(), sigfoxId + "は正しいsigfox IDではありません。処理はスキップされました。");
			return;
			}
// * メッセージテーブルからそのデバイスIDの最後の受信時間を得る
		Date lastTime = this.ph2MessagesMapper.selectLastCastedTime(sigfoxId);
// * 最後の受信時刻が無い場合、lastTimeを初期化する。
		if (null == lastTime)
			{
			lastTime = new Date();
			lastTime.setTime(0);
			}
// * 受信時刻がある場合は1ミリ秒追加する
		else
			{
			lastTime =deviceDayAlgorithm.addMilliscond(lastTime);
			}
		
		this.deviceLogDomain.log(LOG, deviceId, getClass(), "Sigfoxデータを"+lastTime.toString()+"から取り込みます。");
		
// * 問合せを実行する
		String nextUrl;
// * DB登録用のキャッシュを生成
		MessagesCacher cache = new MessagesCacher(this.ph2MessagesMapper);
// * SigFoxデータの最終取得する日以降のものを取得
		try
			{
			Thread.sleep(2000);
			}
		catch(Exception e)
			{
			
			}
		SigFoxMessagesEntity data;
		try
			{
			data = api.getMessages(sigfoxId, lastTime.getTime());
			}
		catch(Exception e)
			{
			this.deviceLogDomain.log(LOG, deviceId, getClass(), "Sigfoxデータの取り込みに失敗しました。");
			this.deviceLogDomain.log(LOG, deviceId, getClass(), "エラー原因は「" + e.getMessage() + "」です。");
			throw e;
			}
// * 取得したデータの状態を得る
		Long startTime = null;
		Long endTime = null;
		int count = data.getData().size();
		if(count > 0)
			{
			startTime = data.getData().get(0).getTime();
			endTime =  data.getData().get(count-1).getTime();
			}

// * メッセージデータを生成し、キャッシュに登録
		nextUrl = this.insertPh2Messages(sigfoxId, data, cache);
// * 次のページがある場合、そのページのURLをコール
		while (null != nextUrl)
			{
			this.deviceLogDomain.log(LOG, deviceId, getClass(), "Sigfoxデータを継続して取り込みます。" + nextUrl);
			data = api.getMessages(nextUrl);
// * メッセージデータを生成し、キャッシュに登録
			if( data.getData().size() > 0)
				{
				final int subCount = data.getData().size();
				count += subCount;
				endTime = data.getData().get(subCount-1).getTime();
				}
			nextUrl = this.insertPh2Messages(sigfoxId, data, cache);
			}
// * キャッシュ内に残っているデータをDBに登録
		cache.flush();
// * 取得したデータを出力
		this.deviceLogDomain.log(LOG, deviceId, getClass(), "取得したSigfoxデータの件数は"+ count + "件です。");
		this.deviceLogDomain.log(LOG, deviceId, getClass(), "取得したSigfoxデータの期間は"
					+ (new Date(startTime)).toString() + "から" +  (new Date(endTime)).toString() + "です。");
		}
	// ===============================================
	// 非公開クラス群
	// ===============================================
	// --------------------------------------------------
	/**
	 * Sigfoxのセンサーデータをメッセージテーブルに代入する
	 * @param sigfoxId
	 * @param data
	 * @param cache
	 * @return ページングデータ
	 */
	// --------------------------------------------------
	private String insertPh2Messages(String sigfoxId, SigFoxMessagesEntity data, MessagesCacher cache)
		{
// * 受信データのタイムゾーンをUTCに矯正するために時差を取得する
		long timezone = TimeZone.getTimeZone("JST").getRawOffset();
// * SigFoxデータからメッセージデータを作成する
		for (SigFoxDataEntity item : data.getData())
			{
// * データが空の場合は代入しない
			if (null == item.getData()) continue;
// * メッセージデータの作成
			Ph2MessagesEntity entity = new Ph2MessagesEntity();
// * 受信データの作成
			Date utc = new Date();
			utc.setTime(item.getTime() - timezone);
			entity.setCastedAt(utc);
			entity.setRaw(item.getData());
			entity.setSeq(item.getSeqNumber());
			entity.setSigfoxId(sigfoxId);
// * メッセージテーブルに登録する
			cache.addMessage(entity);
			}
		if (null == data.getPaging()) return null;
		if (null == data.getPaging().getNext()) return null;
		return data.getPaging().getNext();
		}
	}
