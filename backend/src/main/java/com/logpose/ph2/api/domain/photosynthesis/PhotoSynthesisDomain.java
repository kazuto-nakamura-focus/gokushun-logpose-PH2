package com.logpose.ph2.api.domain.photosynthesis;

import java.sql.Timestamp;
import java.text.ParseException;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.logpose.ph2.api.algorythm.DateTimeUtility;
import com.logpose.ph2.api.dao.db.entity.Ph2ParamsetPsFieldEntity;
import com.logpose.ph2.api.dao.db.entity.Ph2ParamsetPsWeibullEntity;
import com.logpose.ph2.api.dao.db.entity.Ph2RealPsAmountEntity;
import com.logpose.ph2.api.dao.db.entity.Ph2RealPsAmountEntityExample;
import com.logpose.ph2.api.dao.db.mappers.Ph2ParamsetPsFieldMapper;
import com.logpose.ph2.api.dao.db.mappers.Ph2ParamsetPsWeibullMapper;
import com.logpose.ph2.api.dao.db.mappers.Ph2RealPsAmountMapper;
import com.logpose.ph2.api.domain.ParameterSetDomain;
import com.logpose.ph2.api.dto.PhotosynthesisParamSetDTO;
import com.logpose.ph2.api.dto.PhotosynthesisValueDTO;
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
		exm.setOrderByClause("date asc");

// * 問合せ実行
		return this.ph2RealPsAmountMapper.selectByExample(exm);
		}

	// ###############################################
	/**
	 * 光合成推定実績値更新
	 *
	 * @param records PhotosynthesisValueDTO 更新データリスト
	 * @throws ParseException 
	 */
	// ###############################################
	public void update(List<PhotosynthesisValueDTO> records) throws ParseException
		{
		Timestamp time = new Timestamp(System.currentTimeMillis());
		for (final PhotosynthesisValueDTO dto : records)
			{
			Date tmp = DateTimeUtility.getDateFromString(dto.getDate());
// * 該当するデバイスと日付を検索
			Ph2RealPsAmountEntity entity = this.ph2RealPsAmountMapper.selectByPrimaryKey(dto.getDeviceId(), tmp);
// * 存在しない場合、新たに作成
			if (null == entity)
				{
				entity = new Ph2RealPsAmountEntity();
				entity.setDeviceId(dto.getDeviceId());
				entity.setYear(dto.getYear());
				entity.setDate(time);
				}
// * データの作成
			entity.setUpdatedAt(time);
			entity.setValueF(dto.getF());
			entity.setValueG(dto.getG());
// * データの更新時
			if (null != entity.getCreatedAt())
				{
				this.ph2RealPsAmountMapper.updateByPrimaryKey(entity);
				}
			else
				{
				entity.setCreatedAt(time);
				this.ph2RealPsAmountMapper.insert(entity);
				}
			}
// * 古いデータの削除
		if (records.size() > 0)
			{
			Ph2RealPsAmountEntityExample exm = new Ph2RealPsAmountEntityExample();
			exm.createCriteria()
					.andDeviceIdEqualTo(records.get(0).getDeviceId())
					.andYearEqualTo(records.get(0).getYear())
					.andUpdatedAtLessThan(time);
			this.ph2RealPsAmountMapper.countByExample(exm);
			}
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
