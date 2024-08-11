package com.logpose.ph2.api.bulk.service;

import java.text.ParseException;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.logpose.ph2.api.bulk.domain.DeviceLogDomain;
import com.logpose.ph2.api.dao.db.entity.Ph2DeviceDayEntity;
import com.logpose.ph2.api.dao.db.entity.Ph2DevicesEntity;
import com.logpose.ph2.api.domain.model.ModelDataDomain;

@Service
public class S8ModelDataApplyrService
	{
	// ===============================================
	// クラスメンバー
	// ===============================================
	private static Logger LOG = LogManager.getLogger(S8ModelDataApplyrService.class);
	@Autowired
	private DeviceLogDomain deviceLogDomain;
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
	public void doService(Ph2DevicesEntity device, List<Ph2DeviceDayEntity> deviceDays, boolean isAll) throws Exception
		{
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
					try
						{
						this.modelDataDomain.doService(device.getId(), entity.getYear());
						this.deviceLogDomain.log(LOG, device, getClass(), year+"年度の各モデルデータの作成を完了しました。", isAll);
						}
					catch(Exception e)
						{
						this.deviceLogDomain.log(LOG, device, getClass(), year+"年度の各モデルデータの作成に失敗しました。", isAll);
						throw e;
						}
					}
				}
			}
		}
	}
