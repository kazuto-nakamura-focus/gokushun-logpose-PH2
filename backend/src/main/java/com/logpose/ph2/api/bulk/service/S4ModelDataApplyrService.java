package com.logpose.ph2.api.bulk.service;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.logpose.ph2.api.bulk.domain.ModelDataDomain;
import com.logpose.ph2.api.dao.db.entity.Ph2DeviceDayEntity;

@Service
public class S4ModelDataApplyrService
	{
	// ===============================================
	// クラスメンバー
	// ===============================================
	private static Logger LOG = LogManager
			.getLogger(S4ModelDataApplyrService.class);
	@Autowired
	private ModelDataDomain modelDataDomain;

	// ===============================================
	// 公開関数群
	// ===============================================
	public void doService(List<Ph2DeviceDayEntity> deviceDays, Date startDay) throws ParseException
		{
		LOG.info("モデルデータの作成を開始します。", startDay);
		// * 年度の開始日まで移動
		List<Ph2DeviceDayEntity> tmp = new ArrayList<>();
		for (Ph2DeviceDayEntity entity : deviceDays)
			{
			if (entity.getLapseDay() == 1)
				{
				// 既に３６５日以上用意されている
				if (tmp.size() >= 365)
					{
					Ph2DeviceDayEntity deviceDay = tmp.get(0);
					if (deviceDay.getHasReal())
						{
						this.modelDataDomain.doService(deviceDay.getDeviceId(), deviceDay.getYear(),
								deviceDay.getDate());
						}
					}
				tmp.clear();
				}
			tmp.add(entity);
			}
		if (tmp.size() >= 0)
			{
			Ph2DeviceDayEntity deviceDay = tmp.get(0);
			if (deviceDay.getHasReal())
				{
				this.modelDataDomain.doService(deviceDay.getDeviceId(), deviceDay.getYear(),
						deviceDay.getDate());
				}
			}
		LOG.info("モデルデータの作成が終了しました。", startDay);
		}
	}
