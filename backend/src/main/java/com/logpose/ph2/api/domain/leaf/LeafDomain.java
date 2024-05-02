package com.logpose.ph2.api.domain.leaf;

import java.sql.Timestamp;
import java.text.ParseException;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.logpose.ph2.api.algorythm.DateTimeUtility;
import com.logpose.ph2.api.algorythm.DeviceDayAlgorithm;
import com.logpose.ph2.api.dao.db.entity.Ph2ParamsetLeafAreaEntity;
import com.logpose.ph2.api.dao.db.entity.Ph2ParamsetLeafCountEntity;
import com.logpose.ph2.api.dao.db.entity.Ph2RealLeafShootsAreaEntity;
import com.logpose.ph2.api.dao.db.entity.Ph2RealLeafShootsAreaEntityExample;
import com.logpose.ph2.api.dao.db.entity.Ph2RealLeafShootsCountEntity;
import com.logpose.ph2.api.dao.db.entity.Ph2WibleMasterEntity;
import com.logpose.ph2.api.dao.db.entity.Ph2WibleMasterEntityExample;
import com.logpose.ph2.api.dao.db.entity.joined.ModelAndDailyDataEntity;
import com.logpose.ph2.api.dao.db.mappers.Ph2ModelDataMapper;
import com.logpose.ph2.api.dao.db.mappers.Ph2RealLeafShootsAreaMapper;
import com.logpose.ph2.api.dao.db.mappers.Ph2WibleMasterMapper;
import com.logpose.ph2.api.domain.ModelAndDailyDataDomain;
import com.logpose.ph2.api.domain.applied_model.AppliedModel;
import com.logpose.ph2.api.dto.LeafParamSetDTO;
import com.logpose.ph2.api.dto.leaf.LeafAreaValueDTO;
import com.logpose.ph2.api.master.ModelMaster;

@Component
public class LeafDomain extends LeafModelDataParameterAggregator
	{
	// ===============================================
	// クラスメンバー
	// ===============================================
	@Autowired
	private AppliedModel appliedModel;
	@Autowired
	private Ph2ModelDataMapper modelDataMapper;
	@Autowired
	private ModelAndDailyDataDomain modelDataDomain;
	@Autowired
	private Ph2WibleMasterMapper ph2WibleMasterMapper;
	@Autowired
	protected Ph2RealLeafShootsAreaMapper ph2RealLeafShootsAreaMapper;

	// ===============================================
	// 公開関数群
	// ===============================================
	// ###############################################
	/**
	 * デバイスのモデルテーブルを更新する
	 * 
	 * @param deviceId デバイスID
	 * @param year 年度
	 * @throws ParseException 
	 */
	// ###############################################
	public void updateModelTable(Long deviceId, Short year) throws ParseException
		{
		this.updateModelTable(deviceId, year, null, null, null);
		}

	// ###############################################
	/**
	 * デバイスのモデルテーブルを更新する
	 * 
	 * @param deviceId デバイスID
	 * @param year 年度
	 * @param shootCount 新梢数
	 * @param sproutDay 萌芽日
	 * @param parameters パラメータセットの値群
	 * @throws ParseException
	 */
	// ###############################################
	public void updateModelTable(Long deviceId, Short year, Short shootCount, Short sproutDay,
			LeafParamSetDTO parameters)
			throws ParseException
		{
// * 統計対象開始日から存在しているDailyBaseDataの気温情報を取得
		List<ModelAndDailyDataEntity> realDayData = this.modelDataDomain.get(deviceId, year);

// * 日ごとデータがある場合
		if (0 != realDayData.size())
			{
// * 葉面積パラメータセットの取得
			if (null == parameters) parameters = this.getParmaters(deviceId, year);

// * 萌芽日の取得
			if (null == sproutDay)
				{
				sproutDay = this.appliedModel.getSproutDay(deviceId, year);
				}

// * 新梢数が無い場合は取得する
			if (null == shootCount) shootCount = (short) super.getShootCount(deviceId, year);

// * ワイブル分布の取得
			List<Ph2WibleMasterEntity> wibles = this.ph2WibleMasterMapper
					.selectByExample(new Ph2WibleMasterEntityExample());

// * データの生成を行う
			new LeafModelDataGenerator(sproutDay, shootCount, parameters, wibles, realDayData);

// * DBへの更新を行う
			this.modelDataDomain.upate(realDayData);
			}
		}

	// ###############################################
	/**
	 * 新梢数登録処理
	 *
	 * @param entity Ph2ParamsetLeafCountEntity
	 * @throws ParseException 
	 */
	// ###############################################
	public void addShootCount(Ph2RealLeafShootsCountEntity entity) throws ParseException
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
// * モデルテーブルを更新する
		this.updateModelTable(entity.getDeviceId(), entity.getYear(), entity.getCount().shortValue(), null, null);
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
	 * 葉面積・葉枚数パラメータセット更新
	 *
	 * @param LeafParamSetDTO 更新データ
	 * @return 更新されたパラメータセットがデフォルトかどうかのフラグ
	 * @throws ParseException 
	 */
	// ###############################################
	public boolean updateParamSet(LeafParamSetDTO dto) throws ParseException
		{
		boolean isDeault = parameterSetDomain.update(dto, ModelMaster.LEAF);

		Ph2ParamsetLeafAreaEntity area = this.ph2ParamsetLeafAreaMapper
				.selectByPrimaryKey(dto.getId());
		area.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
		area.setValueA(dto.getAreaA());
		area.setValueB(dto.getAreaB());
		area.setValueC(dto.getAreaC());
		this.ph2ParamsetLeafAreaMapper.updateByPrimaryKey(area);

		Ph2ParamsetLeafCountEntity count = this.ph2ParamsetLeafCountMapper
				.selectByPrimaryKey(dto.getId());
		count.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
		count.setValueC(dto.getCountC());
		count.setValueD(dto.getCountD());
		this.ph2ParamsetLeafCountMapper.updateByPrimaryKey(count);

		// * デフォルト値の場合、モデルデータの更新を行う
		if (isDeault)
			{
			LeafParamSetDTO args = new LeafParamSetDTO();
			args.setAreaA(dto.getAreaA());
			args.setAreaB(dto.getAreaB());
			args.setAreaC(dto.getAreaC());
			args.setCountC(dto.getCountC());
			args.setCountD(dto.getCountD());
			// * モデルデータの更新
			this.updateModelTable(dto.getDeviceId(), dto.getYear(), null, null, args);
			}

		return isDeault;
		}

	// ###############################################
	/**
	 * デフォルト値の設定
	 * 
	 * @param deviceId デバイスID
	 * @param year 年度
	 * @param paramId パラメータセットID
	 * @throws ParseException 
	 */
	// ###############################################
	public void setDefault(Long deviceId, Short year, Long paramId)
			throws ParseException
		{
// * パラメータセットの詳細を取得する
		LeafParamSetDTO paramInfo = super.getDetail(paramId);
// * 同じ年度・デバイスの場合
		if ((paramInfo.getDeviceId().longValue() != deviceId.longValue())
				|| (paramInfo.getYear().shortValue() != year.shortValue()))
			{
			paramInfo.setDeviceId(deviceId);
			paramInfo.setYear(year);
			paramId = super.addParamSet(null, paramInfo);
			}
		parameterSetDomain.setDefautParamSet(ModelMaster.LEAF, deviceId, year,
				paramId);
// * デバイスのモデルテーブルを更新する
		this.updateModelTable(deviceId, year, null, null, null);
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
