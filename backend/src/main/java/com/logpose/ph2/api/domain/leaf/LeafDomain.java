package com.logpose.ph2.api.domain.leaf;

import java.sql.Timestamp;
import java.text.ParseException;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.logpose.ph2.api.algorythm.DateTimeUtility;
import com.logpose.ph2.api.algorythm.DeviceDayAlgorithm;
import com.logpose.ph2.api.dao.db.entity.Ph2RealLeafShootsAreaEntity;
import com.logpose.ph2.api.dao.db.entity.Ph2RealLeafShootsAreaEntityExample;
import com.logpose.ph2.api.dao.db.entity.Ph2RealLeafShootsCountEntity;
import com.logpose.ph2.api.dao.db.mappers.Ph2ModelDataMapper;
import com.logpose.ph2.api.dao.db.mappers.Ph2RealLeafShootsAreaMapper;
import com.logpose.ph2.api.dao.db.mappers.Ph2RealLeafShootsCountMapper;
import com.logpose.ph2.api.dto.leaf.LeafAreaValueDTO;

@Component
public class LeafDomain
	{
	// ===============================================
	// クラスメンバー
	// ===============================================
	@Autowired
	private Ph2ModelDataMapper modelDataMapper;
	@Autowired
	private Ph2RealLeafShootsCountMapper ph2RealLeafShootsCountMapper;
	@Autowired
	private Ph2RealLeafShootsAreaMapper ph2RealLeafShootsAreaMapper;

	// ===============================================
	// 公開関数群
	// ===============================================
	// ###############################################
	/**
	 * 新梢数登録処理
	 *
	 * @param entity Ph2ParamsetLeafCountEntity
	 * @return 新梢数
	 */
	// ###############################################
	public short addShootCount(Ph2RealLeafShootsCountEntity entity)
		{
		entity.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
// * 既存のものがあるか検索
		Ph2RealLeafShootsCountEntity old = this.ph2RealLeafShootsCountMapper.selectByPrimaryKey(entity.getDeviceId(),
				entity.getYear());
// * 既存のものがあれば、更新をする
		if (null != old)
			{
			entity.setCreatedAt(old.getCreatedAt());
			this.ph2RealLeafShootsCountMapper.updateByPrimaryKey(entity);
			}
		else
			{
			entity.setCreatedAt(new Timestamp(System.currentTimeMillis()));
			this.ph2RealLeafShootsCountMapper.insert(entity);
			}
		return entity.getCount().shortValue();
// * モデルテーブルを更新する
//		this.updateModelTable(entity.getDeviceId(), entity.getYear(), entity.getCount().shortValue(), null, null);
		}
	// ###############################################
	/**
	 * 新梢数検索処理(日付指定無し)
	 *
	 * @param deviceId デバイスID
	 * @param year 年度
	 * @return Ph2RealLeafShootsCountEntity
	 */
	// ###############################################
	public Ph2RealLeafShootsCountEntity searchShootCount(
			Long deviceId, Short year)
		{
		List<Ph2RealLeafShootsCountEntity> entities = this.ph2RealLeafShootsCountMapper
				.selectByDeviceId(deviceId, year);
		return (entities.size() != 0) ? entities.get(0) : null;
		}

	// ###############################################
	/**
	 * 新梢数検索処理(日付指定あり)
	 *
	 * @param deviceId デバイスID
	 * @param year 年度
	 * @param date 日付
	 * @return Ph2RealLeafShootsCountEntity
	 */
	// ###############################################
	public Ph2RealLeafShootsCountEntity searchShootCountByDate(
			Long deviceId, Short year, Date date)
		{
		List<Ph2RealLeafShootsCountEntity> entities = this.ph2RealLeafShootsCountMapper
				.selectByDate(deviceId, year, date);
		return (entities.size() != 0) ? entities.get(0) : null;
		}

	// ###############################################
	/**
	 * モデル値取得処理
	 * 
	 * @param deviceId デバイスID
	 * @param year 年度
	 * @param date 日付
	 * @return モデル値
	 */
	// ###############################################
	public Double getModelValue(Long deviceId, Short year, Date date)
		{
		date = new DeviceDayAlgorithm().getTimeZero(date);
		return this.modelDataMapper.selectCrownLeafAreaBySpecificDate(deviceId, year, date);
		}

	// ###############################################
	/**
	 * 新梢辺り葉枚数・平均個葉面積登録処理
	 * 
	 * @param deviceId デバイスID
	 * @param year 年度
	 * @param values List<LeafAreaValuesDTO>
	 * @throws ParseException
	 */
	// ###############################################
	public void addAreaAndCount(Long deviceId, Short year, List<LeafAreaValueDTO> values) throws ParseException
		{
		Timestamp time = new Timestamp(System.currentTimeMillis());
		for (final LeafAreaValueDTO dto : values)
			{
			Date tmp = DateTimeUtility.getDateFromString(dto.getDate());
// * 該当するデバイスと日付を検索
			Ph2RealLeafShootsAreaEntity entity = this.ph2RealLeafShootsAreaMapper.selectByPrimaryKey(deviceId, tmp);
// * 存在しない場合、新たに作成
			if (null == entity)
				{
				entity = new Ph2RealLeafShootsAreaEntity();
				entity.setDeviceId(deviceId);
				entity.setTargetDate(tmp);
				}
// * 値の設定
			entity.setCount(dto.getCount());
			entity.setAverageArea(dto.getAverageArea());
			entity.setRealArea(dto.getTotalArea());
			entity.setYear(year);
			entity.setUpdatedAt(time);
// * データの更新時
			if (null != entity.getCreatedAt())
				{
				this.ph2RealLeafShootsAreaMapper.updateByPrimaryKey(entity);
				}
			else
				{
				entity.setCreatedAt(time);
				this.ph2RealLeafShootsAreaMapper.insert(entity);
				}
			}
// * 古いデータの削除
		if (values.size() > 0)
			{
			Ph2RealLeafShootsAreaEntityExample exm = new Ph2RealLeafShootsAreaEntityExample();
			exm.createCriteria()
					.andDeviceIdEqualTo(deviceId)
					.andYearEqualTo(year)
					.andUpdatedAtLessThan(time);
			this.ph2RealLeafShootsAreaMapper.deleteByExample(exm);
			}
		}



	// ###############################################
	/**
	 * 葉面積・葉枚数検索処理
	 * 
	 * @param deviceId
	 * @param year
	 * @return List<LeafAreaValueDTO>
	 */
	// ###############################################
	public List<LeafAreaValueDTO> searchShootAreaAll(Long deviceId, Short year)
		{
		return this.ph2RealLeafShootsAreaMapper.selectByDeviceYear(deviceId, year);
		}

	// ###############################################
	/**
	 * 葉面積・葉枚数検索処理(日付指定あり)
	 *
	 * @param deviceId デバイスID
	 * @param year 年度
	 * @param date 日付
	 * @return Ph2RealLeafShootsAreaEntity
	 */
	// ###############################################
	public Ph2RealLeafShootsAreaEntity searchShootAreaByDate(
			Long deviceId, Short year, Date date)
		{
		List<Ph2RealLeafShootsAreaEntity> entities = this.ph2RealLeafShootsAreaMapper
				.selectByDate(deviceId, year, date);
		return (entities.size() != 0) ? entities.get(0) : null;
		}
	}
