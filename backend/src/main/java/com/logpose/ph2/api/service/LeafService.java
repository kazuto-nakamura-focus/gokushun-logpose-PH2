package com.logpose.ph2.api.service;

import java.text.ParseException;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.logpose.ph2.api.dto.LeafParamSetDTO;
import com.logpose.ph2.api.dto.LeafShootDTO;
import com.logpose.ph2.api.dto.LeafvaluesDTO;
import com.logpose.ph2.api.dto.RealModelGraphDataDTO;

/**
 * 葉面積・葉枚数のサービス
 *
 */

public interface LeafService
	{
	// ===============================================
	// パブリック関数（検索系)
	// ===============================================
	// --------------------------------------------------
	/**
	 * 葉面積・葉枚数モデルデータ作成(バッチよりコール)
	 *
	 * @param deviceId デバイスID
	 * @pram year 対象年度
	 * @param date 対象日付
	 */
	// --------------------------------------------------
	public void updateDateModel(Long deviceId, Short year, Date date) throws ParseException;

	// --------------------------------------------------
	/**
	 * 葉面積モデルグラフデータ取得
	 *
	 * @param deviceId-デバイスID
	 * @param year-対象年度
	 * @param paramId-パラメータID
	 * @return LeafGraphDataDTO
	 * @throws ParseException 
	 */
	// --------------------------------------------------
	public List<RealModelGraphDataDTO> getLeaflGraphData(Long deviceId, short year) throws ParseException;

	// --------------------------------------------------
	/**
	 * 新梢数取得処理
	 *
	 * @param deviceId デバイスID
	 * @param year 対象年度
	 * @param date 対象日付
	 * @throws ParseException 
	 */
	// --------------------------------------------------
	public LeafShootDTO getShootCount(Long deviceId, Short year, Date date) throws ParseException;

	// --------------------------------------------------
	/**
	 * 新梢辺り葉枚数・平均個葉面積検索処理
	 *
	 * @param deviceId-データを表示するデバイスのID
	 * @param date-実績の日付
	 * 	@param date 対象日付
	 * @throws ParseException
	 */
	// --------------------------------------------------
	public LeafvaluesDTO getAreaAndCount(Long deviceId, Short year, Date date) throws ParseException;

	// --------------------------------------------------
	/**
	 * 葉面積・葉枚数パラメータセット詳細取得
	 *
	 * @param paramSetId
	 *            パラメータセットID
	 * @return LeafParamSetDTO
	 * @throws JsonProcessingException
	 * @throws JsonMappingException
	 */
	// --------------------------------------------------
	public LeafParamSetDTO getDetailParamSet(Long paramSetId) throws JsonMappingException, JsonProcessingException;

	// --------------------------------------------------
	/**
	 * 新梢辺り葉枚数・平均個葉面積定義検索処理
	 *
	 * @param deviceId
	 * @param year
	 * @throws ParseException
	 */
	// --------------------------------------------------
	List<LeafvaluesDTO> getAllAreaAndCount(Long deviceId, Short year);

	// --------------------------------------------------
	/**
	 * 基準パラメータセットの取得
	 *
	 * @param deviceId
	 * @param year
	 */
	// --------------------------------------------------
	public LeafParamSetDTO getDefault(Long deviceId, Short year);

	// ===============================================
	// パブリック関数（更新系)
	// ===============================================
	// --------------------------------------------------
	/**
	 * 新梢数登録処理
	 *
	 * @param dto
	 *            - LeafShootDTO
	 * @throws ParseException
	 */
	// --------------------------------------------------
	public void addShoot(LeafShootDTO dto) throws ParseException;

	// --------------------------------------------------
	/**
	 * 新梢辺り葉枚数・平均個葉面積登録処理
	 *
	 * @param dto LeafvaluesDTO
	 * @return ResponseDTO (null)
	 * @throws ParseException
	 */
	// --------------------------------------------------
	public void setAreaAndCount(LeafvaluesDTO dto) throws ParseException;

	// --------------------------------------------------
	/**
	 * 基準パラメータセットの設定
	 *
	 * @throws ParseException 
	 */
	// --------------------------------------------------
	public void setDefault(Long deviceId, Short year, Long paramId) throws ParseException;

	// --------------------------------------------------
	/**
	 * 葉面積・葉枚数パラメータセット更新
	 *
	 * @param dto
	 *            LeafParamSetDTO
	 * @return ResponseDTO (null)
	 */
	// --------------------------------------------------
	public void updateParamSet(LeafParamSetDTO dto);

	// --------------------------------------------------
	/**
	 * 葉面積・葉枚数パラメータセット追加
	 *
	 * @param dto
	 *            LeafParamSetDTO
	 * @return ResponseDTO (null)
	 */
	// --------------------------------------------------
	public Long addParamSet(LeafParamSetDTO dto);

	}
