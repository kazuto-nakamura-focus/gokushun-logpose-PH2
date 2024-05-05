package com.logpose.ph2.api.domain.model;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.logpose.ph2.api.dao.db.entity.Ph2RealGrowthFStageEntity;
import com.logpose.ph2.api.dao.db.entity.Ph2RealGrowthFStageEntityExample;
import com.logpose.ph2.api.dao.db.mappers.Ph2ModelDataMapper;
import com.logpose.ph2.api.dao.db.mappers.Ph2RealGrowthFStageMapper;
import com.logpose.ph2.api.dao.db.mappers.Ph2RealLeafShootsAreaMapper;

@Component
public class AppliedModel
	{
	// ===============================================
	// クラスメンバー
	// ===============================================
	@Autowired
	private Ph2RealGrowthFStageMapper realGrowthFStageMapper;
	@Autowired
	private Ph2ModelDataMapper modelDataMapper;
	@Autowired
	protected Ph2RealLeafShootsAreaMapper ph2RealLeafShootsAreaMapper;

	// ===============================================
	// 公開関数群
	// ===============================================
	// ###############################################
	/**
	 * 萌芽日を取得する
	 * @param deviceId デバイスID
	 * @param year 年度
	 * @return 萌芽の経過日
	 */
	// ###############################################
	public short getSproutDay(Long deviceId, Short year)
		{
		Ph2RealGrowthFStageEntityExample exm = new Ph2RealGrowthFStageEntityExample();
		exm.createCriteria().andDeviceIdEqualTo(deviceId).andYearEqualTo(year).andStageEndEqualTo((short) 4);

		List<Ph2RealGrowthFStageEntity> rec = this.realGrowthFStageMapper.selectByExample(exm);
		Ph2RealGrowthFStageEntity entity = rec.get(0);

		List<Integer> sproutDays = this.modelDataMapper.selectLapseDayByFValue(deviceId, year,
				entity.getActualDate(), entity.getAccumulatedF());
		if (sproutDays.size() == 0)
			{
			sproutDays = this.modelDataMapper.selectLapseDayByFValue(deviceId, year, null, (double) 15);
			}
		return sproutDays.get(0).shortValue();
		}

	}
