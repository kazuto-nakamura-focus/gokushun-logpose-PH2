package com.logpose.ph2.api.domain.growth;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.logpose.ph2.api.dao.db.entity.Ph2RealGrowthFStageEntity;
import com.logpose.ph2.api.dao.db.mappers.Ph2ModelDataMapper;
import com.logpose.ph2.api.dao.db.mappers.Ph2RealGrowthFStageMapper;
import com.logpose.ph2.api.dto.ValueDateDTO;
import com.logpose.ph2.api.dto.growth.FValuesDTO;

@Component
public class FStageTableDomain
	{
	// ===============================================
	// クラスメンバー
	// ===============================================
	@Autowired
	private Ph2ModelDataMapper ph2ModelDataMapper;
	@Autowired
	private Ph2RealGrowthFStageMapper ph2RealGrowthFStageMapper;

	// ===============================================
	// 公開関数群
	// ===============================================
	// --------------------------------------------------
	/**
	 * Ph2RealGrowthFStageの推定値の設定
	 * @param deviceId
	 * @param year
	 * @throws ParseException 
	 */
	// --------------------------------------------------
	public void resetActualDate(Long deviceId, Short year) throws ParseException
		{
		List<Ph2RealGrowthFStageEntity> fstages = this.ph2RealGrowthFStageMapper.selectByDeviceAndYear(deviceId, year);
		this.resetActualDate(deviceId, year, fstages);
		}

	// --------------------------------------------------
	/**
	 * Ph2RealGrowthFStageの推定値の設定
	 * @param deviceId
	 * @param year
	 * @param fstages
	 * @throws ParseException 
	 */
	// --------------------------------------------------
	public void resetActualDate(Long deviceId, Short year, List<Ph2RealGrowthFStageEntity> fstages)
			throws ParseException
		{
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

// * 指定デバイスの年度からF値のリストを得る
		List<ValueDateDTO> allData = this.ph2ModelDataMapper.selectFValueByYear(deviceId, year);
		int index = 0;
		for (Ph2RealGrowthFStageEntity entity : fstages)
			{
// * 実測値または推定値が無い場合
			if ((null == entity.getEstimateDate()) || (null == entity.getActualDate()))
				{
// * F値リストから該当日を探す
				for (; index < allData.size(); index++)
					{
					ValueDateDTO value = allData.get(index);
// * F値が超えた場合
					if (value.getValue() >= entity.getAccumulatedF())
						{
						Date date = sdf.parse(value.getDate());
						entity.setEstimateDate(date);
						this.ph2RealGrowthFStageMapper.updateByPrimaryKey(entity);
						index++;
						break;
						}
					}
				}
			}
		}

	// --------------------------------------------------
	/**
	 * 日付からF値の情報を得る
	 *
	 * @param id
	 * @param date
	 */
	// --------------------------------------------------
	public FValuesDTO checkFValueByDate(Long id, Date date)
		{
		FValuesDTO result = new FValuesDTO();
		
		Ph2RealGrowthFStageEntity entity = this.ph2RealGrowthFStageMapper.selectByPrimaryKey(id);
// * 指定デバイスの年度からF値のリストを得る
		ValueDateDTO value = this.ph2ModelDataMapper.selectFValueBySpecificDate(entity.getDeviceId(), entity.getYear(), date);
		if (null == value)
			{
			throw new RuntimeException("該当する日付にデータはありません。");
			}
		result.setAccumulated(value.getValue());
		double diff = value.getValue().doubleValue() - entity.getAccumulatedF().doubleValue();
		double interval = entity.getIntervalF().doubleValue() + diff;
		if(interval < 0)
			{
			throw new RuntimeException("適切なインターバルではありません。");
			}
		result.setInterval(interval);

		return result;
		}

	}
