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
import com.logpose.ph2.api.dao.db.entity.Ph2RealLeafShootsCountEntity;
import com.logpose.ph2.api.dao.db.entity.Ph2RealLeafShootsCountEntityExample;
import com.logpose.ph2.api.dao.db.entity.Ph2WibleMasterEntity;
import com.logpose.ph2.api.dao.db.entity.Ph2WibleMasterEntityExample;
import com.logpose.ph2.api.dao.db.mappers.Ph2ParamsetLeafAreaMapper;
import com.logpose.ph2.api.dao.db.mappers.Ph2ParamsetLeafCountMapper;
import com.logpose.ph2.api.dao.db.mappers.Ph2RealLeafShootsCountMapper;
import com.logpose.ph2.api.dao.db.mappers.Ph2WibleMasterMapper;
import com.logpose.ph2.api.dao.db.mappers.joined.LeafDomainMapper;
import com.logpose.ph2.api.domain.ParameterSetDomain;
import com.logpose.ph2.api.dto.LeafParamSetDTO;
import com.logpose.ph2.api.master.ModelMaster;

@Component
public class LeafModelDataParameterAggregator
	{
	// ===============================================
	// クラスメンバー
	// ===============================================
	@Autowired
	protected ParameterSetDomain parameterSetDomain;
	/** ワイブル分布マスターデータ */
	@Autowired
	private Ph2WibleMasterMapper ph2WibleMasterMapper;
	@Autowired
	private DefaultLeafCountParameters defaultLeafCountParameters;
	@Autowired
	private DefaultLeafAreaParameters defaultLeafAreaParameters;
	@Autowired
	private Ph2RealLeafShootsCountMapper ph2RealLeafShootsCountMapper;
	@Autowired
	private Ph2ParamsetLeafAreaMapper ph2ParamsetLeafAreaMapper;
	@Autowired
	private Ph2ParamsetLeafCountMapper ph2ParamsetLeafCountMapper;
	@Autowired
	private LeafDomainMapper leafDomainMapper;

	// ===============================================
	// 公開メソッド
	// ===============================================
	// --------------------------------------------------
	/**
	 *　パラメータの設定を行う
	 * @param parameters 設定対象となるパラメータ
	 */
	// --------------------------------------------------
	public void setParameters(Long deviceId, Short year, LeafModelDataParameters parameters)
		{
// * 葉面積パラメータセットの取得
		LeafParamSetDTO paramset = this.getParmaters(deviceId, year);
		parameters.setParams(paramset);
// * 萌芽日
		List<Integer> rec = this.leafDomainMapper.getSproutDay(4, deviceId, year);
		parameters.setLapseDay(rec.get(0));
// * 新梢数
		int shoot_count = this.getShootCount(deviceId, year);
		parameters.setShootCount(shoot_count);
// * ワイブル分布の取得
		List<Ph2WibleMasterEntity> wibles = this.ph2WibleMasterMapper
				.selectByExample(new Ph2WibleMasterEntityExample());
		parameters.setWibles(wibles);
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

	}
