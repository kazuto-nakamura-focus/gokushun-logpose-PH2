package com.logpose.ph2.api.domain;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.logpose.ph2.api.dao.db.entity.Ph2DevicesEntity;
import com.logpose.ph2.api.dao.db.entity.Ph2RealFruitsBearingEntity;
import com.logpose.ph2.api.dao.db.entity.Ph2RealFruitsBearingEntityExample;
import com.logpose.ph2.api.dao.db.entity.Ph2RealFruitsDataEntity;
import com.logpose.ph2.api.dao.db.entity.Ph2RealFruitsDataEntityExample;
import com.logpose.ph2.api.dao.db.entity.Ph2RealGrowthFStageEntity;
import com.logpose.ph2.api.dao.db.entity.Ph2RealGrowthFStageEntityExample;
import com.logpose.ph2.api.dao.db.entity.Ph2RealLeafShootsAreaEntity;
import com.logpose.ph2.api.dao.db.entity.Ph2RealLeafShootsAreaEntityExample;
import com.logpose.ph2.api.dao.db.entity.Ph2RealLeafShootsCountEntity;
import com.logpose.ph2.api.dao.db.entity.Ph2RealLeafShootsCountEntityExample;
import com.logpose.ph2.api.dao.db.entity.Ph2RealPsAmountEntity;
import com.logpose.ph2.api.dao.db.entity.Ph2RealPsAmountEntityExample;
import com.logpose.ph2.api.dao.db.mappers.Ph2DeviceDayMapper;
import com.logpose.ph2.api.dao.db.mappers.Ph2DevicesMapper;
import com.logpose.ph2.api.dao.db.mappers.Ph2RealFruitsBearingMapper;
import com.logpose.ph2.api.dao.db.mappers.Ph2RealFruitsDataMapper;
import com.logpose.ph2.api.dao.db.mappers.Ph2RealGrowthFStageMapper;
import com.logpose.ph2.api.dao.db.mappers.Ph2RealLeafShootsAreaMapper;
import com.logpose.ph2.api.dao.db.mappers.Ph2RealLeafShootsCountMapper;
import com.logpose.ph2.api.dao.db.mappers.Ph2RealPsAmountMapper;
import com.logpose.ph2.api.dto.device.DeviceTransitInfoDTO;

/**
 * デバイス処理を行う
 */

@Component
public class DeviceTransitDomain
	{
	// ===============================================
	// クラスメンバー
	// ===============================================
	@Autowired
	private Ph2DevicesMapper devicesMapper;
	@Autowired
	private Ph2DeviceDayMapper deviceDayMapper;
	@Autowired
	private Ph2RealFruitsBearingMapper ph2RealFruitsBearingMapper;
	@Autowired
	private Ph2RealFruitsDataMapper ph2RealFruitsDataMapper;
	@Autowired
	private Ph2RealGrowthFStageMapper ph2RealGrowthFStageMapper;
	@Autowired
	private Ph2RealLeafShootsAreaMapper ph2RealLeafShootsAreaMapper;
	@Autowired
	private Ph2RealLeafShootsCountMapper ph2RealLeafShootsCountMapper;
	@Autowired
	private Ph2RealPsAmountMapper ph2RealPsAmountMapper;

	// ===============================================
	// パブリック関数群
	// ===============================================
	// --------------------------------------------------
	/**
	 * 引継ぎ元情報を取得する
	 *
	 *@param deviceId
	 *@return DeviceTransitInfoDTO
	 */
	// ------------------------------------------------------
	public DeviceTransitInfoDTO getTransitInfo(Long deviceId)
		{
		DeviceTransitInfoDTO result = new DeviceTransitInfoDTO();
		Ph2DevicesEntity target = this.devicesMapper.selectByPrimaryKey(deviceId);
// * 引継ぎ元デバイス
		result.setPrevDeviceId(target.getPreviousDeviceId());
		if (null == result.getPrevDeviceId()) return null;
// * 運用開始日
		result.setEndDate(target.getOpStart());
		if (null == result.getEndDate()) return null;
// * 運用開始日から年度の開始日を得る
		Short year = this.deviceDayMapper.getYear(deviceId, result.getEndDate());
		Calendar cal = Calendar.getInstance();
		cal.setTime(target.getBaseDate());
		cal.set(Calendar.YEAR, year);
		result.setYear(year);
// * 年度の開始日と運用開始日が同一の場合、終了
		if (cal.getTime().getTime() == result.getEndDate().getTime()) return null;
		result.setStartDate(cal.getTime());
		return result;
		}

	// --------------------------------------------------
	/**
	 * 各デバイスの実績値を更新する
	 *
	 *@param DeviceTransitInfoDTO
	 */
	// ------------------------------------------------------
	public void updateRealValues(Long deviceId, DeviceTransitInfoDTO dto)
		{
// * 着果負担
			{
			// 引継ぎ元IDの該当年度のデータを取得する
			Ph2RealFruitsBearingEntityExample exm = new Ph2RealFruitsBearingEntityExample();
			exm.createCriteria().andDeviceIdEqualTo(dto.getPrevDeviceId()).andYearEqualTo(dto.getYear());
			List<Ph2RealFruitsBearingEntity> entities = this.ph2RealFruitsBearingMapper.selectByExample(exm);
			// 引継ぎ元データがある場合
			if (0 < entities.size())
				{
				// 引継ぎ先データの削除
				exm = new Ph2RealFruitsBearingEntityExample();
				exm.createCriteria().andDeviceIdEqualTo(deviceId).andYearEqualTo(dto.getYear());
				this.ph2RealFruitsBearingMapper.deleteByExample(exm);
				// 引継ぎデータの追加
				Ph2RealFruitsBearingEntity updateEnyity = entities.get(0);
				updateEnyity.setDeviceId(deviceId);
				updateEnyity.setCreatedAt(new Timestamp(System.currentTimeMillis()));
				updateEnyity.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
				this.ph2RealFruitsBearingMapper.insert(updateEnyity);
				}
			}
// * 実測着果数
			{
			// 引継ぎ元IDの該当年度のデータを取得する
			Ph2RealFruitsDataEntityExample exm = new Ph2RealFruitsDataEntityExample();
			exm.createCriteria().andDeviceIdEqualTo(dto.getPrevDeviceId()).andYearEqualTo(dto.getYear())
					.andTargetDateLessThan(dto.getEndDate());
			List<Ph2RealFruitsDataEntity> entities = this.ph2RealFruitsDataMapper.selectByExample(exm);
			// 引継ぎ元データがある場合
			if (0 < entities.size())
				{
				// 引継ぎ先データの一定日付前を削除
				exm = new Ph2RealFruitsDataEntityExample();
				exm.createCriteria().andDeviceIdEqualTo(deviceId).andYearEqualTo(dto.getYear())
						.andTargetDateLessThan(dto.getEndDate());
				this.ph2RealFruitsDataMapper.deleteByExample(exm);
				// 引継ぎデータの追加
				for (final Ph2RealFruitsDataEntity updateEnyity : entities)
					{
					updateEnyity.setDeviceId(deviceId);
					updateEnyity.setCreatedAt(new Timestamp(System.currentTimeMillis()));
					updateEnyity.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
					this.ph2RealFruitsDataMapper.insert(updateEnyity);
					}
				}
			}
// * 葉面積
			{
			// 引継ぎ元IDの該当年度のデータを取得する
			Ph2RealLeafShootsAreaEntityExample exm = new Ph2RealLeafShootsAreaEntityExample();
			exm.createCriteria().andDeviceIdEqualTo(dto.getPrevDeviceId()).andYearEqualTo(dto.getYear())
					.andTargetDateLessThan(dto.getEndDate());
			List<Ph2RealLeafShootsAreaEntity> entities = this.ph2RealLeafShootsAreaMapper.selectByExample(exm);
			// 引継ぎ元データがある場合
			if (0 < entities.size())
				{
				// 引継ぎ先データの一定日付前を削除
				exm = new Ph2RealLeafShootsAreaEntityExample();
				exm.createCriteria().andDeviceIdEqualTo(deviceId).andYearEqualTo(dto.getYear())
						.andTargetDateLessThan(dto.getEndDate());
				this.ph2RealLeafShootsAreaMapper.deleteByExample(exm);
				// 引継ぎデータの追加
				for (final Ph2RealLeafShootsAreaEntity updateEnyity : entities)
					{
					updateEnyity.setDeviceId(deviceId);
					updateEnyity.setCreatedAt(new Timestamp(System.currentTimeMillis()));
					updateEnyity.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
					this.ph2RealLeafShootsAreaMapper.insert(updateEnyity);
					}
				}
			}
// * 葉枚数
			{
			// 引継ぎ元IDの該当年度のデータを取得する
			Ph2RealLeafShootsCountEntityExample exm = new Ph2RealLeafShootsCountEntityExample();
			exm.createCriteria().andDeviceIdEqualTo(dto.getPrevDeviceId()).andYearEqualTo(dto.getYear())
					.andTargetDateLessThan(dto.getEndDate());
			List<Ph2RealLeafShootsCountEntity> entities = this.ph2RealLeafShootsCountMapper.selectByExample(exm);
			// 引継ぎ元データがある場合
			if (0 < entities.size())
				{
				// 引継ぎ先データの一定日付前を削除
				exm = new Ph2RealLeafShootsCountEntityExample();
				exm.createCriteria().andDeviceIdEqualTo(deviceId).andYearEqualTo(dto.getYear())
						.andTargetDateLessThan(dto.getEndDate());
				this.ph2RealLeafShootsCountMapper.deleteByExample(exm);
				// 引継ぎデータの追加
				for (final Ph2RealLeafShootsCountEntity updateEnyity : entities)
					{
					updateEnyity.setDeviceId(deviceId);
					updateEnyity.setCreatedAt(new Timestamp(System.currentTimeMillis()));
					updateEnyity.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
					this.ph2RealLeafShootsCountMapper.insert(updateEnyity);
					}
				}
			}
// * 光合成量
			{
			// 引継ぎ元IDの該当年度のデータを取得する
			Ph2RealPsAmountEntityExample exm = new Ph2RealPsAmountEntityExample();
			exm.createCriteria().andDeviceIdEqualTo(dto.getPrevDeviceId()).andYearEqualTo(dto.getYear())
					.andDateLessThan(dto.getEndDate());
			List<Ph2RealPsAmountEntity> entities = this.ph2RealPsAmountMapper.selectByExample(exm);
			// 引継ぎ元データがある場合
			if (0 < entities.size())
				{
				// 引継ぎ先データの一定日付前を削除
				exm = new Ph2RealPsAmountEntityExample();
				exm.createCriteria().andDeviceIdEqualTo(deviceId).andYearEqualTo(dto.getYear())
						.andDateLessThan(dto.getEndDate());
				this.ph2RealPsAmountMapper.deleteByExample(exm);
				// 引継ぎデータの追加
				for (final Ph2RealPsAmountEntity updateEnyity : entities)
					{
					updateEnyity.setDeviceId(deviceId);
					updateEnyity.setCreatedAt(new Timestamp(System.currentTimeMillis()));
					updateEnyity.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
					this.ph2RealPsAmountMapper.insert(updateEnyity);
					}
				}
			}
// F値テーブル
			{
			// 引継ぎ元IDの該当年度のデータを日付順に取得する
			Ph2RealGrowthFStageEntityExample exm = new Ph2RealGrowthFStageEntityExample();
			exm.createCriteria().andDeviceIdEqualTo(dto.getPrevDeviceId()).andYearEqualTo(dto.getYear());
			exm.setOrderByClause("stage_start");
			List<Ph2RealGrowthFStageEntity> oldEntities = this.ph2RealGrowthFStageMapper.selectByExample(exm);
			// 引継ぎ元データがある場合
			if (0 < oldEntities.size())
				{
				// 引継ぎ先IDの該当年度のデータを日付順に取得する
				Ph2RealGrowthFStageEntityExample exm2 = new Ph2RealGrowthFStageEntityExample();
				exm2.createCriteria().andDeviceIdEqualTo(deviceId).andYearEqualTo(dto.getYear());
				exm.setOrderByClause("stage_start");
				List<Ph2RealGrowthFStageEntity> newEntities = this.ph2RealGrowthFStageMapper.selectByExample(exm);
				// 引継ぎ先に実データがあるまで、引継ぎ先のF値テーブルを移動する
				int i = 0;
				for (; i < newEntities.size(); i++)
					{
					if (null != newEntities.get(i).getActualDate())
						{
						if (newEntities.get(i).getActualDate().getTime() >= dto.getEndDate().getTime()) break;
						}
					}

				// 引継ぎ先に実データがある期間名まで引継ぎ元データをマージ先に追加する
				List<Ph2RealGrowthFStageEntity> mergedEnyities = new ArrayList<>();
				Ph2RealGrowthFStageEntity seekEntity = (i < newEntities.size()) ? newEntities.get(i) : null;
				for (final Ph2RealGrowthFStageEntity oldEntity : oldEntities)
					{
					if (null != seekEntity)
						{
						if (oldEntity.getStageName().equals(seekEntity.getStageName())) break;
						}
					oldEntity.setDeviceId(deviceId);
					mergedEnyities.add(oldEntity);
					}
				// * 引継ぎ先データをマージ先に追加する
				if (null != seekEntity)
					{
					for (; i < newEntities.size(); i++)
						{
						mergedEnyities.add(newEntities.get(i));
						}
					}
				// インターバルを調整する
				double prevAccumulatedF = 0;
				for(final Ph2RealGrowthFStageEntity mergedEntity : mergedEnyities)
					{
					double accumulatedF = mergedEntity.getAccumulatedF();
					mergedEntity.setIntervalF(accumulatedF - prevAccumulatedF);
					prevAccumulatedF = accumulatedF;
					}
				// 引継ぎ先データを削除
				exm = new Ph2RealGrowthFStageEntityExample();
				exm.createCriteria().andDeviceIdEqualTo(deviceId).andYearEqualTo(dto.getYear());
				this.ph2RealGrowthFStageMapper.deleteByExample(exm);
				// 引継ぎデータの追加
				for (final Ph2RealGrowthFStageEntity updateEnyity : mergedEnyities)
					{
					updateEnyity.setId(null);
					updateEnyity.setCreatedAt(new Timestamp(System.currentTimeMillis()));
					updateEnyity.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
					this.ph2RealGrowthFStageMapper.insert(updateEnyity);
					}
				}
			}
		}
	}
