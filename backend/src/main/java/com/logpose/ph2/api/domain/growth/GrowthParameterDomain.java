package com.logpose.ph2.api.domain.growth;

import java.sql.Timestamp;
import java.text.ParseException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.logpose.ph2.api.configration.DefaultGrowthParameters;
import com.logpose.ph2.api.dao.db.entity.Ph2ParamsetCatalogEntity;
import com.logpose.ph2.api.dao.db.entity.Ph2ParamsetGrowthEntity;
import com.logpose.ph2.api.dao.db.mappers.Ph2ParamsetGrowthMapper;
import com.logpose.ph2.api.domain.ParameterSetDomain;
import com.logpose.ph2.api.dto.GrowthParamSetDTO;
import com.logpose.ph2.api.master.ModelMaster;

@Component
public class GrowthParameterDomain
	{
	// ===============================================
	// クラスメンバー
	// ===============================================
	@Autowired
	private ParameterSetDomain parameterSetDomain;
	@Autowired
	private DefaultGrowthParameters parameters;
	@Autowired
	private Ph2ParamsetGrowthMapper ph2ParamsetGrowthMapper;

	// ===============================================
	// 公開関数群
	// ===============================================
	// ###############################################
	/**
	 * パラメータセットをデフォルトフラグがあるもので、近い年から取得する。
	 * 存在しない場合はデフォルト値を設定して返却
	 * 
	 * @param deviceId-デバイスID
	 * @param year-対象年度
	 * @return Ph2ParamsetGrowthEntity
	 * @throws ParseException 
	 */
	// ###############################################
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
	// ###############################################
	/**
	 * 生育推定パラメータセット詳細取得
	 *
	 * @param paramSetId パラメータセットID
	 * @return GrowthParamSetDTO
	 */
	// ###############################################
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
	// ###############################################
	/**
	 * デフォルト値の設定
	 * 
	 * @param deviceId-デバイスID
	 * @param year-対象年度
	 * @param paramId パラメータセットID
	 */
	// ###############################################
	public GrowthParamSetDTO setDefault(Long deviceId, Short year, Long paramId)
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
		return paramInfo;
		}

	// ###############################################
	/**
	 * 生育推定パラメータセット更新
	 *
	 * @param dto 更新データ
	 */
	// ###############################################
	public GrowthParamSetDTO updateParamSet(GrowthParamSetDTO dto)
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
		
		return (isDeault) ? dto : null;
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
