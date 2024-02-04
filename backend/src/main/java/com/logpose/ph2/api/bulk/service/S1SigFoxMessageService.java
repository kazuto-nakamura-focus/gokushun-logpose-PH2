package com.logpose.ph2.api.bulk.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.logpose.ph2.api.bulk.domain.SigFoxDomain;
import com.logpose.ph2.api.configration.DefaultSecurityParameters;
import com.logpose.ph2.api.configration.DefaultSigFoxParameters;
import com.logpose.ph2.api.dao.api.SigFoxAPI;
import com.logpose.ph2.api.dao.db.entity.Ph2DevicesEnyity;

/**
 * SigFoxのデータをデバイス毎に取り込む
 */
@Service
public class S1SigFoxMessageService
	{
	// ===============================================
	// クラスメンバー
	// ===============================================
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
	public void doService(Ph2DevicesEnyity device) throws InterruptedException
		{
// * APIのDAOを作成し、アクセス情報を設定する
		SigFoxAPI api = new SigFoxAPI();
		api.setSigFoxUrl(this.params.getUrl());
// * timezoneから認証を設定する。
		String timeZone = device.getTz();
		String baseAuth = securityParams.getBaseAuthSigFoxTK();
		if (timeZone.equals("Pacific/Auckland"))
			{
			baseAuth = securityParams.getBaseAuthSigFoxNZ();
			}
		api.setBasicAuth(baseAuth);
// * デバイスに対して最新のSigfox データを取り込む
		this.sigFoxDomain.createMessages(device, api);
		}
	}
