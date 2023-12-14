package com.logpose.ph2.api.service;

import java.text.ParseException;
import java.util.Date;

import org.springframework.stereotype.Service;

import com.logpose.ph2.api.dto.PhotosynthesisParamSetDTO;
import com.logpose.ph2.api.dto.PhotosynthesisValueDTO;
import com.logpose.ph2.api.dto.RealModelGraphDataDTO;

/**
 * 光合成推定のサービス
 *
 */
@Service
public interface PhotosynthesisService
	{
	// ===============================================
	// パブリック関数(検索系)
	// ===============================================
	// --------------------------------------------------
	/**
	 * 光合成推定モデルデータ作成(バッチよりコール)
	 *
	 * @param deviceId デバイスID
	 * @param date 対象日付
	 */
	// --------------------------------------------------
	public void updateDateModel(Long deviceId, Short year, Date date) throws ParseException;
	// --------------------------------------------------
	/**
	 * 光合成推定グラフデータ取得
	 *
	 * @param deviceId-デバイスID
	 * @param year-年度
	 * @return GraphDataDTO
	 * @throws ParseException 
	 */
	// --------------------------------------------------
	public RealModelGraphDataDTO GetModelGraphData(Long deviceId, Short year) throws ParseException;

	// --------------------------------------------------
	/**
	 * 光合成推定実績値取得
	 *
	 * @param devieId-デバイスID
	 * @param date-実績の日付
	 */
	// --------------------------------------------------
	public PhotosynthesisValueDTO getRealValues(Long deviceId, Short year, Date date);
	
	// --------------------------------------------------
	/**
	 * 光合成推定パラメータセット詳細取得
	 *
	 * @param paramSetId- パラメータセットID
	 * @return PhotosynthesisParamSetDTO
	 */
	// --------------------------------------------------
	public PhotosynthesisParamSetDTO getDetailParamSet(Long paramSetId);


	// ===============================================
	// パブリック関数(更新系)
	// ===============================================
	// --------------------------------------------------
	/**
	 * 光合成推定実績値更新
	 *
	 * @param dto-PhotosynthesisValueDTO
	 */
	// --------------------------------------------------
	public void setRealValue(PhotosynthesisValueDTO dto);


	// --------------------------------------------------
	/**
	 * 光合成推定パラメータセット更新
	 *
	 * @param PhotosynthesisParamSetDTO
	 */
	// --------------------------------------------------
	public void updateParamSet(PhotosynthesisParamSetDTO dto);

	// --------------------------------------------------
	/**
	 * 光合成推定パラメータセット追加
	 *
	 * @param PhotosynthesisParamSetDTO
	 * @return 
	 */
	// --------------------------------------------------
	public Long addParamSet(PhotosynthesisParamSetDTO dto);
	
	public void setDefault(Long deviceId, Short year, Long paramId) throws ParseException;
	/**
	 * 基準パラメータセットの取得
	 *
	 * @param deviceId
	 * @param year
	 */
	PhotosynthesisParamSetDTO getDefault(Long deviceId, Short year);
	
	}
