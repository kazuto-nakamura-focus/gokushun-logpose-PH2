package com.logpose.ph2.api.domain;

import java.sql.Timestamp;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.logpose.ph2.api.algorythm.DailyDataAlgorythm;
import com.logpose.ph2.api.configration.DefaultLeafAreaParameters;
import com.logpose.ph2.api.configration.DefaultLeafCountParameters;
import com.logpose.ph2.api.dao.db.entity.Ph2DeviceDayEntity;
import com.logpose.ph2.api.dao.db.entity.Ph2ParamsetCatalogEntity;
import com.logpose.ph2.api.dao.db.entity.Ph2ParamsetLeafAreaEntity;
import com.logpose.ph2.api.dao.db.entity.Ph2ParamsetLeafCountEntity;
import com.logpose.ph2.api.dao.db.entity.Ph2RealLeafShootsAreaEntity;
import com.logpose.ph2.api.dao.db.entity.Ph2RealLeafShootsAreaEntityExample;
import com.logpose.ph2.api.dao.db.entity.Ph2RealLeafShootsCountEntity;
import com.logpose.ph2.api.dao.db.entity.Ph2RealLeafShootsCountEntityExample;
import com.logpose.ph2.api.dao.db.entity.joined.ModelDataEntity;
import com.logpose.ph2.api.dao.db.mappers.Ph2ModelDataMapper;
import com.logpose.ph2.api.dao.db.mappers.Ph2ParamsetLeafAreaMapper;
import com.logpose.ph2.api.dao.db.mappers.Ph2ParamsetLeafCountMapper;
import com.logpose.ph2.api.dao.db.mappers.Ph2RealLeafShootsAreaMapper;
import com.logpose.ph2.api.dao.db.mappers.Ph2RealLeafShootsCountMapper;
import com.logpose.ph2.api.dao.db.mappers.joined.GrowthDomainMapper;
import com.logpose.ph2.api.dao.db.mappers.joined.LeafDomainMapper;
import com.logpose.ph2.api.dto.DailyBaseDataDTO;
import com.logpose.ph2.api.dto.LeafParamSetDTO;
import com.logpose.ph2.api.dto.RealModelGraphDataDTO;
import com.logpose.ph2.api.master.ModelMaster;
import com.logpose.ph2.api.utility.DateTimeUtility;

@Component
public class LeafDomain extends GraphDomain
	{
	// ===============================================
	// クラスメンバー
	// ===============================================
	@Autowired
	private DailyDataAlgorythm dailyDataAlgorythm;

	@Autowired
	ParameterSetDomain parameterSetDomain;

	@Autowired
	private DefaultLeafCountParameters defaultLeafCountParameters;

	@Autowired
	private DefaultLeafAreaParameters defaultLeafAreaParameters;

	@Autowired
	private Ph2RealLeafShootsCountMapper ph2RealLeafShootsCountMapper;

	@Autowired
	private Ph2RealLeafShootsAreaMapper ph2RealLeafShootsAreaMapper;

	@Autowired
	private Ph2ParamsetLeafAreaMapper ph2ParamsetLeafAreaMapper;

	@Autowired
	private Ph2ParamsetLeafCountMapper ph2ParamsetLeafCountMapper;

	@Autowired
	private GrowthDomainMapper growthDomainMapper;
	@Autowired
	private LeafDomainMapper leafDomainMapper;
	@Autowired
	private DeviceDayDomain deviceDayDomain;

	@Autowired
	private Ph2ModelDataMapper ph2ModelDataMapper;

	// --------------------------------------------------
	/**
	 * パラメータセットをデフォルトフラグがあるもので、近い年から取得する。
	 * 存在しない場合はデフォルト値を設定して返却
	 * 
	 * @param deviceId-デバイスID
	 * @param year-対象年度
	 * @return Ph2ParamsetGrowthEntity
	 * @throws ParseException 
	 */
	// --------------------------------------------------
	public LeafParamSetDTO getParmaters(Long deviceId, Short year)
	// throws ParseException
		{
		// * 該当するパラメータセットのカタログを取得
		List<Ph2ParamsetCatalogEntity> params = parameterSetDomain.getParamSetCatalogsByYear(
				2, deviceId, year);
		// * 該当するパラメータセットが存在する場合
		if ((0 < params.size()) && (year.intValue() == params.get(0).getYear().intValue()))
			{
			return this.getDetail(params.get(0).getId());
			}
		// * 存在しない場合、パラメータセット・カタログに履歴を付与して更新する
		Long paramId = parameterSetDomain.addCatalogData(2, deviceId, year);
		// * 最近のパラメータセット・カタログがある場合
		if (0 < params.size())
			{
			LeafParamSetDTO dto = this.getDetail(params.get(0).getId());
			this.addParamSet(paramId, dto);
			}
		// * 全く新規の場合
		else
			{
			Ph2ParamsetLeafAreaEntity area = new Ph2ParamsetLeafAreaEntity();
			area.setParamsetId(paramId);
			area.setCreatedAt(new Timestamp(System.currentTimeMillis()));
			area.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
			area.setValueA(this.defaultLeafAreaParameters.getA());
			area.setValueB(this.defaultLeafAreaParameters.getB());
			area.setValueC(this.defaultLeafAreaParameters.getC());
			this.ph2ParamsetLeafAreaMapper.insert(area);

			Ph2ParamsetLeafCountEntity count = new Ph2ParamsetLeafCountEntity();
			count.setParamsetId(paramId);
			count.setCreatedAt(new Timestamp(System.currentTimeMillis()));
			count.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
			count.setValueC(defaultLeafCountParameters.getC());
			count.setValueD(defaultLeafCountParameters.getD());
			this.ph2ParamsetLeafCountMapper.insert(count);
			}
		return this.getDetail(paramId);
		}

	// --------------------------------------------------
	/**
	 * 対象デバイスの対象年度の新梢数を取得する。
	 * 存在しない場合はデフォルト値を設定して返却
	 * 
	 * @param deviceId-デバイスID
	 * @param year-対象年度
	 * @return 新梢数
	 * @throws ParseException 
	 */
	// --------------------------------------------------
	public int getShootCount(Long deviceId, Short year)
		{
		Ph2RealLeafShootsCountEntityExample exm = new Ph2RealLeafShootsCountEntityExample();
		exm.createCriteria().andDeviceIdEqualTo(deviceId)
				.andYearEqualTo(year);
		List<Ph2RealLeafShootsCountEntity> result = this.ph2RealLeafShootsCountMapper
				.selectByExample(exm);
		if (result.size() > 0)
			{
			return result.get(0).getCount();
			}
		else
			{
			return this.defaultLeafCountParameters.getCount();
			}
		}

	// --------------------------------------------------
	/**
	 * モデルデータテーブルから葉面積グラフデータを作成する
	 * 
	 * @param deviceId 対象デバイスID
	 * @param year 対象年度
	 * @param startDate 統計開始日
	 * @throws ParseException 
	 */
	// -------------------------------------------------
	public LeafGraphDataModel getModelGraphData(
			Long deviceId, Short year, Date startDate)
			throws ParseException
		{
		// * 新梢数
		int shootCount = this.getShootCount(deviceId, year);
		// * パラメータ
		LeafParamSetDTO param = this.getParmaters(deviceId, year);
		// * 統計対象開始日から存在しているDailyBaseDataの気温情報を取得
		List<DailyBaseDataDTO> realDayData = this.dailyDataAlgorythm
				.getDailyBaseData(deviceId, year);
		// * 萌芽日
		List<Integer> rec = this.leafDomainMapper.getSproutDay(4, deviceId, year);
		int lapseDay = rec.get(0);
		// * 計算処理を実行
		return new LeafGraphDataModel(param, lapseDay, shootCount, realDayData);
		}

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
		LeafGraphDataModel model = this.getModelGraphData(deviceId, year, startDate);
		this.deviceDayDomain.updateModelData(deviceId, year, model);
		}

	// --------------------------------------------------
	/**
	 * 葉面積モデルグラフデータ取得
	 *    モデルテーブルから検索してデータを取得する
	 *
	 * @param deviceId デバイスID
	 * @param year 対象年度
	 * @return GraphDataDTO
	 * @throws ParseException 
	 */
	// --------------------------------------------------
	public List<RealModelGraphDataDTO> getModelGraph(Long deviceId, Short year)
			throws ParseException
		{
		RealModelGraphDataDTO areaModel = new RealModelGraphDataDTO();
		RealModelGraphDataDTO countModel = new RealModelGraphDataDTO();

		List<ModelDataEntity> entites = this.ph2ModelDataMapper
				.selectModelDataByType(deviceId, year);
		List<Double> values = new ArrayList<>();
		List<Double> predictValues = new ArrayList<>();

		Double minArea = Double.MAX_VALUE;
		Double maxArea = Double.MIN_VALUE;
		Double minCount = Double.MAX_VALUE;
		Double maxCount = Double.MIN_VALUE;
		for (ModelDataEntity entity : entites)
			{
			if (entity.getIsReal())
				{
				areaModel.getValues().add(entity.getCrownLeafArea());
				countModel.getValues().add(entity.getLeafCount());
				}
			else
				{
				areaModel.getPredictValues().add(entity.getCrownLeafArea());
				countModel.getPredictValues().add(entity.getLeafCount());
				}
			double area = (null == entity.getCrownLeafArea()) ? 0 : entity.getCrownLeafArea();
			if (minArea > area)
				minArea = area;
			if (maxArea < area)
				maxArea = area;
			double count = (null == entity.getLeafCount()) ? 0 : entity.getLeafCount();
			if (minCount > count)
				minCount = count;
			if (maxCount < count)
				maxCount = count;
			}
		// * 最小値・最大値の設定
		ModelDataEntity first = entites.get(0);
		ModelDataEntity last = entites.get(entites.size() - 1);
		areaModel
				.setXStart(DateTimeUtility.getStringFromDate(first.getDate()));
		areaModel.setXEnd(DateTimeUtility.getStringFromDate(last.getDate()));
		countModel.setXStart(areaModel.getXStart());
		countModel.setXEnd(areaModel.getXEnd());

		areaModel.setYStart(minArea);
		areaModel.setYEnd(maxArea);
		super.setComment(deviceId, year, areaModel);
		countModel.setYStart(minCount);
		countModel.setYEnd(maxCount);
		super.setComment(deviceId, year, countModel);
		// * 値の設定
		List<RealModelGraphDataDTO> resultData = new ArrayList<>();
		resultData.add(areaModel);
		resultData.add(countModel);
		return resultData;
		}

	// --------------------------------------------------
	/**
	 * 新梢数検索処理
	 *
	 * @param deviceId
	 * @param date
	 */
	// --------------------------------------------------
	public List<Ph2RealLeafShootsCountEntity> searchShootCount(Long deviceId,
			Date date, Ph2RealLeafShootsCountEntityExample exm)
		{
		// * 既存の日付のものがあるか検索
		exm.createCriteria().andTargetDateEqualTo(date)
				.andDeviceIdEqualTo(deviceId);
		return this.ph2RealLeafShootsCountMapper.selectByExample(exm);
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
		Ph2RealLeafShootsCountEntityExample exm = new Ph2RealLeafShootsCountEntityExample();
		// * 既存の日付のものがあるか検索
		List<Ph2RealLeafShootsCountEntity> records = this.searchShootCount(
				entity.getDeviceId(), entity.getTargetDate(), exm);
		if (records.size() > 0)
			{
			Ph2RealLeafShootsCountEntity target = records.get(0);
			target.setCount(entity.getCount());
			target.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
			this.ph2RealLeafShootsCountMapper.updateByExample(target, exm);
			}
		else
			{
			entity.setCreatedAt(new Timestamp(System.currentTimeMillis()));
			entity.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
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
		// * 既存の日付のものがあるか検索
		Ph2RealLeafShootsAreaEntityExample exm = new Ph2RealLeafShootsAreaEntityExample();
		exm.createCriteria().andTargetDateEqualTo(entity.getTargetDate())
				.andDeviceIdEqualTo(entity.getDeviceId());
		List<Ph2RealLeafShootsAreaEntity> records = this.ph2RealLeafShootsAreaMapper
				.selectByExample(exm);
		if (records.size() > 0)
			{
			Ph2RealLeafShootsAreaEntity target = records.get(0);
			target.setCount(entity.getCount());
			target.setAverageArea(entity.getAverageArea());
			target.setRealArea(entity.getRealArea());
			target.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
			this.ph2RealLeafShootsAreaMapper.updateByExample(target, exm);
			}
		else
			{
			entity.setCreatedAt(new Timestamp(System.currentTimeMillis()));
			entity.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
			this.ph2RealLeafShootsAreaMapper.insert(entity);
			}
		}

	// --------------------------------------------------
	/**
	 * 葉面積・葉枚数パラメータセット詳細取得
	 *
	 * @param paramSetId パラメータセットID
	 * @return LeafParamSetDTO
	 */
	// --------------------------------------------------
	public LeafParamSetDTO getDetail(Long paramSetId)
		{
		LeafParamSetDTO result = new LeafParamSetDTO();

		parameterSetDomain.fetchDetail(paramSetId, result);
		Ph2ParamsetLeafAreaEntity area = this.ph2ParamsetLeafAreaMapper
				.selectByPrimaryKey(paramSetId);
		Ph2ParamsetLeafCountEntity count = this.ph2ParamsetLeafCountMapper
				.selectByPrimaryKey(paramSetId);

		result.setAreaA(area.getValueA());
		result.setAreaB(area.getValueB());
		result.setAreaC(area.getValueC());
		result.setCountC(count.getValueC());
		result.setCountD(count.getValueD());

		return result;
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
	 * 葉面積・葉枚数パラメータセット追加
	 *
	 * @param LeafParamSetDTO 更新データ
	 * @return 
	 */
	// --------------------------------------------------
	public Long addParamSet(Long id, LeafParamSetDTO dto)
		{
		if (null == id)
			{
			// * 追加時、ここではデフォルトフラグはfalseとする。
			dto.setDefaultFlg(false);
			id = parameterSetDomain.add(dto, ModelMaster.LEAF);
			}
		Ph2ParamsetLeafAreaEntity area = new Ph2ParamsetLeafAreaEntity();
		area.setParamsetId(id);
		area.setCreatedAt(new Timestamp(System.currentTimeMillis()));
		area.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
		area.setValueA(dto.getAreaA());
		area.setValueB(dto.getAreaB());
		area.setValueC(dto.getAreaC());
		this.ph2ParamsetLeafAreaMapper.insert(area);

		Ph2ParamsetLeafCountEntity count = new Ph2ParamsetLeafCountEntity();
		count.setParamsetId(id);
		count.setCreatedAt(new Timestamp(System.currentTimeMillis()));
		count.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
		count.setValueC(dto.getCountC());
		count.setValueD(dto.getCountD());
		this.ph2ParamsetLeafCountMapper.insert(count);

		return id;
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
		LeafParamSetDTO paramInfo = this.getDetail(paramId);
		// * 同じ年度・デバイスの場合
		if((paramInfo.getDeviceId().longValue() != deviceId.longValue())
				||(paramInfo.getYear().shortValue() != year.shortValue()) )
			{
			paramInfo.setDeviceId(deviceId);
			paramInfo.setYear(year);
			paramId = this.addParamSet(null, paramInfo);
			}
		parameterSetDomain.setDefautParamSet(ModelMaster.LEAF, deviceId, year, paramId);
		//  年度の最初の日を取得
		Ph2DeviceDayEntity deviceDay = this.deviceDayDomain.getFirstDay(deviceId, year);
		this.updateModelTable(deviceId, year, deviceDay.getDate());
		}
	}
