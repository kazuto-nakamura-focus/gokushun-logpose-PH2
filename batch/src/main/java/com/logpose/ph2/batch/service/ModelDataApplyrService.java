package com.logpose.ph2.batch.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.logpose.ph2.batch.dao.db.entity.Ph2SystemStatusEntity;
import com.logpose.ph2.batch.domain.ModelDataDomain;
import com.logpose.ph2.batch.domain.StatusDomain;

@Service
public class ModelDataApplyrService
	{
	// ===============================================
	// クラスメンバー
	// ===============================================
	private static Logger LOG = LogManager
			.getLogger(ModelDataApplyrService.class);
	@Autowired
	private ModelDataDomain modelDataDomain;
	@Autowired
	private StatusDomain statusDomain;

	// ===============================================
	// 公開関数群
	// ===============================================
	@Transactional(rollbackFor = Exception.class)
	public void doService(List<Long> devceList)
		{
		List<Long> idist = new ArrayList<>();
		// * システムステータステーブル(ph2_system_status)からmodel_dataレコードの
		// * 本処理での最終処理レコードの受信時刻を得る。
		Ph2SystemStatusEntity status = this.statusDomain
				.getSystemStatus("model_applid");
		for(Long id : devceList)
			{
			this.modelDataDomain.doService(id);
			}
		status.setLastTime(new Date());
		this.statusDomain.update(status);
		}
	}
