package com.logpose.ph2.api.service.impl;

import java.text.ParseException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.logpose.ph2.api.dao.db.entity.Ph2RealFruitsDataEntity;
import com.logpose.ph2.api.dao.db.entity.Ph2RealGrowthFStageEntity;
import com.logpose.ph2.api.domain.DeviceDomain;
import com.logpose.ph2.api.domain.FruitDomain;
import com.logpose.ph2.api.domain.bearing.BearingDomain;
import com.logpose.ph2.api.domain.model.AppliedModel;
import com.logpose.ph2.api.dto.FruitValuesByDevice;
import com.logpose.ph2.api.dto.FruitValuesDTO;
import com.logpose.ph2.api.dto.bearing.BearingDTO;
import com.logpose.ph2.api.dto.bearing.RealFruitesValues;
import com.logpose.ph2.api.dto.bearing.RealFruitsValueDTO;
import com.logpose.ph2.api.dto.device.DeviceTermDTO;
import com.logpose.ph2.api.service.FruitsService;
import com.logpose.ph2.api.utility.DateTimeUtility;

/**
 * 着果量着果負担のサービスインターフェスクラス
 *
 */
@Service
public class FruitsServiceImpl implements FruitsService
	{
	// ===============================================
	// クラスメンバー
	// ===============================================
	@Autowired
	private FruitDomain fruitDomain;
	@Autowired
	private BearingDomain bearingDomain;
	@Autowired
	private AppliedModel appliedModel;
	@Autowired
	private DeviceDomain deviceDomain;

	// ===============================================
	// パブリック関数(検索系)
	// ===============================================
	// --------------------------------------------------
	/**
	 * 着果実績値取得
	 *
	 * @param deviceId
	 * @param date
	 * @param eventId
	 * @return RealFruitesValues
	 * @throws ParseException 
	 */
	// --------------------------------------------------
	@Override
	@Transactional(readOnly = true)
	public RealFruitesValues getRealFruitsData(Long deviceId, Short year) throws ParseException
		{
		RealFruitesValues result = new RealFruitesValues();
		List<RealFruitsValueDTO> values = this.fruitDomain.getRealFruitsData(deviceId, year);
		result.setValues(values);
		DeviceTermDTO term = this.deviceDomain.getTerm(deviceId, year);
		result.setStartDate(term.getStartDate());
		result.setEndDate(term.getEndDate());
		return result;
		}

	// --------------------------------------------------
	/**
	 * 圃場着果量着果負担詳細取得処理
	 *
	 * @param deviceId-デバイスID
	 * @param year - 年度
	 * @return 着果実績値
	 * @throws ParseException
	 */
	// --------------------------------------------------
	@Override
	@Transactional(readOnly = true)
	public FruitValuesDTO getFruitValues(Long deviceId, Short year)
		{
		return this.fruitDomain.getFruitData(deviceId, year);
		}

	// --------------------------------------------------
	/**
	 * 圃場着果量着果負担詳細取得処理Ver2
	 *
	 * @param deviceId-デバイスID
	 * @param year - 年度
	 * @return 着果実績値
	 * @throws ParseException
	 */
	// --------------------------------------------------
	@Override
	@Transactional(readOnly = true)
	public BearingDTO getFruitValues2(Long deviceId, Short year) throws ParseException
		{
		BearingDTO result = new BearingDTO();
		result.setId(deviceId);
		result.setYear(year);

// * 収穫時のステージを得る
		Ph2RealGrowthFStageEntity harvestDate = this.appliedModel.getHarvestDate(deviceId, year);

// * 収穫時樹冠葉面積(m^2)を得る
		Double havestTimeCrownArea = this.bearingDomain.getHarvestTimeCrownArea(deviceId, year, harvestDate);
		if (null != havestTimeCrownArea)
			{
			result.setHarvestCrownLeafArea(havestTimeCrownArea.floatValue());
			}

// * 積算樹冠光合成量(kgCO2vine^-1)
		Double crownAreaPhotoSynthesis = this.bearingDomain.getCrownAreaPhotoSynthesis(deviceId, year, harvestDate);
		if (null != crownAreaPhotoSynthesis)
			{
			result.setCulminatedCrownPhotoSynthesysAmount(crownAreaPhotoSynthesis.floatValue());
			}
		
// * 果実総重量の取得
		Ph2RealFruitsDataEntity entity = this.bearingDomain.getFruitsEntity(deviceId, year);
		if (null != entity)
			{
			result.setDate(DateTimeUtility.getStringFromDate(entity.getTargetDate()));
			float weightSum = this.bearingDomain.getAllFruitsWeight(entity);
			result.setBearingWeight(weightSum);

// * 着果負担（果実総重量/収穫時樹冠葉面積）(g/m^2)
			result.setBearingWeight(weightSum / havestTimeCrownArea.floatValue());

// * 積算樹冠光合成量あたりの着果量（果実総重量/積算樹冠光合成量）(g/kgCO2 vine^-1)
			result.setBearingPerPhotoSynthesys(weightSum / crownAreaPhotoSynthesis.floatValue());

// * 着果数の取得
			float fruitsSum = this.bearingDomain.getRealFruitsSum(entity);
			result.setBearingCount(fruitsSum / havestTimeCrownArea.floatValue());
			}
		return result;
		}

	// ===============================================
	// パブリック関数(更新系)
	// ===============================================
	// --------------------------------------------------
	/**
	 * 着果実績値更新
	 *
	 * @param dto FruitAmountDTO
	 * @throws ParseException
	 */
	// --------------------------------------------------
	@Override
	@Transactional(rollbackFor = Exception.class)
	public void setFruitValues(FruitValuesByDevice dto) throws ParseException
		{
		Ph2RealFruitsDataEntity entity = new Ph2RealFruitsDataEntity();
		entity.setAverage(dto.getWeight());
		entity.setCount(dto.getCount());
		entity.setDeviceId(dto.getDeviceId());
		entity.setEventId(dto.getEventId());
		entity.setTargetDate(DateTimeUtility.getDateFromString(dto.getDate()));
		entity.setYear(dto.getYear());

		this.fruitDomain.setFruitValue(entity);
		}
	// --------------------------------------------------
	/**
	 * 実績値のクリア
	 *
	 * @return Ph2RealFruitsDataEntity
	 * @throws ParseException 
	 */
	// --------------------------------------------------
	@Override
	@Transactional(rollbackFor = Exception.class)
	public void deletetRealFruitsData(Long deviceId, Short year, Short eventId) throws ParseException
		{
		this.fruitDomain.delete(deviceId, year, eventId);
		}

	}
