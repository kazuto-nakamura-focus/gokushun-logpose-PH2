package com.logpose.ph2.api.domain.leaf;

import java.sql.Timestamp;
import java.text.ParseException;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.logpose.ph2.api.dao.db.entity.Ph2DeviceDayEntity;
import com.logpose.ph2.api.dao.db.entity.Ph2ParamsetLeafAreaEntity;
import com.logpose.ph2.api.dao.db.entity.Ph2ParamsetLeafCountEntity;
import com.logpose.ph2.api.dao.db.entity.Ph2RealLeafShootsAreaEntity;
import com.logpose.ph2.api.dao.db.entity.Ph2RealLeafShootsAreaEntityExample;
import com.logpose.ph2.api.dao.db.entity.Ph2RealLeafShootsCountEntity;
import com.logpose.ph2.api.dao.db.entity.Ph2RealLeafShootsCountEntityExample;
import com.logpose.ph2.api.dao.db.mappers.Ph2ModelDataMapper;
import com.logpose.ph2.api.dao.db.mappers.Ph2ParamsetLeafAreaMapper;
import com.logpose.ph2.api.dao.db.mappers.Ph2ParamsetLeafCountMapper;
import com.logpose.ph2.api.dao.db.mappers.Ph2RealLeafShootsAreaMapper;
import com.logpose.ph2.api.dao.db.mappers.Ph2RealLeafShootsCountMapper;
import com.logpose.ph2.api.dao.db.mappers.joined.GrowthDomainMapper;
import com.logpose.ph2.api.domain.DeviceDayDomain;
import com.logpose.ph2.api.dto.DailyBaseDataDTO;
import com.logpose.ph2.api.dto.LeafParamSetDTO;
import com.logpose.ph2.api.master.ModelMaster;

@Component
public class LeafDomain extends LeafModelDataParameterAggregator
	{
	// ===============================================
	// クラスメンバー
	// ===============================================

	@Autowired
	private GrowthDomainMapper growthDomainMapper;

	@Autowired
	private Ph2RealLeafShootsCountMapper ph2RealLeafShootsCountMapper;

	@Autowired
	private Ph2RealLeafShootsAreaMapper ph2RealLeafShootsAreaMapper;

	@Autowired
	private Ph2ParamsetLeafAreaMapper ph2ParamsetLeafAreaMapper;

	@Autowired
	private Ph2ParamsetLeafCountMapper ph2ParamsetLeafCountMapper;

	@Autowired
	private DeviceDayDomain deviceDayDomain;

	@Autowired
	private Ph2ModelDataMapper ph2ModelDataMapper;

	// ===============================================
	// 公開メソッド
	// ===============================================
	// --------------------------------------------------
	/**
	 * デバイスのモデルテーブルを更新する
	 * 
	 * @param deviceId デバイスID
	 * @param year 年度
	 * @param startDate 統計対象開始日
	 * @throws ParseException 
	 */
	// --------------------------------------------------
	public void updateModelTable(Long deviceId, Short year, Date startDate)
			throws ParseException
		{
		if (null == year)
			{
			// * デバイスID、統計開始日から年度を取得。
			year = this.deviceDayDomain.getYear(deviceId, startDate);
			}

// * 統計対象開始日から存在しているDailyBaseDataの気温情報を取得
		List<DailyBaseDataDTO> realDayData = this.growthDomainMapper
				.selectDailyData(deviceId, year, null);
// * 日ごとデータがある場合
		if (0 != realDayData.size())
			{
// * データ生成のためのパラメータを作成する。
			LeafModelDataParameters parameters = new LeafModelDataParameters();
			super.setParameters(deviceId, year, parameters);
// * データの出力先の設定をする
			LeafModelDataExporter exporter = new LeafModelDataExporter(ph2ModelDataMapper);
// * データの生成を行う
			new LeafModelDataGenerator(parameters, exporter, realDayData);
			}
		}

	// --------------------------------------------------
	/**
	 *新梢辺り葉枚数・平均個葉面積検索処理
	 *
	 * @param deviceId
	 * @param date
	 */
	// --------------------------------------------------
	public List<Ph2RealLeafShootsAreaEntity> searchShooArea(Long deviceId,
			Date date, Ph2RealLeafShootsAreaEntityExample exm)
		{
		// * 既存の日付のものがあるか検索
		exm.createCriteria().andTargetDateEqualTo(date)
				.andDeviceIdEqualTo(deviceId);
		return this.ph2RealLeafShootsAreaMapper.selectByExample(exm);
		}

	// --------------------------------------------------
	/**
	 * 新梢数登録処理
	 *
	 * @param entity Ph2ParamsetLeafCountEntity
	 */
	// --------------------------------------------------
	public void addShootCount(Ph2RealLeafShootsCountEntity entity)
		{
		entity.setUpdatedAt(new Timestamp(System.currentTimeMillis()));

		Ph2RealLeafShootsCountEntityExample exm = new Ph2RealLeafShootsCountEntityExample();
		// * 既存の日付のものがあるか検索
		List<Ph2RealLeafShootsCountEntity> records = this.ph2RealLeafShootsCountMapper.selectByDate(
				entity.getDeviceId(), entity.getYear(), entity.getTargetDate());
		if (records.size() > 0)
			{
			entity.setCreatedAt(records.get(0).getCreatedAt());
			this.ph2RealLeafShootsCountMapper.updateByExample(entity, exm);
			}
		else
			{
			entity.setCreatedAt(new Timestamp(System.currentTimeMillis()));
			this.ph2RealLeafShootsCountMapper.insert(entity);
			}
		}

	// --------------------------------------------------
	/**
	 * 新梢辺り葉枚数・平均個葉面積登録処理
	 *
	 * @param dto LeafvaluesDTO
	 * @return 実測新梢辺り葉面積
	 */
	// --------------------------------------------------
	public void addAreaAndCount(Ph2RealLeafShootsAreaEntity entity)
		{
		entity.setUpdatedAt(new Timestamp(System.currentTimeMillis()));

		// * 既存の日付のものがあるか検索
		Ph2RealLeafShootsAreaEntityExample exm = new Ph2RealLeafShootsAreaEntityExample();
		exm.createCriteria().andTargetDateEqualTo(entity.getTargetDate())
				.andDeviceIdEqualTo(entity.getDeviceId());
		List<Ph2RealLeafShootsAreaEntity> records = this.ph2RealLeafShootsAreaMapper
				.selectByExample(exm);
		if (records.size() > 0)
			{
			entity.setCreatedAt(records.get(0).getCreatedAt());
			this.ph2RealLeafShootsAreaMapper.updateByExample(entity, exm);
			}
		else
			{
			entity.setCreatedAt(new Timestamp(System.currentTimeMillis()));
			this.ph2RealLeafShootsAreaMapper.insert(entity);
			}
		}

	// --------------------------------------------------
	/**
	 * 葉面積・葉枚数パラメータセット更新
	 *
	 * @param LeafParamSetDTO 更新データ
	 */
	// --------------------------------------------------
	public void updateParamSet(LeafParamSetDTO dto)
		{
		parameterSetDomain.update(dto, ModelMaster.LEAF);

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
		}

	// --------------------------------------------------
	/**
	 * デフォルト値の設定
	 *
	 * @param paramId パラメータセットID
	 * @throws ParseException 
	 */
	// --------------------------------------------------
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
// * 年度の最初の日を取得
		Ph2DeviceDayEntity deviceDay = this.deviceDayDomain.getFirstDay(deviceId, year);
// * テーブルをデフォルトパラメータで更新
		this.updateModelTable(deviceId, year, deviceDay.getDate());
		}

	// --------------------------------------------------
	/**
	 * 新梢数検索処理(日付指定無し)
	 *
	 * @param deviceId
	 * @param year
	 */
	// --------------------------------------------------
	public Ph2RealLeafShootsCountEntity searchShootCount(
			Long deviceId, Short year)
		{
		List<Ph2RealLeafShootsCountEntity> entities = this.ph2RealLeafShootsCountMapper
				.selectByDeviceId(deviceId, year);
		return (entities.size() != 0) ? entities.get(0) : null;
		}

	// --------------------------------------------------
	/**
	 * 新梢数検索処理(日付指定あり)
	 *
	 * @param deviceId
	 * @param year
	 */
	// --------------------------------------------------
	public Ph2RealLeafShootsCountEntity searchShootCountByDate(
			Long deviceId, Short year, Date date)
		{
		List<Ph2RealLeafShootsCountEntity> entities = this.ph2RealLeafShootsCountMapper
				.selectByDate(deviceId, year, date);
		return (entities.size() != 0) ? entities.get(0) : null;
		}

	// --------------------------------------------------
	/**
	 * 葉面積・葉枚数検索処理
	 *
	 * @param deviceId
	 * @param year
	 */
	// --------------------------------------------------
	public List<Ph2RealLeafShootsAreaEntity> searchShootArea(
			Long deviceId, Short year)
		{
		return this.ph2RealLeafShootsAreaMapper.selectByDeviceId(deviceId, year);
		}

	// --------------------------------------------------
	/**
	 * 葉面積・葉枚数検索処理(日付指定あり)
	 *
	 * @param deviceId
	 * @param year
	 */
	// --------------------------------------------------
	public Ph2RealLeafShootsAreaEntity searchShootAreaByDate(
			Long deviceId, Short year, Date date)
		{
		List<Ph2RealLeafShootsAreaEntity> entities = this.ph2RealLeafShootsAreaMapper
				.selectByDate(deviceId, year, date);
		return (entities.size() != 0) ? entities.get(0) : null;
		}
	}
