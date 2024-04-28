package com.logpose.ph2.api.service;

import java.text.ParseException;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.logpose.ph2.api.dto.LeafParamSetDTO;
import com.logpose.ph2.api.dto.graph.ModelGraphDataDTO;
import com.logpose.ph2.api.dto.leaf.LeafAreaValueDTO;
import com.logpose.ph2.api.dto.leaf.LeafAreaValueListDTO;
import com.logpose.ph2.api.dto.leaf.LeafShootDTO;

/**
 * 葉面積・葉枚数のサービス
 *
 */

public interface LeafService
	{
	// ===============================================
	// パブリック関数（検索系)
	// ===============================================
	// ###############################################
	/**
	 * 葉面積モデルグラフデータ取得
	 *
	 * @param deviceId-デバイスID
	 * @param year-対象年度
	 * @return LeafGraphDataDTO
	 * @throws ParseException 
	 */
	// ###############################################
	public List<ModelGraphDataDTO> getLeaflGraphData(Long deviceId, short year) throws ParseException;

	// ###############################################
	/**
	 * 新梢数取得処理
	 *
	 * @param deviceId デバイスID
	 * @param year 対象年度
	 * @throws ParseException 
	 */
	// ###############################################
	public LeafShootDTO getShootCount(Long deviceId, Short year) throws ParseException;

	// ###############################################
	/**
	 * 葉面積・葉枚数パラメータセット詳細取得
	 *
	 * @param paramSetId パラメータセットID
	 * @return LeafParamSetDTO
	 * @throws JsonProcessingException
	 * @throws JsonMappingException
	 */
	// ###############################################
	public LeafParamSetDTO getDetailParamSet(Long paramSetId) throws JsonMappingException, JsonProcessingException;

	// ###############################################
	/**
	 * 新梢辺り葉枚数・平均個葉面積定義検索処理
	 *
	 * @param deviceId
	 * @param year
	 * @throws ParseException 
	 */
	// ###############################################
	LeafAreaValueListDTO getAllAreaAndCount(Long deviceId, Short year) throws ParseException;

	// ###############################################
	/**
	 * モデル値検索
	 *
	 * @param deviceId データを表示するデバイスのID
	 * @param date 実績の日付
	 * 	@param date 対象日付
	 * @return モデル値
	 * @throws ParseException
	 */
	// ###############################################
	public Double getModelValue(Long deviceId, Short year, Date date) throws ParseException;

	// ===============================================
	// パブリック関数（更新系)
	// ===============================================
	// ###############################################
	/**
	 * 新梢数登録処理
	 *
	 * @param deviceId デバイスID
	 * @param year 対象年度
	 * @param dto 登録データ
	 * @throws ParseException
	 */
	// ###############################################
	public void addShoot(Long deviceId, Short year, LeafShootDTO dto) throws ParseException;

	// ###############################################
	/**
	 * 新梢辺り葉枚数・平均個葉面積登録処理
	 *
	 * @param dto LeafvaluesDTO
	 * @return ResponseDTO (null)
	 * @throws ParseException
	 */
	// ###############################################
	public void setAreaAndCount(Long deviceId, Short year, List<LeafAreaValueDTO> values) throws ParseException;

	// ###############################################
	/**
	 * 基準パラメータセットの設定
	 * @param deviceId デバイスID
	 * @param year 対象年度
	 * @param paramSetId パラメータセットID
	 * @throws ParseException 
	 */
	// ###############################################
	public void setDefault(Long deviceId, Short year, Long paramId) throws ParseException;

	// ###############################################
	/**
	 * 葉面積・葉枚数パラメータセット更新
	 *
	 * @param dto LeafParamSetDTO
	 * @return ResponseDTO (null)
	 * @throws ParseException 
	 */
	// ###############################################
	public void updateParamSet(LeafParamSetDTO dto) throws ParseException;

	// ###############################################
	/**
	 * 葉面積・葉枚数パラメータセット追加
	 *
	 * @param dto LeafParamSetDTO
	 * @return ResponseDTO (null)
	 */
	// ###############################################
	public Long addParamSet(LeafParamSetDTO dto);

	}
