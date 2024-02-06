package com.logpose.ph2.api.bulk.service;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.logpose.ph2.api.dao.db.entity.Ph2HeadLinesEntity;
import com.logpose.ph2.api.dao.db.entity.Ph2HeadLinesEntityExample;
import com.logpose.ph2.api.dao.db.entity.Ph2RawDataEntity;
import com.logpose.ph2.api.dao.db.entity.Ph2RawDataEntityExample;
import com.logpose.ph2.api.dao.db.mappers.Ph2HeadLinesMapper;
import com.logpose.ph2.api.dao.db.mappers.Ph2RawDataMapper;

@Service
public class S4HeadLineLoaderService
	{
	// ===============================================
	// クラスメンバー
	// ===============================================
	@Autowired
	private Ph2RawDataMapper rawDataMapper;
	@Autowired
	private Ph2HeadLinesMapper ph2HeadLinesMapper;

	// ===============================================
	// 公開関数群
	// ===============================================
	@Transactional(rollbackFor = Exception.class)
	@CacheEvict(value="getFieldData",  allEntries = true)
	public void createHealines(Long deviceId, Date lastTime)
		{
// * RawDataから最後の更新日付のデータを取得する
		Ph2RawDataEntityExample ex = new Ph2RawDataEntityExample();
		ex.createCriteria().andCastedAtEqualTo(lastTime);
		List<Ph2RawDataEntity> entities = this.rawDataMapper.selectByExample(ex);
		
		Ph2HeadLinesEntityExample hdex = new Ph2HeadLinesEntityExample();
		hdex.createCriteria().andDeviceIdEqualTo(deviceId);
		this.ph2HeadLinesMapper.deleteByExample(hdex);
		
		for(Ph2RawDataEntity entity : entities)
			{
			Ph2HeadLinesEntity headline = new Ph2HeadLinesEntity();
			headline.setDeviceId(deviceId);
			headline.setRawDataId(entity.getId());
			this.ph2HeadLinesMapper.insert(headline);
			}
		}
	}
