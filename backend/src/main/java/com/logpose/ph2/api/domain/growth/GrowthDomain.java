package com.logpose.ph2.api.domain.growth;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.logpose.ph2.api.algorythm.DeviceDayAlgorithm;
import com.logpose.ph2.api.dao.db.entity.Ph2RealGrowthFStageEntity;
import com.logpose.ph2.api.dao.db.entity.Ph2RealGrowthFStageEntityExample;
import com.logpose.ph2.api.dao.db.mappers.Ph2ModelDataMapper;
import com.logpose.ph2.api.dao.db.mappers.Ph2RealGrowthFStageMapper;
import com.logpose.ph2.api.dto.FDataListDTO;
import com.logpose.ph2.api.dto.ValueDateDTO;

@Component
public class GrowthDomain
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
	// ###############################################
	/**
	 * 生育推定F値データ取得
	 *
	 * @param deviceId デバイスID
	 * @param year  年度
	 * @return List<Ph2RealGrowthFStageEntity>
	 */
	// ###############################################
	public List<Ph2RealGrowthFStageEntity> getALlFValus(Long deviceId, Short year)
		{
		Ph2RealGrowthFStageEntityExample exm = new Ph2RealGrowthFStageEntityExample();
		exm.createCriteria().andDeviceIdEqualTo(deviceId).andYearEqualTo(year);
		exm.setOrderByClause("stage_start asc");
		List<Ph2RealGrowthFStageEntity> result = this.ph2RealGrowthFStageMapper
				.selectByExample(exm);
		for (final Ph2RealGrowthFStageEntity item : result)
			{
			if (null != item.getActualDate())
				{
				if (item.getActualDate().getTime() < 0)
					{
					item.setActualDate(null);
					}
				}
			}
		return result;
		}

	// ###############################################
	/**
	 * 生育推定実績値取得
	 *
	 * @param deviceId デバイスID
	 * @param date 日付
	 * @return ValueDateDTO
	 */
	// ###############################################
	public ValueDateDTO getRealFData(Long deviceId, Date date)
		{
// * 時刻を０時間に設定する
		date = new DeviceDayAlgorithm().getTimeZero(date);
// * モデルテーブルから該当する日付にもっとも近い過去の実績値を取得する
		ValueDateDTO value = this.ph2ModelDataMapper.selectFValueByDate(deviceId, date);
		if (null == value)
			{
			throw new RuntimeException("実績データはまだありません。");
			}
		return value;
		}

	// ###############################################
	/**
	 * 生育推定実績値更新
	 *
	 * @param deviceId デバイスID
	 * @param date 日付
	 * @param dto FDataListDTO
	 */
	// ###############################################
	@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRES_NEW)
	public void updateFValues(long deviceId, short year, FDataListDTO dto)
		{
// * 更新時刻の設定
		Date updateTime = new Timestamp(System.currentTimeMillis());
		for (Ph2RealGrowthFStageEntity item : dto.getList())
			{
			// * IDがある場合
			if (null != item.getId())
				{
				// * 該当のFstageデータを取得する
				Ph2RealGrowthFStageEntity entity = this.ph2RealGrowthFStageMapper
						.selectByPrimaryKey(item.getId());
				entity.setAccumulatedF(item.getAccumulatedF());
				entity.setIntervalF(item.getIntervalF());
				entity.setStageStart(item.getStageStart());
				entity.setStageEnd(item.getStageEnd());
				entity.setStageName(item.getStageName());
				entity.setActualDate(item.getActualDate());
				entity.setEstimateDate(item.getEstimateDate());
				entity.setColor(item.getColor());
				entity.setUpdatedAt(updateTime);
				this.ph2RealGrowthFStageMapper.updateByPrimaryKey(entity);
				}
			else
				{
				item.setDeviceId(dto.getDeviceId());
				item.setYear(dto.getYear());
				item.setCreatedAt(updateTime);
				item.setUpdatedAt(updateTime);
				this.ph2RealGrowthFStageMapper.insert(item);
				}
			}
// * 未更新の削除処理
		Ph2RealGrowthFStageEntityExample exp = new Ph2RealGrowthFStageEntityExample();
		exp.createCriteria().andDeviceIdEqualTo(deviceId)
				.andYearEqualTo(year).andUpdatedAtNotEqualTo(updateTime);
		this.ph2RealGrowthFStageMapper.deleteByExample(exp);
		}

	}
