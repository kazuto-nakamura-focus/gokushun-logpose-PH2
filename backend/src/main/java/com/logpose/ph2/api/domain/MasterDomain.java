package com.logpose.ph2.api.domain;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.logpose.ph2.api.dao.db.mappers.Ph2SensorContentsMapper;
import com.logpose.ph2.api.dao.db.mappers.Ph2SensorModelsMapper;
import com.logpose.ph2.api.dao.db.mappers.SensorSizesMapper;
import com.logpose.ph2.api.dto.device.DeviceMastersDTO;

@Component
public class MasterDomain
	{
	// ===============================================
	// クラスメンバー
	// ===============================================
	@Autowired
	private Ph2SensorContentsMapper ph2SensorContentsMapper;
	@Autowired
	private Ph2SensorModelsMapper ph2SensorModelMapper;
	@Autowired
	private SensorSizesMapper Ph2SensorSizesMapper;
	// ===============================================
	// 公開関数
	// ===============================================
	/** --------------------------------------------------
	 * デバイス関連のマスター情報を取得する。
	 *
	 * @return DeviceMastersDTO
	 ------------------------------------------------------ */
	public DeviceMastersDTO getMasters()
		{
		DeviceMastersDTO result = new DeviceMastersDTO();
		result.setSensorContents(this.ph2SensorContentsMapper.getAll());
		result.setSensorModels(this.ph2SensorModelMapper.getAll());
		result.setSensorSizes(this.Ph2SensorSizesMapper.getAll());
		return result;
		}

	}
