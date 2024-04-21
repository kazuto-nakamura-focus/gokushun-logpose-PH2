package com.logpose.ph2.api.domain.photosynthesis;

import java.sql.Timestamp;
import java.text.ParseException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.logpose.ph2.api.dao.db.entity.Ph2ParamsetPsFieldEntity;
import com.logpose.ph2.api.dao.db.entity.Ph2ParamsetPsWeibullEntity;
import com.logpose.ph2.api.dao.db.entity.Ph2RealPsAmountEntity;
import com.logpose.ph2.api.dao.db.entity.Ph2RealPsAmountEntityExample;
import com.logpose.ph2.api.dao.db.mappers.Ph2ParamsetPsFieldMapper;
import com.logpose.ph2.api.dao.db.mappers.Ph2ParamsetPsWeibullMapper;
import com.logpose.ph2.api.dao.db.mappers.Ph2RealPsAmountMapper;
import com.logpose.ph2.api.domain.ParameterSetDomain;
import com.logpose.ph2.api.dto.PhotosynthesisParamSetDTO;
import com.logpose.ph2.api.master.ModelMaster;

@Component
public class PhotoSynthesisDomain extends PSGraphDataGeneratorWrapper
	{
	// ===============================================
	// クラスメンバー
	// ===============================================
	@Autowired
	private ParameterSetDomain parameterSetDomain;
	@Autowired
	private Ph2RealPsAmountMapper ph2RealPsAmountMapper;
	@Autowired
	private Ph2ParamsetPsFieldMapper ph2ParamsetPsFieldMapper;
	@Autowired
	private Ph2ParamsetPsWeibullMapper ph2ParamsetPsWeibullMapper;

	// ===============================================
	// 公開関数群
	// ===============================================
	// ###############################################
	/**
	 * モデルデータの更新
	 *
	 * @param deviceId デバイスID
	 * @param year 年度
	 */
	// ###############################################
	public void updateModelTable(Long devieId, Short year)
		{
// * モデルデータの更新
		super.updateModelTable(devieId, year, null, null, null);
		}

	// ###############################################
	/**
	 * 光合成推定実績値取得
	 *
	 * @param deviceId デバイスID
	 * @param year 年度
	 */
	// ###############################################
	public List<Ph2RealPsAmountEntity> getRealPsAmountEntity(Long devieId, Short year)
		{
// * 検索条件の設定
		Ph2RealPsAmountEntityExample exm = new Ph2RealPsAmountEntityExample();
		exm.createCriteria().andDeviceIdEqualTo(devieId).andYearEqualTo(year);
		exm.setOrderByClause("date desc");

// * 問合せ実行
		return this.ph2RealPsAmountMapper.selectByExample(exm);
		}

	// ###############################################
	/**
	 * 光合成推定実績値更新
	 *
	 * @param entity Ph2RealPsAmountEntity 更新データ
	 */
	// ###############################################
	public void update(Ph2RealPsAmountEntity entity)
		{
// * 検索条件の設定
		Ph2RealPsAmountEntityExample exm = new Ph2RealPsAmountEntityExample();
		exm.createCriteria().andDeviceIdEqualTo(entity.getDeviceId()).andDateEqualTo(entity.getDate());
		List<Ph2RealPsAmountEntity> entities = this.ph2RealPsAmountMapper.selectByExample(exm);

// * データの更新または追加
		if (0 < entities.size())
			{
			this.ph2RealPsAmountMapper.updateByExample(entity, exm);
			}
		else
			this.ph2RealPsAmountMapper.insert(entity);

// * モデルデータの更新
		super.updateModelTable(entity.getDeviceId(), entity.getYear(), null, null, null);
		}

	// ###############################################
	/**
	 * 光合成推定パラメータセット更新
	 *
	 * @param dto PhotosynthesisParamSetDTO 更新データ
	 */
	// ###############################################
	public void updateParamSet(PhotosynthesisParamSetDTO dto)
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
		if (isDeault)
			{
			// * モデルデータの更新
			super.updateModelTable(dto.getDeviceId(), dto.getYear(), dto.getFieldF(), dto.getFieldG(), null);
			}
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
	public void setDefault(Long deviceId, Short year, Long paramId)
			throws ParseException
		{
// * パラメータセットの詳細を取得する
		PhotosynthesisParamSetDTO paramInfo = super.getDetail(paramId);

// * デバイスIDまたは年度が異なっている場合、新たなパラメータとして追加する
		if ((paramInfo.getDeviceId().longValue() != deviceId.longValue())
				|| (paramInfo.getYear().shortValue() != year.shortValue()))
			{
			paramInfo.setDeviceId(deviceId);
			paramInfo.setYear(year);
			paramId = super.addParamSet(null, paramInfo);
			}
// * 指定されたパラメータセットIDを指定デバイスの指定年度に対して、デフォルト値に設定する
		parameterSetDomain.setDefautParamSet(ModelMaster.PHOTO, deviceId, year, paramId);

// * デバイスのモデルテーブルを更新する
		super.updateModelTable(deviceId, year, paramInfo.getFieldF(), paramInfo.getFieldG(), null);
		}
	}
