package com.logpose.ph2.api.domain.photosynthesis;

import java.sql.Timestamp;
import java.text.ParseException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.logpose.ph2.api.configration.DefaultPsParameters;
import com.logpose.ph2.api.dao.db.entity.Ph2ParamsetCatalogEntity;
import com.logpose.ph2.api.dao.db.entity.Ph2ParamsetPsFieldEntity;
import com.logpose.ph2.api.dao.db.entity.Ph2ParamsetPsWeibullEntity;
import com.logpose.ph2.api.dao.db.mappers.Ph2ParamsetPsFieldMapper;
import com.logpose.ph2.api.dao.db.mappers.Ph2ParamsetPsWeibullMapper;
import com.logpose.ph2.api.domain.ParameterSetDomain;
import com.logpose.ph2.api.dto.photosynthesis.PhotosynthesisParamSetDTO;
import com.logpose.ph2.api.master.ModelMaster;

@Component
public class PSParameterSetDomain
	{
	// ===============================================
	// クラスメンバー
	// ===============================================
	@Autowired
	private DefaultPsParameters defaultPsParameters;
	@Autowired
	private ParameterSetDomain parameterSetDomain;
	@Autowired
	private Ph2ParamsetPsFieldMapper ph2ParamsetPsFieldMapper;
	@Autowired
	private Ph2ParamsetPsWeibullMapper ph2ParamsetPsWeibullMapper;

	// ===============================================
	// 公開関数群(検索系)
	// ===============================================
	// ###############################################
	/**
	 * パラメータセットをデフォルトフラグがあるもので、近い年から取得する。
	 * 存在しない場合はデフォルト値を設定して返却
	 * 
	 * @param deviceId-デバイスID
	 * @param year-対象年度
	 * @return Ph2ParamsetGrowthEntity
	 */
	// ###############################################
	public PhotosynthesisParamSetDTO getParmaters(Long deviceId, Short year)
		{
		// * 該当するパラメータセットのカタログを取得
		List<Ph2ParamsetCatalogEntity> params = parameterSetDomain.getParamSetCatalogsByYear(
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

	// ###############################################
	/**
	 * 光合成推定パラメータセット詳細取得
	 *
	 * @param paramSetId パラメータセットID
	 * @return PhotosynthesisParamSetDTO 光合成推定パラメータセット詳細
	 */
	// ###############################################
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

	// ===============================================
	// 公開関数群(更新系)
	// ===============================================
	// ###############################################
	/**
	 * 光合成推定パラメータセット追加
	 *
	 * @param PhotosynthesisParamSetDTO 更新データ
	 * @return 追加されたパラメータセットのID
	 */
	// ###############################################
	public Long addParamSet(Long id, PhotosynthesisParamSetDTO dto)
		{
		if (null == id)
			{
			// * 追加時、ここではデフォルトフラグはfalseとする。
			dto.setDefaultFlg(false);
			id = parameterSetDomain.add(dto, ModelMaster.PHOTO);
			}
		else
			{
			parameterSetDomain.addHistory(dto, id);
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

	// ###############################################
	/**
	 * 光合成推定パラメータセット更新
	 *
	 * @param dto PhotosynthesisParamSetDTO 更新データ
	 * @param デフォルトが変更された場合、デフォルトパラメータセット
	 */
	// ###############################################
	public PhotosynthesisParamSetDTO updateParamSet(PhotosynthesisParamSetDTO dto)
		{
// * パラメータセットの共通部分の変更
		boolean isDeault = parameterSetDomain.update(dto, ModelMaster.PHOTO);

// * フィールド値の変更
		Ph2ParamsetPsFieldEntity field = this.ph2ParamsetPsFieldMapper
				.selectByPrimaryKey(dto.getId());
		field.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
		field.setValueF(dto.getFieldF());
		field.setValueG(dto.getFieldG());
		this.ph2ParamsetPsFieldMapper.updateByPrimaryKey(field);

// * ワイブル値の変更
		Ph2ParamsetPsWeibullEntity weibull = this.ph2ParamsetPsWeibullMapper
				.selectByPrimaryKey(dto.getId());
		weibull.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
		weibull.setValueA(dto.getWeibullA());
		weibull.setValueB(dto.getWeibullB());
		weibull.setValueL(dto.getWeibullL());
		this.ph2ParamsetPsWeibullMapper.updateByPrimaryKey(weibull);

// * デフォルト値の場合、モデルデータの更新を行う
		return (isDeault) ? dto : null;
		}

	// ###############################################
	/**
	 * デフォルト値の設定
	 * @param deviceId 対象となるデバイスID
	 * @param year 対象となる年度
	 * @param paramId デフォルトとなるパラメータセットID
	 * @throws ParseException
	 */
	// ###############################################
	public PhotosynthesisParamSetDTO setDefault(Long deviceId, Short year, Long paramId)
		{
// * パラメータセットの詳細を取得する
		PhotosynthesisParamSetDTO paramInfo = this.getDetail(paramId);

// * デバイスIDまたは年度が異なっている場合、新たなパラメータとして追加する
		if ((paramInfo.getDeviceId().longValue() != deviceId.longValue())
				|| (paramInfo.getYear().shortValue() != year.shortValue()))
			{
			paramInfo.setDeviceId(deviceId);
			paramInfo.setYear(year);
			paramId = this.addParamSet(null, paramInfo);
			}
// * 指定されたパラメータセットIDを指定デバイスの指定年度に対して、デフォルト値に設定する
		parameterSetDomain.setDefautParamSet(ModelMaster.PHOTO, deviceId, year, paramId);

		return paramInfo;
		}
	}
