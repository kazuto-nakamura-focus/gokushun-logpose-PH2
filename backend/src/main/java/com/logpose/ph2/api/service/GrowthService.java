package com.logpose.ph2.api.service;

import java.text.ParseException;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.logpose.ph2.api.dao.db.entity.Ph2ParamsetGrowthEntity;
import com.logpose.ph2.api.dao.db.entity.Ph2RealGrowthFStageEntity;
import com.logpose.ph2.api.dto.EventDaysDTO;
import com.logpose.ph2.api.dto.FDataListDTO;
import com.logpose.ph2.api.dto.GrowthParamSetDTO;
import com.logpose.ph2.api.dto.RealModelGraphDataDTO;
import com.logpose.ph2.api.dto.ValueDateDTO;
import com.logpose.ph2.api.dto.growth.FValuesDTO;

/**
 * 光合成推定のサービス
 *
 */
public interface GrowthService
	{
	// ===============================================
	// パブリック関数
	// ===============================================
	// --------------------------------------------------
	/**
	 * 生育推定モデルデータの作成
	 *
	 * @param deviceId デバイスID
	 * @param date 対象日付
	 */
	// --------------------------------------------------
	public void updateDateModel(Long deviceId, Short year, Date date) throws ParseException;
	// --------------------------------------------------
	/**
	 * パラメータ指定による生育推定モデルデータの作成
	 *
	 * @param deviceId デバイスID
	 * @param date 対象日付
	 * @param paramId パラメータセットID
	 * @throws ParseException 
	 */
	// --------------------------------------------------
	public void setDefaultParamSet(Long deviceId, Short year, Long paramId) throws ParseException;
	// --------------------------------------------------
	/**
	 * 生育推定モデルグラフデータ取得
	 *    モデルテーブルから検索してデータを取得する
	 *
	 * @param deviceId-デバイスID
	 * @param year-対象年度
	 * @return GraphDataDTO
	 * @throws ParseException 
	 */
	// --------------------------------------------------
	public RealModelGraphDataDTO getModelGraph(Long deviceId, Short year) throws ParseException;
	// --------------------------------------------------
	/**
	 * 生育推定モデルグラフデータ取得
	 *    デイリーデータをベースに再計算をしてデータを取得する
	 *
	 * @param deviceId-デバイスID
	 * @param year-対象年度
	 * @param paramId-パラメータID
	 * @return GraphDataDTO
	 * @throws ParseException 
	 */
	// --------------------------------------------------
	public RealModelGraphDataDTO getSumilateGraph(
			Long deviceId, Short year, Long paramId) throws ParseException;
	
	// --------------------------------------------------
	/**
	 * デフォルトパラメータの変更
	 *
	 * @param paramId -パラメータセットID
	 */
	// --------------------------------------------------
	public void setParameter(Long deviceId, Short year, Long paramId) throws ParseException;
	// --------------------------------------------------
	/**
	 * 生育推定イベントデータ取得
	 *
	 * @param fieldId 圃場ID
	 * @param year 年度
	 * @param paramSetId パラメータセットID
	 * @return EventDaysDTO
	 */
	// --------------------------------------------------
	public List<EventDaysDTO> getEvent(Long deviceId, Short year);

	// --------------------------------------------------
	/**
	 * 生育推定F値データ取得
	 *
	 * @param deviceId デバイスID
	 * @param year 年度
	 * @return List<Ph2RealGrowthFStageEntity>
	 * @throws ParseException 
	 */
	// --------------------------------------------------
	public List<Ph2RealGrowthFStageEntity> getAllF(Long deviceId, Short year) throws ParseException;

	// --------------------------------------------------
	/**
	 * 生育推定実績値取得
	 *
	 * @param deviceId デバイスID
	 * @param date 日付
	 * @return F値
	 * @throws ParseException 
	 * @throws Exception 
	 */
	// --------------------------------------------------
	public ValueDateDTO getFData(Long deviceId, Date date) throws Exception;

	// --------------------------------------------------
	/**
	 * 生育推定実績値更新
	 *
	 * @param dto FDataListDTO
	 */
	// --------------------------------------------------
	public void updateFData(FDataListDTO dto);

	// --------------------------------------------------
	/**
	 * 生育推定パラメータセット詳細取得
	 *
	 * @param paramSetId パラメータセットID
	 * @return GrowthParamSetDTO
	 * @throws JsonProcessingException
	 * @throws JsonMappingException
	 */
	// --------------------------------------------------
	public GrowthParamSetDTO getDetail(Long paramSetId);

	// --------------------------------------------------
	/**
	 * デフォルト値の設定
	 *
	 * @param paramId パラメータセットID
	 * @throws ParseException 
	 */
	// --------------------------------------------------
	public void setDefault(Long deviceId, Short year, Long paramId) throws ParseException;
	
	// --------------------------------------------------
	/**
	 * 生育推定パラメータセット更新
	 *
	 * @param dto GrowthParamSetDTO
	 * @throws ParseException 
	 */
	// --------------------------------------------------
	public void updateParamSet(GrowthParamSetDTO dto) throws ParseException;

	// --------------------------------------------------
	/**
	 * 生育推定パラメータセット追加
	 *
	 * @param dto GrowthParamSetDTO
	 * @return 
	 */
	// --------------------------------------------------
	public Long addParamSet(GrowthParamSetDTO dto);
	
	// --------------------------------------------------
	/**
	 * 基準パラメータセットの取得
	 *
	 * @param deviceId
	 * @param year
	 */
	// --------------------------------------------------
	public Ph2ParamsetGrowthEntity getDefault(Long deviceId, Short year);
	// --------------------------------------------------
	/**
	 * 日付からF値の情報を得る
	 *
	 * @param id
	 * @param date
	 */
	// --------------------------------------------------
	public FValuesDTO checkFValueByDate(Long id, Date date);

	}
