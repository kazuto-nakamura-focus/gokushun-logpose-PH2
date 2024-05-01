package com.logpose.ph2.api.service;

import java.text.ParseException;
import java.util.List;

import org.springframework.stereotype.Service;

import com.logpose.ph2.api.dto.graph.ModelGraphDataDTO;
import com.logpose.ph2.api.dto.photosynthesis.PhotosynthesisDetailDTO;
import com.logpose.ph2.api.dto.photosynthesis.PhotosynthesisParamSetDTO;
import com.logpose.ph2.api.dto.photosynthesis.PhotosynthesisValueDTO;

/**
 * 光合成推定のサービス
 *
 */
@Service
public interface PhotosynthesisService
	{
	// ===============================================
	// 公開関数群
	// ===============================================
	// ###############################################
	/**
	 * 光合成推定グラフデータ取得
	 *
	 * @param deviceId-デバイスID
	 * @param year-年度
	 * @return ModelGraphDataDTO
	 * @throws ParseException 
	 */
	// ###############################################
	public ModelGraphDataDTO GetModelGraphData(Long deviceId, Short year) throws ParseException;
	// ###############################################
	/**
	 * モデルデータの更新
	 *
	 * @param deviceId デバイスID
	 * @param year 年度
	 */
	// ###############################################
	public void updateDateModel(Long deviceId, Short year);

	// ###############################################
	/**
	 * 光合成推定実績値取得
	 *
	 * @param devieId-デバイスID
	 * @param year 年度
	 * @throws ParseException 
	 */
	// ###############################################
	public PhotosynthesisDetailDTO getRealValues(Long deviceId, Short year) throws ParseException;

	// ###############################################
	/**
	 * 光合成推定パラメータセット詳細取得
	 *
	 * @param paramSetId パラメータセットID
	 * @return PhotosynthesisParamSetDTO 光合成推定パラメータセット詳細
	 */
	// ###############################################
	public PhotosynthesisParamSetDTO getDetailParamSet(Long paramSetId);

	// ###############################################
	/**
	 * 光合成推定実績値更新
	 *
	 * @param records PhotosynthesisValueDTO 更新データリスト
	 * @throws ParseException
	 */
	// ###############################################
	public void setRealValue(List<PhotosynthesisValueDTO> dto) throws ParseException;

	// ###############################################
	/**
	 * 光合成推定パラメータセット更新
	 *
	 * @param dto PhotosynthesisParamSetDTO 更新データ
	 */
	// ###############################################
	public void updateParamSet(PhotosynthesisParamSetDTO dto);

	// ###############################################
	/**
	 * 光合成推定パラメータセット追加
	 *
	 * @param PhotosynthesisParamSetDTO 更新データ
	 * @return 追加されたパラメータセットのID
	 */
	// ###############################################
	public Long addParamSet(PhotosynthesisParamSetDTO dto);

	// ###############################################
	/**
	 * 基準パラメータセットの設定
	 *
	 * @param deviceId デバイスID
	 * @param year 年度
	 * @param paramId パラメータセットID
	 * @throws ParseException
	 */
	// ###############################################
	public void setDefault(Long deviceId, Short year, Long paramId) throws ParseException;
	}
