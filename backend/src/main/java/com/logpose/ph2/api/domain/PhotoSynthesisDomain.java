package com.logpose.ph2.api.domain;

import java.sql.Timestamp;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.logpose.ph2.api.algorythm.DailyDataAlgorythm;
import com.logpose.ph2.api.configration.DefaultPsParameters;
import com.logpose.ph2.api.dao.db.entity.Ph2ParamsetCatalogEntity;
import com.logpose.ph2.api.dao.db.entity.Ph2ParamsetPsFieldEntity;
import com.logpose.ph2.api.dao.db.entity.Ph2ParamsetPsWeibullEntity;
import com.logpose.ph2.api.dao.db.entity.Ph2RealPsAmountEntity;
import com.logpose.ph2.api.dao.db.entity.Ph2RealPsAmountEntityExample;
import com.logpose.ph2.api.dao.db.entity.joined.ModelDataEntity;
import com.logpose.ph2.api.dao.db.mappers.Ph2ModelDataMapper;
import com.logpose.ph2.api.dao.db.mappers.Ph2ParamsetPsFieldMapper;
import com.logpose.ph2.api.dao.db.mappers.Ph2ParamsetPsWeibullMapper;
import com.logpose.ph2.api.dao.db.mappers.Ph2RealPsAmountMapper;
import com.logpose.ph2.api.dao.db.mappers.joined.GrowthDomainMapper;
import com.logpose.ph2.api.dto.DailyBaseDataDTO;
import com.logpose.ph2.api.dto.PhotosynthesisParamSetDTO;
import com.logpose.ph2.api.dto.RealModelGraphDataDTO;
import com.logpose.ph2.api.master.ModelMaster;
import com.logpose.ph2.api.utility.DateTimeUtility;

@Component
public class PhotoSynthesisDomain
	{
	// ===============================================
	// クラスメンバー
	// ===============================================
	@Autowired
	private DailyDataAlgorythm dailyDataAlgorythm;
	@Autowired
	ParameterSetDomain parameterSetDomain;
	@Autowired
	private DefaultPsParameters defaultPsParameters;

	@Autowired
	private Ph2RealPsAmountMapper ph2RealPsAmountMapper;

	@Autowired
	private Ph2ParamsetPsFieldMapper ph2ParamsetPsFieldMapper;

	@Autowired
	private Ph2ParamsetPsWeibullMapper ph2ParamsetPsWeibullMapper;
	
	@Autowired
	private GrowthDomainMapper growthDomainMapper;
	
	@Autowired
	private Ph2ModelDataMapper ph2ModelDataMapper;
	
	@Autowired
	private DeviceDayDomain deviceDayDomain;
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
	public PhotosynthesisParamSetDTO getParmaters(Long deviceId, Short year)
	// throws ParseException
		{
		// * 該当するパラメータセットのカタログを取得
		List<Ph2ParamsetCatalogEntity> params = parameterSetDomain.getParamSetCatalogsByYear(
				3, deviceId, year);
		// * 該当するパラメータセットが存在する場合
		if ((0 < params.size()) && (year == params.get(0).getYear()))
			{
			return this.getDetail(params.get(0).getId());
			}
		// * 存在しない場合、パラメータセット・カタログに履歴を付与して更新する
		Long paramId = parameterSetDomain.addCatalogData(3, deviceId, year);
		// * 最近のパラメータセット・カタログがある場合
		if (0 < params.size())
			{
			PhotosynthesisParamSetDTO dto = this
					.getDetail(params.get(0).getId());
			this.addParamSet(paramId, dto);
			}
		// * 全く新規の場合
		else
			{
			Ph2ParamsetPsFieldEntity field = new Ph2ParamsetPsFieldEntity();
			field.setParamsetId(paramId);
			field.setCreatedAt(new Timestamp(System.currentTimeMillis()));
			field.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
			field.setValueF(this.defaultPsParameters.getF());
			field.setValueG(this.defaultPsParameters.getG());
			this.ph2ParamsetPsFieldMapper.insert(field);

			Ph2ParamsetPsWeibullEntity weibull = new Ph2ParamsetPsWeibullEntity();
			weibull.setParamsetId(paramId);
			weibull.setCreatedAt(new Timestamp(System.currentTimeMillis()));
			weibull.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
			weibull.setValueA(this.defaultPsParameters.getA());
			weibull.setValueB(this.defaultPsParameters.getB());
			weibull.setValueL(this.defaultPsParameters.getL());
			this.ph2ParamsetPsWeibullMapper.insert(weibull);
			}
		return this.getDetail(paramId);
		}

	// --------------------------------------------------
	/**
	 * モデルデータテーブルから光合成量グラフデータを作成する
	 * 
	 * @param deviceId 対象デバイスID
	 * @param year 対象年度
	 * @param startDate 統計開始日
	 * @throws ParseException 
	 */
	// -------------------------------------------------
	public PsGraphDataModel getModelGraphData(
			Long deviceId, Short year, Date startDate)
			throws ParseException
		{
		// * 実測値
		List<Ph2RealPsAmountEntity> real = this.getRealPsAmountEntity(deviceId,
				year, startDate);
		Float realF = null;
		Float realG = null;
		if (real.size() > 0)
			{
			realF = real.get(0).getValueF();
			realG = real.get(0).getValueG();
			}
		// * パラメータ
		PhotosynthesisParamSetDTO param = this.getParmaters(deviceId, year);
		// * 統計対象開始日から存在しているDailyBaseDataの気温情報を取得
		List<DailyBaseDataDTO> realDayData = this.dailyDataAlgorythm
				.getDailyBaseData(deviceId, year);
		// * 計算処理を実行
		return new PsGraphDataModel(realF, realG,
				param, ph2ModelDataMapper, realDayData);
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
		PsGraphDataModel model= this.getModelGraphData(deviceId, year, startDate);
		this.deviceDayDomain.updateModelData(deviceId, year, model);
		}
	// --------------------------------------------------
	/**
	 * 光合成量モデルグラフデータ取得
	 *    モデルテーブルから検索してデータを取得する
	 *
	 * @param deviceId デバイスID
	 * @param year 対象年度
	 * @return GraphDataDTO
	 * @throws ParseException 
	 */
	// --------------------------------------------------
	public RealModelGraphDataDTO getModelGraph(Long deviceId, Short year)
			throws ParseException
		{
		RealModelGraphDataDTO areaModel = new RealModelGraphDataDTO();
		
		List<ModelDataEntity> entites = this.ph2ModelDataMapper
				.selectModelDataByType(deviceId, year);
		List<Double> values = new ArrayList<>();
		List<Double> predictValues = new ArrayList<>();
		for (ModelDataEntity entity : entites)
			{
			if (entity.getIsReal())
				{
				areaModel.getValues().add(entity.getCulmitiveCnopyPs());
				}
			else
				{
				areaModel.getPredictValues().add(entity.getCulmitiveCnopyPs());
				}
			}
		// * 最小値・最大値の設定
		ModelDataEntity first = entites.get(0);
		ModelDataEntity last = entites.get(entites.size() - 1);
		areaModel
				.setXStart(DateTimeUtility.getStringFromDate(first.getDate()));
		areaModel.setXEnd(DateTimeUtility.getStringFromDate(last.getDate()));
		areaModel.setYStart(entites.get(0).getCulmitiveCnopyPs());
		areaModel.setYEnd(entites.get(entites.size() - 1).getCulmitiveCnopyPs());

		return areaModel;
		}

	// --------------------------------------------------
	/**
	 * 光合成推定実績値取得
	 *
	 * @param devieId
	 */
	// --------------------------------------------------
	public List<Ph2RealPsAmountEntity> getRealPsAmountEntity(Long devieId,
			Short year, Date date)
		{
		Ph2RealPsAmountEntityExample exm = new Ph2RealPsAmountEntityExample();
		exm.createCriteria().andDeviceIdEqualTo(devieId).andYearEqualTo(year).andDateEqualTo(date);
		return this.ph2RealPsAmountMapper.selectByExample(exm);
		}

	// --------------------------------------------------
	/**
	 * 光合成推定実績値更新
	 *
	 * @param dto PhotosynthesisValueDTO
	 */
	// --------------------------------------------------
	public void update(Ph2RealPsAmountEntity entity)
		{
		Ph2RealPsAmountEntityExample exm = new Ph2RealPsAmountEntityExample();
		exm.createCriteria().andDeviceIdEqualTo(entity.getDeviceId());
		this.ph2RealPsAmountMapper.updateByExample(entity, exm);
		}

	// --------------------------------------------------
	/**
	 * 光合成推定パラメータセット詳細取得
	 *
	 * @param paramSetId パラメータセットID
	 * @return PhotosynthesisParamSetDTO
	 */
	// --------------------------------------------------
	public PhotosynthesisParamSetDTO getDetail(Long paramSetId)
		{
		PhotosynthesisParamSetDTO result = new PhotosynthesisParamSetDTO();

		parameterSetDomain.fetchDetail(paramSetId, result);
		Ph2ParamsetPsFieldEntity field = this.ph2ParamsetPsFieldMapper
				.selectByPrimaryKey(paramSetId);
		Ph2ParamsetPsWeibullEntity weibull = this.ph2ParamsetPsWeibullMapper
				.selectByPrimaryKey(paramSetId);

		result.setFieldF(field.getValueF());
		result.setFieldG(field.getValueG());
		result.setWeibullA(weibull.getValueA());
		result.setWeibullB(weibull.getValueB());
		result.setWeibullL(weibull.getValueL());

		return result;
		}

	// --------------------------------------------------
	/**
	 * 光合成推定パラメータセット更新
	 *
	 * @param PhotosynthesisParamSetDTO 更新データ
	 */
	// --------------------------------------------------
	public void updateParamSet(PhotosynthesisParamSetDTO dto)
		{
		parameterSetDomain.update(dto, ModelMaster.PHOTO);

		Ph2ParamsetPsFieldEntity field = this.ph2ParamsetPsFieldMapper
				.selectByPrimaryKey(dto.getId());
		field.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
		field.setValueF(dto.getFieldF());
		field.setValueG(dto.getFieldG());
		this.ph2ParamsetPsFieldMapper.updateByPrimaryKey(field);

		Ph2ParamsetPsWeibullEntity weibull = this.ph2ParamsetPsWeibullMapper
				.selectByPrimaryKey(dto.getId());
		weibull.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
		weibull.setValueA(dto.getWeibullA());
		weibull.setValueB(dto.getWeibullB());
		weibull.setValueL(dto.getWeibullL());
		this.ph2ParamsetPsWeibullMapper.updateByPrimaryKey(weibull);
		}

	// --------------------------------------------------
	/**
	 * 光合成推定パラメータセット追加
	 *
	 * @param PhotosynthesisParamSetDTO 更新データ
	 * @return 
	 */
	// --------------------------------------------------
	public Long addParamSet(Long id, PhotosynthesisParamSetDTO dto)
		{
		if (null == id)
			{
			 id = parameterSetDomain.add(dto, ModelMaster.PHOTO);
			}
		Ph2ParamsetPsFieldEntity field = new Ph2ParamsetPsFieldEntity();
		field.setParamsetId(id);
		field.setCreatedAt(new Timestamp(System.currentTimeMillis()));
		field.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
		field.setValueF(dto.getFieldF());
		field.setValueG(dto.getFieldG());
		this.ph2ParamsetPsFieldMapper.insert(field);

		Ph2ParamsetPsWeibullEntity weibull = new Ph2ParamsetPsWeibullEntity();
		weibull.setParamsetId(id);
		weibull.setCreatedAt(new Timestamp(System.currentTimeMillis()));
		weibull.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
		weibull.setValueA(dto.getWeibullA());
		weibull.setValueB(dto.getWeibullB());
		weibull.setValueL(dto.getWeibullL());
		this.ph2ParamsetPsWeibullMapper.insert(weibull);
		
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
		parameterSetDomain.setDefautParamSet(paramId);
		this.updateModelTable(deviceId, year, null);
		}
	}
