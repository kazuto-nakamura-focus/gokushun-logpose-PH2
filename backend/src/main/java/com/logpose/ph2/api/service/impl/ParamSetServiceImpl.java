package com.logpose.ph2.api.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.logpose.ph2.api.domain.ParameterSetDomain;
import com.logpose.ph2.api.dto.HistoryDTO;
import com.logpose.ph2.api.dto.ParamSetDTO;
import com.logpose.ph2.api.service.ParamSetService;

/**
 * パラメータセットサービス
 *
 */
@Service
public class ParamSetServiceImpl implements  ParamSetService
	{
	// ===============================================
	// クラスメンバー
	// ===============================================
	@Autowired
	private ParameterSetDomain parameterSetlDomain;
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
	public List<ParamSetDTO> getParamSetList(Integer modelId)
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
	@Transactional(readOnly = true)
	public Long getDefaultParam(Integer modelId, Long deviceId, Short year)
		{
		return this.parameterSetlDomain.getDefaultParam(modelId, deviceId, year);
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
