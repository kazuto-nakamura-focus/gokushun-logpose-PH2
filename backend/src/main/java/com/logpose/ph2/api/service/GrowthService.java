package com.logpose.ph2.api.service;

import java.text.ParseException;
import java.util.Date;
import java.util.List;

import com.logpose.ph2.api.dao.db.entity.Ph2RealGrowthFStageEntity;
import com.logpose.ph2.api.dto.EventDaysDTO;
import com.logpose.ph2.api.dto.FDataListDTO;
import com.logpose.ph2.api.dto.GrowthParamSetDTO;
import com.logpose.ph2.api.dto.ValueDateDTO;
import com.logpose.ph2.api.dto.graph.ModelGraphDataDTO;
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
	// ###############################################
	/**
	 * 生育推定モデルグラフデータ取得
	 *    モデルテーブルから検索してデータを取得する
	 *
	 * @param deviceId-デバイスID
	 * @param year-対象年度
	 * @return GraphDataDTO
	 * @throws ParseException 
	 */
	// ###############################################
	public ModelGraphDataDTO getModelGraph(Long deviceId, Short year) throws ParseException;
	// ###############################################
	/**
	 * 生育推定モデルグラフデータ取得
	 *    デイリーデータをベースに再計算をしてデータを取得する
	 *
	 * @param deviceId デバイスID
	 * @param year 対象年度
	 * @param paramId パラメータID
	 * @return GraphDataDTO
	 * @throws ParseException 
	 */
	// ###############################################
/*	public ModelGraphDataDTO getSumilateGraph(
			Long deviceId, Short year, Long paramId) throws ParseException;*/
	
	// ###############################################
	/**
	 * 生育推定イベントデータ取得
	 *
	 * @param deviceId デバイスID
	 * @param year 年度
	 * @return EventDaysDTO
	 */
	// ###############################################
	public List<EventDaysDTO> getEvent(Long deviceId, Short year);

	// ###############################################
	/**
	 * 生育推定F値データ取得
	 *
	 * @param deviceId デバイスID
	 * @param year 年度
	 * @return List<Ph2RealGrowthFStageEntity>
	 * @throws ParseException 
	 */
	// ###############################################
	public List<Ph2RealGrowthFStageEntity> getAllF(Long deviceId, Short year) throws ParseException;

	// ###############################################
	/**
	 * 生育推定実績値取得
	 *
	 * @param deviceId デバイスID
	 * @param date 日付
	 * @return ValueDateDTO
	 * @throws Exception 
	 */
	// ###############################################
	public ValueDateDTO getFData(Long deviceId, Date date) throws Exception;

	// ###############################################
	/**
	 * 生育推定実績値更新
	 *
	 * @param dto FDataListDTO
	 */
	// ###############################################
	public void updateFData(FDataListDTO dto);

	// ###############################################
	/**
	 * 生育推定パラメータセット詳細取得
	 *
	 * @param paramSetId パラメータセットID
	 * @return GrowthParamSetDTO
	 */
	// ###############################################
	public GrowthParamSetDTO getDetail(Long paramSetId);

	// ###############################################
	/**
	 * デフォルト値の設定
	 * 
	 * @param deviceId デバイスID
	 * @param date 日付
	 * @param paramId パラメータセットID
	 * @throws ParseException 
	 */
	// ###############################################
	public void setDefault(Long deviceId, Short year, Long paramId) throws ParseException;
	
	// ###############################################
	/**
	 * 生育推定パラメータセット更新
	 *
	 * @param dto GrowthParamSetDTO
	 * @throws ParseException 
	 */
	// ###############################################
	public void updateParamSet(GrowthParamSetDTO dto) throws ParseException;

	// ###############################################
	/**
	 * 生育推定パラメータセット追加
	 *
	 * @param dto GrowthParamSetDTO
	 * @return パラメータセットID
	 */
	// ###############################################
	public Long addParamSet(GrowthParamSetDTO dto);
	
	// ###############################################
	/**
	 * 日付からF値の情報を得る
	 *
	 * @param id 
	 * @param date
	 * @throws ParseException 
	 */
	// ###############################################
	public FValuesDTO checkFValueByDate(Long id, String date) throws ParseException;

	}
