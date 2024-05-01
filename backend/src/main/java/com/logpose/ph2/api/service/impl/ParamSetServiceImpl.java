package com.logpose.ph2.api.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.logpose.ph2.api.dao.db.entity.Ph2ParamsetGrowthEntity;
import com.logpose.ph2.api.domain.ParameterSetDomain;
import com.logpose.ph2.api.domain.growth.GrowthParameterDomain;
import com.logpose.ph2.api.domain.leaf.LeafDomain;
import com.logpose.ph2.api.domain.photosynthesis.PSModelDataParameterAggregator;
import com.logpose.ph2.api.dto.HistoryDTO;
import com.logpose.ph2.api.dto.LeafParamSetDTO;
import com.logpose.ph2.api.dto.ParamSetExtendDTO;
import com.logpose.ph2.api.dto.photosynthesis.PhotosynthesisParamSetDTO;
import com.logpose.ph2.api.master.ModelMaster;
import com.logpose.ph2.api.service.ParamSetService;

/**
 * パラメータセットサービス
 *
 */
@Service
public class ParamSetServiceImpl implements ParamSetService
	{
	// ===============================================
	// クラスメンバー
	// ===============================================
	@Autowired
	private ParameterSetDomain parameterSetlDomain;
	@Autowired
	private GrowthParameterDomain growthParameterDomain;
	@Autowired
	private LeafDomain leafDomain;
	@Autowired
	private PSModelDataParameterAggregator photoSynthesisDomain;

	// ===============================================
	// パブリック関数
	// ===============================================
	// --------------------------------------------------
	/**
	 * パラメータセットリスト取得
	 *
	 * @param modelId-モデルID
	 * @return ParamSetListDTO
	 */
	// --------------------------------------------------
	@Override
	@Transactional(readOnly = true)
	public List<ParamSetExtendDTO> getParamSetList(Integer modelId)
		{
		return this.parameterSetlDomain.getParamSetList(modelId);
		}

	// --------------------------------------------------
	/**
	 * デフォルトパラメータIDの取得
	 *
	 * @param deviceId
	 * @param year
	 * @return デフォルトパラメータID
	 */
	// --------------------------------------------------
	@Override
	@Transactional(rollbackFor = Exception.class)
	public Long getDefaultParam(Integer modelId, Long deviceId, Short year)
		{
		if (ModelMaster.GROWTH == modelId)
			{
			Ph2ParamsetGrowthEntity entity = this.growthParameterDomain.getParmaters(deviceId, year);
			return entity.getParamsetId();
			}
		else if (ModelMaster.LEAF == modelId)
			{
			LeafParamSetDTO entity = this.leafDomain.getParmaters(deviceId, year);
			return entity.getId();
			}
		else if(ModelMaster.PHOTO == modelId)
			{
			PhotosynthesisParamSetDTO entity = this.photoSynthesisDomain.getParmaters(deviceId, year);
			return entity.getId();
			}
		return null;
		}

	// --------------------------------------------------
	/**
	 * パラメータセット更新履歴取得
	 *
	 * @param paramSetId
	 * @return List<HistoryDTO>
	 */
	// --------------------------------------------------
	@Override
	@Transactional(readOnly = true)
	public List<HistoryDTO> getHistory(Long paramSetId)
		{
		return this.parameterSetlDomain.getHistory(paramSetId);
		}

	// --------------------------------------------------
	/**
	 * パラメータセットの削除
	 *
	 * @param paramId
	 */
	// --------------------------------------------------
	@Override
	@Transactional(rollbackFor = Exception.class)
	public void removeParamSet(long paramId)
		{
		this.parameterSetlDomain.removeParamSet(paramId);
		}
	}
