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
import com.logpose.ph2.api.dao.db.mappers.Ph2ModelDataMapper;
import com.logpose.ph2.api.dao.db.mappers.Ph2ParamsetPsFieldMapper;
import com.logpose.ph2.api.dao.db.mappers.Ph2ParamsetPsWeibullMapper;
import com.logpose.ph2.api.dao.db.mappers.Ph2RealPsAmountMapper;
import com.logpose.ph2.api.dao.db.mappers.joined.GrowthDomainMapper;
import com.logpose.ph2.api.domain.DeviceDayDomain;
import com.logpose.ph2.api.dto.DailyBaseDataDTO;
import com.logpose.ph2.api.dto.PhotosynthesisParamSetDTO;
import com.logpose.ph2.api.master.ModelMaster;

@Component
public class PhotoSynthesisDomain extends PSModelDataParameterAggregator
	{
	// ===============================================
	// クラスメンバー
	// ===============================================
	@Autowired
	private DeviceDayDomain deviceDayDomain;
	@Autowired
	private GrowthDomainMapper growthDomainMapper;
	@Autowired
	private Ph2RealPsAmountMapper ph2RealPsAmountMapper;
	@Autowired
	private Ph2ParamsetPsFieldMapper ph2ParamsetPsFieldMapper;
	@Autowired
	private Ph2ParamsetPsWeibullMapper ph2ParamsetPsWeibullMapper;
	@Autowired
	private Ph2ModelDataMapper ph2ModelDataMapper;
	/*
	 * @Autowired
	 * ParameterSetDomain parameterSetDomain;
	 * @Autowired
	 * private DefaultPsParameters defaultPsParameters;
	 */

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
	public void updateModelTable(Long deviceId, Short year)
			throws ParseException
		{
// * 統計対象開始日から存在しているDailyBaseDataの気温情報を取得
		List<DailyBaseDataDTO> realDayData = this.growthDomainMapper
				.selectDailyData(deviceId, year, null);
// * データの出力先の設定をする
		if (0 != realDayData.size())
			{
			// * データ生成のためのパラメータを作成する。
			PSModelDataParameters parameters = new PSModelDataParameters();
			super.setParameters(deviceId, year, parameters);
			
			PSModelDataExporter exporter = new PSModelDataExporter(ph2ModelDataMapper);
			new PSGraphDataGenerator(parameters, exporter, realDayData);
			}
		}

	// --------------------------------------------------
	/**
	 * 光合成推定実績値取得
	 *
	 * @param devieId
	 */
	// --------------------------------------------------
	public List<Ph2RealPsAmountEntity> getRealPsAmountEntity(Long devieId, Short year)
		{
		Ph2RealPsAmountEntityExample exm = new Ph2RealPsAmountEntityExample();
		exm.createCriteria().andDeviceIdEqualTo(devieId).andYearEqualTo(year);
		exm.setOrderByClause("date desc");
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
		exm.createCriteria().andDeviceIdEqualTo(entity.getDeviceId()).andDateEqualTo(entity.getDate());
		List<Ph2RealPsAmountEntity> entities = this.ph2RealPsAmountMapper.selectByExample(exm);
		if(0 < entities.size())
			{
			this.ph2RealPsAmountMapper.updateByExample(entity, exm);
			}
		else this.ph2RealPsAmountMapper.insert(entity);
		}

	// --------------------------------------------------
	/**
	 * 光合成推定パラメータセット更新
	 *
	 * @param PhotosynthesisParamSetDTO 更新データ
	 */
	// --------------------------------------------------
	public boolean updateParamSet(PhotosynthesisParamSetDTO dto)
		{
		boolean isDeault = parameterSetDomain.update(dto, ModelMaster.PHOTO);

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
		
		return isDeault;
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
		PhotosynthesisParamSetDTO paramInfo = super.getDetail(paramId);
		// * 同じ年度・デバイスの場合
		if ((paramInfo.getDeviceId().longValue() != deviceId.longValue())
				|| (paramInfo.getYear().shortValue() != year.shortValue()))
			{
			paramInfo.setDeviceId(deviceId);
			paramInfo.setYear(year);
			paramId = super.addParamSet(null, paramInfo);
			}
		parameterSetDomain.setDefautParamSet(ModelMaster.PHOTO, deviceId, year, paramId);
		}
	}
