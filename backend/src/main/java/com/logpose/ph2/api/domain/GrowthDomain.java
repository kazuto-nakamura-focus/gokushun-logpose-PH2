package com.logpose.ph2.api.domain;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.logpose.ph2.api.algorythm.DeviceDayAlgorithm;
import com.logpose.ph2.api.configration.DefaultFtageValues;
import com.logpose.ph2.api.configration.DefaultGrowthParameters;
import com.logpose.ph2.api.dao.db.entity.Ph2DeviceDayEntity;
import com.logpose.ph2.api.dao.db.entity.Ph2ParamsetCatalogEntity;
import com.logpose.ph2.api.dao.db.entity.Ph2ParamsetGrowthEntity;
import com.logpose.ph2.api.dao.db.entity.Ph2RealGrowthFStageEntity;
import com.logpose.ph2.api.dao.db.entity.Ph2RealGrowthFStageEntityExample;
import com.logpose.ph2.api.dao.db.entity.joined.ModelDataEntity;
import com.logpose.ph2.api.dao.db.mappers.Ph2ModelDataMapper;
import com.logpose.ph2.api.dao.db.mappers.Ph2ParamsetGrowthMapper;
import com.logpose.ph2.api.dao.db.mappers.Ph2RealGrowthFStageMapper;
import com.logpose.ph2.api.dao.db.mappers.joined.GrowthDomainMapper;
import com.logpose.ph2.api.dto.DailyBaseDataDTO;
import com.logpose.ph2.api.dto.EventDaysDTO;
import com.logpose.ph2.api.dto.FDataListDTO;
import com.logpose.ph2.api.dto.GrowthParamSetDTO;
import com.logpose.ph2.api.dto.RealModelGraphDataDTO;
import com.logpose.ph2.api.dto.ValueDateDTO;
import com.logpose.ph2.api.master.ModelMaster;

@Component
public class GrowthDomain extends GraphDomain
	{
	// ===============================================
	// クラスメンバー
	// ===============================================
	@Autowired
	private ParameterSetDomain parameterSetDomain;
	@Autowired
	private DefaultGrowthParameters parameters;
	@Autowired
	private DefaultFtageValues fstageValues;
	@Autowired
	private Ph2ModelDataMapper ph2ModelDataMapper;
	@Autowired
	private Ph2RealGrowthFStageMapper ph2RealGrowthFStageMapper;
	@Autowired
	private Ph2ParamsetGrowthMapper ph2ParamsetGrowthMapper;
	@Autowired
	private GrowthDomainMapper growthDomainMapper;
	/*
	 * @Autowired
	 * private Ph2FDataMapper ph2FDataMapper;
	 */
	@Autowired
	private DeviceDayDomain deviceDayDomain;

	// ===============================================
	// 公開関数群
	// ===============================================
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
	public Ph2ParamsetGrowthEntity getParmaters(Long deviceId, Short year)
	// throws ParseException
		{
		// * 該当するパラメータセットのカタログを取得
		List<Ph2ParamsetCatalogEntity> params = parameterSetDomain
				.getParamSetCatalogsByYear(
						1, deviceId, year);
		// * 該当するパラメータセットが存在する場合
		if ((0 < params.size()) && (year.intValue() == params.get(0).getYear().intValue()))
			{
			return this.ph2ParamsetGrowthMapper
					.selectByPrimaryKey(params.get(0).getId());
			}
		// * 存在しない場合、パラメータセット・カタログに履歴を付与して更新する
		Long paramId = parameterSetDomain.addCatalogData(1, deviceId, year);
		// * 最近のパラメータセット・カタログがある場合
		Ph2ParamsetGrowthEntity entity;
		if (0 < params.size())
			{
			GrowthParamSetDTO dto = this.getDetail(params.get(0).getId());
			this.addParamSet(paramId, dto);
			entity = this.ph2ParamsetGrowthMapper.selectByPrimaryKey(paramId);
			}
		// * 全く新規の場合
		else
			{
			entity = new Ph2ParamsetGrowthEntity();
			entity.setParamsetId(paramId);
			entity.setCreatedAt(new Timestamp(System.currentTimeMillis()));
			entity.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
			entity.setAfterD(parameters.getAd());
			entity.setAfterE(parameters.getAe());
			entity.setBeforeD(parameters.getBd());
			entity.setBeforeE(parameters.getBe());
			this.ph2ParamsetGrowthMapper.insert(entity);
			}
		return entity;
		}

	// --------------------------------------------------
	/**
	 * 対象デバイスの対象年度のFデータを取得する。
	 * 存在しない場合はデフォルト値を設定して返却
	 * 
	 * @param deviceId-デバイスID
	 * @param year-対象年度
	 * @return List<Ph2RealGrowthFStageEntity>
	 * @throws ParseException 
	 */
	// --------------------------------------------------
	public List<Ph2RealGrowthFStageEntity> getFStageData(Long deviceId,
			Short year)
		{
		// * 検索条件の設定
		Ph2RealGrowthFStageEntityExample exm = new Ph2RealGrowthFStageEntityExample();
		exm.createCriteria().andDeviceIdEqualTo(deviceId).andYearEqualTo(year);
		exm.setOrderByClause("stage_start asc");
		// * 検索実行
		List<Ph2RealGrowthFStageEntity> records = this.ph2RealGrowthFStageMapper
				.selectByExample(exm);
		// * 該当が無い場合
		if (0 == records.size())
			{
			int sum = this.fstageValues.getStart().size();
			for (int i = 0; i < sum; i++)
				{
				// * デフォルトのFStageデータを作成し、DBに登録する。
				Ph2RealGrowthFStageEntity entity = new Ph2RealGrowthFStageEntity();
				entity.setDeviceId(deviceId);
				entity.setYear(year);
				entity.setAccumulatedF(this.fstageValues.getSig().get(i));
				entity.setIntervalF(this.fstageValues.getInterval().get(i));
				entity.setStageStart(this.fstageValues.getStart().get(i));
				entity.setStageEnd(this.fstageValues.getEnd().get(i));
				entity.setStageName(this.fstageValues.getName().get(i));
				entity.setColor(this.fstageValues.getColors().get(i));;
				entity.setCreatedAt(new Timestamp(System.currentTimeMillis()));
				entity.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
				long id = this.ph2RealGrowthFStageMapper.insert(entity);
				entity.setId(id);// TODO 必要か？
				records.add(entity);
				}
			return records;
			}
		else
			return records;
		}

	// --------------------------------------------------
	/**
	 * モデルデータテーブルから生育推定グラフデータを作成する
	 * 
	 * @param deviceId 対象デバイスID
	 * @param year 対象年度
	 * @param startDate 統計開始日
	 * @throws ParseException 
	 */
	// -------------------------------------------------
	public RealModelGraphDataDTO getModelGraphData(
			Long deviceId, Short year, Date startDate,
			Ph2ParamsetGrowthEntity param)
			throws ParseException
		{
// * デバイスID、年度からFStage情報を取得
		List<Ph2RealGrowthFStageEntity> fstageInfo = this
				.getFStageData(deviceId, year);
// * 統計対象開始日から存在しているDailyBaseDataの気温情報を取得
		List<DailyBaseDataDTO> realDayData = this.growthDomainMapper
				.selectDailyData(deviceId, year, null);
// * 計算処理を実行
		if (0 != realDayData.size())
			{
			GrowthGraphDataModel modelData = new GrowthGraphDataModel();
			modelData.calculateFvalues(realDayData, param, fstageInfo,
					this.fstageValues.getSprout());
// * グラフデータの生成
			return modelData.toGraphData();
			}
		else
			return null;
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
		// * デバイスID、年度からパラメータセットを取得
		Ph2ParamsetGrowthEntity param = this.getParmaters(deviceId, year);
		// * 生育推定モデルデータの取得
		RealModelGraphDataDTO data = this.getModelGraphData(deviceId, year,
				startDate, param);
		if (null != data)
			{
			// *モデルデータテーブルに生育推定モデルデータを設定する
			short startDay = 1;
			startDay = this.deviceDayDomain.updateModelData(startDay, deviceId,
					year, year, data.getValues());
			this.deviceDayDomain.updateModelData(startDay, deviceId, (short) (year - 1), year,
					data.getPredictValues());

			}
		}

	// --------------------------------------------------
	/**
	 * 特定のパラメータ指定による生育推定モデルグラフデータ取得
	 *
	 * @param deviceId  デバイスID
	 * @param year 対象年度
	 * @param param 生育推定パラメータ
	 * @return GraphDataDTO
	 * @throws ParseException 
	 */
	// --------------------------------------------------
	private RealModelGraphDataDTO getModelDataByParameter(Long deviceId,
			Short year,
			Ph2ParamsetGrowthEntity param) throws ParseException
		{
		Ph2DeviceDayEntity firstDay = this.deviceDayDomain.getFirstDay(deviceId,
				year);
		return this.getModelGraphData(deviceId, year, firstDay.getDate(),
				param);
		}

	// --------------------------------------------------
	/**
	 * 特定のパラメータ指定による生育推定モデルグラフデータ取得
	 *
	 * @param deviceId  デバイスID
	 * @param year 対象年度
	 * @param paramID パラメータID
	 * @return GraphDataDTO
	 * @throws ParseException 
	 */
	// --------------------------------------------------
	public RealModelGraphDataDTO getSimulateModelGraph(Long deviceId,
			Short year,
			Long paramId) throws ParseException
		{
		Ph2ParamsetGrowthEntity entity = this.ph2ParamsetGrowthMapper
				.selectByPrimaryKey(paramId);
		return getModelDataByParameter(deviceId, year, entity);
		}

	// --------------------------------------------------
	/**
	 * 特定のパラメータ指定による生育推定モデルグラフデータ取得
	 *
	 * @param deviceId  デバイスID
	 * @param year 対象年度
	 * @param bd 萌芽日前d値
	 * @param be 萌芽日前e値
	 * @param ad 萌芽日後d値
	 * @param ae 萌芽日後e値 
	 * @return GraphDataDTO
	 * @throws ParseException 
	 */
	// --------------------------------------------------
	public RealModelGraphDataDTO getSimulateModelGraph(Long deviceId,
			Short year,
			double bd, double be, double ad, double ae) throws ParseException
		{
		Ph2ParamsetGrowthEntity param = new Ph2ParamsetGrowthEntity();
		param.setAfterD(ad);
		param.setAfterE(ae);
		param.setBeforeD(bd);
		param.setBeforeE(be);
		return getModelDataByParameter(deviceId, year, param);
		}

	// --------------------------------------------------
	/**
	 * 生育推定モデルグラフデータ取得
	 *    モデルテーブルから検索してデータを取得する
	 *
	 * @param deviceId デバイスID
	 * @param year 対象年度
	 * @return RealModelGraphDataDTO
	 */
	// --------------------------------------------------
	public RealModelGraphDataDTO getModelGraph(Long deviceId, Short year)
		{
		List<ModelDataEntity> entites = this.ph2ModelDataMapper
				.selectModelDataByType(deviceId, year);
// * データが存在しない場合 nullを返す
		if (0 == entites.size()) return null;
		if (null == entites.get(0).getfValue()) return null;

		List<Double> values = new ArrayList<>();
		List<Double> predictValues = new ArrayList<>();
// * 日付カテゴリ
		List<String> category = new ArrayList<>();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
		for (ModelDataEntity entity : entites)
			{
			if (entity.getIsReal())
				values.add(entity.getfValue());
			else
				predictValues.add(entity.getfValue());
			// * 取得日
			category.add(sdf.format(entity.getDate()));
			}
		RealModelGraphDataDTO resultData = new RealModelGraphDataDTO();

// * 値の設定
		resultData.setValues(values);
		resultData.setPredictValues(predictValues);
		resultData.setCategory(category);
// * アノテーションデータの生成
		List<EventDaysDTO> annotations = super.getEvent(deviceId, year);
		resultData.setAnnotations(annotations);
// * 最小値・最大値の設定
		String first = category.get(0);
		String last = category.get(category.size() - 1);
		resultData.setXStart(first);
		resultData.setXEnd(last);
		
		resultData.setYStart((double) 0);
		resultData.setYEnd(annotations.get(annotations.size() - 1).getValue()+10);
// * コメント
		super.setComment(deviceId, year, resultData);
// * グラフデータの返却
		return resultData;
		}

	// --------------------------------------------------
	/**
	 * 生育推定F値データ取得
	 *
	 * @param deviceId デバイスID
	 * @param year  年度
	 * @return List<Ph2RealGrowthFStageEntity>
	 * @throws ParseException
	 */
	// --------------------------------------------------
	public List<Ph2RealGrowthFStageEntity> getALlFValus(Long deviceId,
			Short year)
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

	// --------------------------------------------------
	/**
	 * 生育推定実績値取得
	 *
	 * @param deviceId デバイスID
	 * @param date 日付
	 * @return 実績F値
	 */
	// --------------------------------------------------
	public ValueDateDTO getRealFData(Long deviceId, Date date)
		{
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		new DeviceDayAlgorithm().setTimeZero(cal);
		// * モデルテーブルから該当する日付にもっとも近い過去の実績値を取得する
		ValueDateDTO value = this.ph2ModelDataMapper.selectFValueByDate(deviceId, cal.getTime());
		if (null == value)
			{
			throw new RuntimeException("実績データはまだありません。");
			}
		return value;
		}

	// --------------------------------------------------
	/**
	 * 生育推定実績値更新
	 *
	 * @param dto FDataListDTO
	 */
	// --------------------------------------------------
	public void updateFValues(FDataListDTO dto)
		{
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
				entity.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
				this.ph2RealGrowthFStageMapper.updateByPrimaryKey(entity);
				}
			else
				{
				item.setDeviceId(dto.getDeviceId());
				item.setYear(dto.getYear());
				item.setCreatedAt(new Timestamp(System.currentTimeMillis()));
				item.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
				this.ph2RealGrowthFStageMapper.insert(item);
				}
			}
		}

	// --------------------------------------------------
	/**
	 * 生育推定パラメータセット詳細取得
	 *
	 * @param paramSetId パラメータセットID
	 * @return GrowthParamSetDTO
	 */
	// --------------------------------------------------
	public GrowthParamSetDTO getDetail(Long paramSetId)
		{
		GrowthParamSetDTO result = new GrowthParamSetDTO();

		parameterSetDomain.fetchDetail(paramSetId, result);
		Ph2ParamsetGrowthEntity field = this.ph2ParamsetGrowthMapper
				.selectByPrimaryKey(paramSetId);

		result.setAd(field.getAfterD());
		result.setAe(field.getAfterE());
		result.setBd(field.getBeforeD());
		result.setBe(field.getBeforeE());
		return result;
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
		GrowthParamSetDTO paramInfo = this.getDetail(paramId);
		// * 同じ年度・デバイスの場合
		if ((paramInfo.getDeviceId().longValue() != deviceId.longValue())
				|| (paramInfo.getYear().shortValue() != year.shortValue()))
			{
			paramInfo.setDeviceId(deviceId);
			paramInfo.setYear(year);
			paramId = this.addParamSet(null, paramInfo);
			}
		parameterSetDomain.setDefautParamSet(ModelMaster.GROWTH, deviceId, year, paramId);
		// 年度の最初の日を取得
		Ph2DeviceDayEntity deviceDay = this.deviceDayDomain.getFirstDay(deviceId, year);
		// デバイスがデータを持っている場合
		if (null != deviceDay)
			{
			this.updateModelTable(deviceId, year, deviceDay.getDate());
			}
		}

	// --------------------------------------------------
	/**
	 * 生育推定パラメータセット更新
	 *
	 * @param dto 更新データ
	 * @throws ParseException 
	 */
	// --------------------------------------------------
	public void updateParamSet(GrowthParamSetDTO dto) throws ParseException
		{
		boolean isDeault = parameterSetDomain.update(dto, ModelMaster.GROWTH);

		Ph2ParamsetGrowthEntity entity = this.ph2ParamsetGrowthMapper
				.selectByPrimaryKey(dto.getId());
		entity.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
		entity.setAfterD(dto.getAd());
		entity.setAfterE(dto.getAe());
		entity.setBeforeD(dto.getBd());
		entity.setBeforeE(dto.getBe());
		this.ph2ParamsetGrowthMapper.updateByPrimaryKey(entity);
		// * デフォルト値の更新の場合、モデルテーブルを更新する
		if (isDeault)
			{
			this.updateModelTable(dto.getDeviceId(), dto.getYear(), null);
			}
		}

	// --------------------------------------------------
	/**
	 * 生育推定パラメータセット追加
	 *
	 * @param dto 更新データ
	 * @return 
	 */
	// --------------------------------------------------
	public Long addParamSet(Long parentId, GrowthParamSetDTO dto)
		{
		// * パラメータセットカタログに登録する
		if (null == parentId)
			{
			// * 追加時、ここではデフォルトフラグはfalseとする。
			dto.setDefaultFlg(false);
			parentId = parameterSetDomain.add(dto, ModelMaster.GROWTH);
			}
		// * テーブル:成長予測パラメータセット(ph2_paramset_growth)に新しいパラメータセットレコードを追加する。
		Ph2ParamsetGrowthEntity entity = new Ph2ParamsetGrowthEntity();
		entity.setParamsetId(parentId);
		entity.setCreatedAt(new Timestamp(System.currentTimeMillis()));
		entity.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
		entity.setAfterD(dto.getAd());
		entity.setAfterE(dto.getAe());
		entity.setBeforeD(dto.getBd());
		entity.setBeforeE(dto.getBe());
		this.ph2ParamsetGrowthMapper.insert(entity);
		return parentId;
		}

	}
