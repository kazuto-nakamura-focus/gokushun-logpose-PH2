package com.logpose.ph2.api.domain.bearing;

import java.text.ParseException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.logpose.ph2.api.dao.db.entity.Ph2RealFruitsDataEntity;
import com.logpose.ph2.api.dao.db.entity.Ph2RealFruitsDataEntityExample;
import com.logpose.ph2.api.dao.db.entity.Ph2RealGrowthFStageEntity;
import com.logpose.ph2.api.dao.db.entity.Ph2RealLeafShootsAreaEntity;
import com.logpose.ph2.api.dao.db.mappers.Ph2ModelDataMapper;
import com.logpose.ph2.api.dao.db.mappers.Ph2RealFruitsDataMapper;
import com.logpose.ph2.api.dao.db.mappers.Ph2RealLeafShootsAreaMapper;

public class BearingDomain
	{
	// ===============================================
	// クラスメンバー
	// ===============================================
	@Autowired
	private Ph2ModelDataMapper ph2ModelDataMapper;
	@Autowired
	private Ph2RealLeafShootsAreaMapper ph2RealLeafShootsAreaMapper;
	@Autowired
	private Ph2RealFruitsDataMapper ph2RealFruitsDataMapper;

	// ===============================================
	// 公開関数群
	// ===============================================
	// ###############################################
	/**
	 * 収穫時樹冠葉面積(m^2)
	 *
	 * @param deviceId デバイスID
	 * @param year 年度
	 * @param stage 収穫日のステージ情報
	 * @return 収穫時樹冠葉面積
	 */
	// ###############################################
	public Double getHarvestTimeCrownArea(Long deviceId, Short year, Ph2RealGrowthFStageEntity stage)
		{
// * 各生育予測の実績日が入力されている場合
		if (null != stage.getActualDate())
			{
			Ph2RealLeafShootsAreaEntity entity = this.ph2RealLeafShootsAreaMapper.selectByPrimaryKey(deviceId,
					stage.getActualDate());
// * 実測値がある場合
			if (null != entity)
				{
				return entity.getRealArea();
				}
// * 実測値が無い場合
			else
				{
				return this.ph2ModelDataMapper.selectCrownLeafAreaBySpecificDate(deviceId, year,
						stage.getActualDate());
				}
			}
// * 実績日が無い場合
		else
			{
			return this.ph2ModelDataMapper.selectCrownLeafAreaBySpecificDate(deviceId, year,
					stage.getEstimateDate());
			}
		}

	// ###############################################
	/**
	 * 積算樹冠光合成量(kgCO2vine^-1)
	 *
	 * @param deviceId デバイスID
	 * @param year 年度
	 * @param stage 収穫日のステージ情報
	 * @return 積算樹冠光合成量
	 */
	// ###############################################
	public Double getCrownAreaPhotoSynthesis(Long deviceId, Short year, Ph2RealGrowthFStageEntity stage)
		{
		if (null != stage.getActualDate())
			{
			return this.ph2ModelDataMapper.selectPhotosynthesisBySpecificDate(deviceId, year,
					stage.getActualDate());
			}
		else
			{
			return this.ph2ModelDataMapper.selectPhotosynthesisBySpecificDate(deviceId, year,
					stage.getEstimateDate());
			}
		}

	// ###############################################
	/**
	 * 実測日のデータを得る
	 *
	 * @param deviceId デバイスID
	 * @param year 年度
	 * @return 実測日のデータ
	 */
	// ###############################################
	public Ph2RealFruitsDataEntity getFruitsEntity(Long deviceId, Short year)
		{
		Ph2RealFruitsDataEntityExample exm = new Ph2RealFruitsDataEntityExample();
		exm.createCriteria().andDeviceIdEqualTo(deviceId).andYearEqualTo(year);
		exm.setOrderByClause("target_date desc");
		List<Ph2RealFruitsDataEntity> entities = this.ph2RealFruitsDataMapper.selectByExample(exm);
		return (entities.size() == 0) ? null : entities.get(0);
		}

	// ###############################################
	/**
	 * 果実総重量を得る
	 *
	 * @param entity 実測値
	 * @return 果実総重量
	 * @throws ParseException 
	 */
	// ###############################################
	public float getAllFruitsWeight(Ph2RealFruitsDataEntity entity) throws ParseException
		{
		return entity.getAverage() * entity.getCount();
		}

	// ###############################################
	/**
	 * 実測着果数を得る
	 *
	 * @param entity 実測値
	 * @return 実測着果数
	 */
	// ###############################################
	public float getRealFruitsSum(Ph2RealFruitsDataEntity entity)
		{
		return entity.getCount();
		}
	}
