package com.logpose.ph2.api.domain.leaf;

import java.sql.Timestamp;
import java.text.ParseException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.logpose.ph2.api.configration.DefaultLeafAreaParameters;
import com.logpose.ph2.api.configration.DefaultLeafCountParameters;
import com.logpose.ph2.api.dao.db.entity.Ph2ParamsetCatalogEntity;
import com.logpose.ph2.api.dao.db.entity.Ph2ParamsetLeafAreaEntity;
import com.logpose.ph2.api.dao.db.entity.Ph2ParamsetLeafCountEntity;
import com.logpose.ph2.api.dao.db.mappers.Ph2ParamsetLeafAreaMapper;
import com.logpose.ph2.api.dao.db.mappers.Ph2ParamsetLeafCountMapper;
import com.logpose.ph2.api.dao.db.mappers.Ph2RealLeafShootsCountMapper;
import com.logpose.ph2.api.domain.ParameterSetDomain;
import com.logpose.ph2.api.dto.LeafParamSetDTO;
import com.logpose.ph2.api.master.ModelMaster;

@Component
public class LeafParameterDomain
	{
	// ===============================================
	// クラスメンバー
	// ===============================================
	@Autowired
	protected ParameterSetDomain parameterSetDomain;
	@Autowired
	protected DefaultLeafCountParameters defaultLeafCountParameters;
	@Autowired
	protected DefaultLeafAreaParameters defaultLeafAreaParameters;
	@Autowired
	protected Ph2RealLeafShootsCountMapper ph2RealLeafShootsCountMapper;
	@Autowired
	protected Ph2ParamsetLeafAreaMapper ph2ParamsetLeafAreaMapper;
	@Autowired
	protected Ph2ParamsetLeafCountMapper ph2ParamsetLeafCountMapper;
	// ===============================================
	// 公開メソッド
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
	// ###############################################
	/**
	 * 葉面積・葉枚数パラメータセット詳細取得
	 *
	 * @param paramSetId パラメータセットID
	 * @return LeafParamSetDTO
	 */
	// ###############################################
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
	// ###############################################
	/**
	 * 葉面積・葉枚数パラメータセット更新
	 *
	 * @param LeafParamSetDTO 更新データ
	 * @return 更新されたパラメータセットがデフォルトかどうかのフラグ
	 * @throws ParseException 
	 */
	// ###############################################
	public LeafParamSetDTO updateParamSet(LeafParamSetDTO dto) throws ParseException
		{
		boolean isDeault = this.parameterSetDomain.update(dto, ModelMaster.LEAF);

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

		// * デフォルト値の場合、モデルデータの更新を行う
		if (isDeault)
			{
			LeafParamSetDTO args = new LeafParamSetDTO();
			args.setAreaA(dto.getAreaA());
			args.setAreaB(dto.getAreaB());
			args.setAreaC(dto.getAreaC());
			args.setCountC(dto.getCountC());
			args.setCountD(dto.getCountD());
			return args;
			}
		else
			{
			return null;
			}
		}

	// ###############################################
	/**
	 * デフォルト値の設定
	 * 
	 * @param deviceId デバイスID
	 * @param year 年度
	 * @param paramId パラメータセットID
	 * @throws ParseException 
	 */
	// ###############################################
	public LeafParamSetDTO setDefault(Long deviceId, Short year, Long paramId)
			throws ParseException
		{
// * パラメータセットの詳細を取得する
		LeafParamSetDTO paramInfo = this.getDetail(paramId);
// * 同じ年度・デバイスの場合
		if ((paramInfo.getDeviceId().longValue() != deviceId.longValue())
				|| (paramInfo.getYear().shortValue() != year.shortValue()))
			{
			paramInfo.setDeviceId(deviceId);
			paramInfo.setYear(year);
			paramId = this.addParamSet(null, paramInfo);
			}
		parameterSetDomain.setDefautParamSet(ModelMaster.LEAF, deviceId, year, paramId);
		return paramInfo;
		}
	// ###############################################
	/**
	 * 葉面積・葉枚数パラメータセット追加
	 *
	 * @param LeafParamSetDTO 更新データ
	 * @return 
	 */
	// ###############################################
	public Long addParamSet(Long id, LeafParamSetDTO dto)
		{
		if (null == id)
			{
			// * 追加時、ここではデフォルトフラグはfalseとする。
			dto.setDefaultFlg(false);
			id = parameterSetDomain.add(dto, ModelMaster.LEAF);
			}
		else
			{
			parameterSetDomain.addHistory(dto, id);
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

	}
