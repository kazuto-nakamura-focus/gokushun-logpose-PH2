package com.logpose.ph2.api.bulk.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.logpose.ph2.api.bulk.domain.DeviceLogDomain;
import com.logpose.ph2.api.bulk.domain.SigFoxDomain;
import com.logpose.ph2.api.configration.DefaultSecurityParameters;
import com.logpose.ph2.api.configration.DefaultSigFoxParameters;
import com.logpose.ph2.api.dao.api.SigFoxAPI;
import com.logpose.ph2.api.dao.db.entity.Ph2DevicesEntity;

/**
 * SigFoxのデータをデバイス毎に取り込む
 * @author 高橋史成
 */
@Service
public class S1SigFoxMessageService
	{
	// ===============================================
	// クラスメンバー
	// ===============================================
	private static Logger LOG = LogManager.getLogger(S1SigFoxMessageService.class);
	@Autowired
	private DeviceLogDomain deviceLogDomain;
	@Autowired
	private DefaultSigFoxParameters params;
	@Autowired
	private DefaultSecurityParameters securityParams;
	@Autowired
	private SigFoxDomain sigFoxDomain;

	// ===============================================
	// 公開関数群
	// ===============================================
	// --------------------------------------------------
	/**
	 * 指定されたデバイスに対してSigFoxのデータを取り込む
	 * @param device
	 * @throws InterruptedException 
	 */
	// --------------------------------------------------
	public void doService(Ph2DevicesEntity device) throws InterruptedException
		{
// * APIのDAOを作成し、アクセス情報を設定する
		SigFoxAPI api = new SigFoxAPI();
		api.setSigFoxUrl(this.params.getUrl());
// * timezoneから認証を設定する。
		String timeZone = device.getTz();
		String baseAuth = securityParams.getBaseAuthSigFoxTK();
		
		this.deviceLogDomain.log(LOG, device.getId(), getClass(), "タイムゾーンは"+timeZone+"です。");
		
		if (timeZone.equals("Pacific/Auckland"))
			{
			baseAuth = securityParams.getBaseAuthSigFoxNZ();
			}
		api.setBasicAuth(baseAuth);
		
		this.sigFoxDomain.createMessages(device.getId(), device.getSigfoxDeviceId(), api);
		}
	}
