package com.logpose.ph2.api.domain.photosynthesis;

import java.sql.Timestamp;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.logpose.ph2.api.configration.DefaultLeafCountParameters;
import com.logpose.ph2.api.configration.DefaultPsParameters;
import com.logpose.ph2.api.dao.db.entity.Ph2ParamsetCatalogEntity;
import com.logpose.ph2.api.dao.db.entity.Ph2ParamsetPsFieldEntity;
import com.logpose.ph2.api.dao.db.entity.Ph2ParamsetPsWeibullEntity;
import com.logpose.ph2.api.dao.db.entity.Ph2RealLeafShootsCountEntity;
import com.logpose.ph2.api.dao.db.entity.Ph2RealLeafShootsCountEntityExample;
import com.logpose.ph2.api.dao.db.entity.Ph2RealPsAmountEntity;
import com.logpose.ph2.api.dao.db.entity.Ph2RealPsAmountEntityExample;
import com.logpose.ph2.api.dao.db.mappers.Ph2ModelDataMapper;
import com.logpose.ph2.api.dao.db.mappers.Ph2ParamsetPsFieldMapper;
import com.logpose.ph2.api.dao.db.mappers.Ph2ParamsetPsWeibullMapper;
import com.logpose.ph2.api.dao.db.mappers.Ph2RealLeafShootsCountMapper;
import com.logpose.ph2.api.dao.db.mappers.Ph2RealPsAmountMapper;
import com.logpose.ph2.api.domain.ParameterSetDomain;
import com.logpose.ph2.api.dto.PhotosynthesisParamSetDTO;
import com.logpose.ph2.api.master.ModelMaster;

@Component
public class PSModelDataParameterAggregator
	{
	// ===============================================
	// クラスメンバー
	// ===============================================
	@Autowired
	protected ParameterSetDomain parameterSetDomain;
	
	@Autowired
	private DefaultPsParameters defaultPsParameters;
	
	@Autowired
	private DefaultLeafCountParameters defaultLeafParameters;

	@Autowired
	private Ph2ParamsetPsFieldMapper ph2ParamsetPsFieldMapper;

	@Autowired
	private Ph2ParamsetPsWeibullMapper ph2ParamsetPsWeibullMapper;
	
	@Autowired
	private Ph2RealPsAmountMapper ph2RealPsAmountMapper;
	
	@Autowired
	private Ph2ModelDataMapper ph2ModelDataMapper;
	
	@Autowired
	private Ph2RealLeafShootsCountMapper ph2RealLeafShootsCountMapper;

	// ===============================================
	// 公開メソッド
	// ===============================================
	// --------------------------------------------------
	/**
	 *　パラメータの設定を行う
	 * @param parameters 設定対象となるパラメータ
	 */
	// --------------------------------------------------
	public void setParameters(Long deviceId, Short year, PSModelDataParameters parameters)
		{
// * 葉面積パラメータセットの取得
		PhotosynthesisParamSetDTO paramset = this.getParmaters(deviceId, year);
		parameters.setParams(paramset);
// * 実測値Mapの設定
		Map<Date, Ph2RealPsAmountEntity> map = this.setRealValueDateMap(deviceId, year);
		parameters.setRealParamMap(map);
// * モデルマッパーの設定
		parameters.setPh2ModelDataMapper(ph2ModelDataMapper);
// * 新梢数
		Ph2RealLeafShootsCountEntityExample exm = new Ph2RealLeafShootsCountEntityExample();
		exm.createCriteria().andDeviceIdEqualTo(deviceId)
				.andYearEqualTo(year);
		List<Ph2RealLeafShootsCountEntity> result = this.ph2RealLeafShootsCountMapper
				.selectByExample(exm);
		if(result.size()>0)
			{
			parameters.setShootCount(result.get(0).getCount());
			}
		else
			{
			parameters.setShootCount(this.defaultLeafParameters.getCount());
			}
		}

	// --------------------------------------------------
	/**
	 * 実測値を取得する
	 * 存在しない場合はデフォルト値を設定して返却
	 * 
	 * @param deviceId-デバイスID
	 * @param year-対象年度
	 * @return Ph2ParamsetGrowthEntity
	 * @throws ParseException 
	 */
	// --------------------------------------------------
	private Map<Date, Ph2RealPsAmountEntity> setRealValueDateMap(Long deviceId, Short year)
		{
		Ph2RealPsAmountEntityExample exm = new Ph2RealPsAmountEntityExample();
		exm.createCriteria().andDeviceIdEqualTo(deviceId).andYearEqualTo(year);
		List<Ph2RealPsAmountEntity> entities = this.ph2RealPsAmountMapper.selectByExample(exm);
		Map<Date, Ph2RealPsAmountEntity> map = new HashMap<>();
		for(Ph2RealPsAmountEntity entity : entities)
			{
			map.put(entity.getDate(), entity);
			}
		return map;
		}
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
		List<Ph2ParamsetCatalogEntity> params =parameterSetDomain.getParamSetCatalogsByYear(
				3, deviceId, year);
		// * 該当するパラメータセットが存在する場合
		if ((0 < params.size()) && (year.intValue() == params.get(0).getYear().intValue()))
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
			// * 追加時、ここではデフォルトフラグはfalseとする。
			dto.setDefaultFlg(false);
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
	}
