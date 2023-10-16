package com.logpose.ph2.api.service;

import java.util.List;

import com.logpose.ph2.api.dto.HistoryDTO;
import com.logpose.ph2.api.dto.ParamSetExtendDTO;

/**
 * パラメータセットサービス
 *
 */
public interface ParamSetService
	{
	// ===============================================
	// パブリック関数
	// ===============================================
	// --------------------------------------------------
	/**
	 * パラメータセットリスト取得
	 *
	 * @param modelId モデルID
	 * @return ParamSetListDTO
	 */
	// --------------------------------------------------
	public List<ParamSetExtendDTO> getParamSetList(Integer modelId);

	public Long getDefaultParam(Integer modelId, Long deviceId, Short year);
	// --------------------------------------------------
	/**
	 * パラメータセット更新履歴取得
	 *
	 * @param Integer paramSetId
	 * @return List<HistoryDTO>
	 */
	// --------------------------------------------------
	public List<HistoryDTO> getHistory(Long paramSetId);

	/**
	 * パラメータセットの削除
	 *
	 * @param paramId
	 */
	void removeParamSet(long paramId);

	}
