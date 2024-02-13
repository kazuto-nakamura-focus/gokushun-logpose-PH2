package com.logpose.ph2.api.bulk.service;

import java.text.ParseException;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.logpose.ph2.api.bulk.domain.ModelDataDomain;
import com.logpose.ph2.api.dao.db.entity.Ph2DeviceDayEntity;

@Service
public class S7ModelDataApplyrService
	{
	// ===============================================
	// クラスメンバー
	// ===============================================
	private static Logger LOG = LogManager
			.getLogger(S7ModelDataApplyrService.class);
	@Autowired
	private ModelDataDomain modelDataDomain;

	// ===============================================
	// 公開関数群
	// ===============================================
	// --------------------------------------------------
	/**
	 * 各モデルデータ生成のモジュールを呼び出す
	 * @param deviceId 処理対象となるデバイスID
	 * @param deviceDays 年度初日のデータで実測値があるもの
	 * @throws ParseException
	 */
	// --------------------------------------------------
	public void doService(Long deviceId, List<Ph2DeviceDayEntity> deviceDays) throws Exception
		{
		LOG.info("モデルデータの作成を開始します。", deviceId);
		short year = 0;
		for (Ph2DeviceDayEntity entity : deviceDays)
			{
// * 年度替わりの場合
			if (entity.getYear().shortValue() != year)
				{
				year = entity.getYear().shortValue();
// * 経過日が１で、実データがある場合
				if (entity.getLapseDay().shortValue() == 1)
					{
					if(entity.getHasReal())
						{
						LOG.info(year + "年度モデルデータの作成。", deviceId);
						this.modelDataDomain.doService(deviceId, entity.getYear(), entity.getDate());
						}
					}
				}
			}
		LOG.info("モデルデータの作成が終了しました。", deviceId);
		}
	}
