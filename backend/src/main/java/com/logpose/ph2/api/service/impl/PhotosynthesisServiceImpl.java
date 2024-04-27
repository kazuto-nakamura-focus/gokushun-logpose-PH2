package com.logpose.ph2.api.service.impl;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.logpose.ph2.api.algorythm.DateTimeUtility;
import com.logpose.ph2.api.dao.db.entity.Ph2RealPsAmountEntity;
import com.logpose.ph2.api.domain.DeviceDomain;
import com.logpose.ph2.api.domain.photosynthesis.PhotoGraphDomain;
import com.logpose.ph2.api.domain.photosynthesis.PhotoSynthesisDomain;
import com.logpose.ph2.api.dto.device.DeviceTermDTO;
import com.logpose.ph2.api.dto.graph.ModelGraphDataDTO;
import com.logpose.ph2.api.dto.photosynthesis.PhotosynthesisDetailDTO;
import com.logpose.ph2.api.dto.photosynthesis.PhotosynthesisParamSetDTO;
import com.logpose.ph2.api.dto.photosynthesis.PhotosynthesisValueDTO;
import com.logpose.ph2.api.service.PhotosynthesisService;

/**
 * 光合成推定のサービス
 *
 */
@Service
public class PhotosynthesisServiceImpl implements PhotosynthesisService
	{
	// ===============================================
	// 公開関数群
	// ===============================================
	@Autowired
	private PhotoSynthesisDomain photoSynthesisDomain;

	@Autowired
	private PhotoGraphDomain graphDomain;
	
	@Autowired
	private DeviceDomain deviceDomain;
	// ===============================================
	// 公開関数群
	// ===============================================
	// ###############################################
	/**
	 * 光合成推定グラフデータ取得
	 *
	 * @param deviceId デバイスID
	 * @param year 対象年度
	 * @return GraphDataDTO
	 */
	// ###############################################
	@Override
	@Transactional(rollbackFor = Exception.class)
	public ModelGraphDataDTO GetModelGraphData(Long deviceId, Short year) throws ParseException
		{
		return this.graphDomain.getModelGraph(deviceId, year);
		}

	// ###############################################
	/**
	 * モデルデータの更新
	 *
	 * @param deviceId デバイスID
	 * @param year 対象年度
	 * @param year 年度
	 */
	// ###############################################
	@Override
	@Transactional(rollbackFor = Exception.class)
	public void updateDateModel(Long deviceId, Short year)
		{
		this.photoSynthesisDomain.updateModelTable(deviceId, year);
		}

	// ###############################################
	/**
	 * 光合成推定実績値取得
	 *
	 * @param deviceId デバイスID
	 * @param year 対象年度
	 * @throws ParseException 
	 */
	// ###############################################
	@Override
	public PhotosynthesisDetailDTO getRealValues(Long deviceId, Short year) throws ParseException
		{
		PhotosynthesisDetailDTO result = new PhotosynthesisDetailDTO();
		List<PhotosynthesisValueDTO> values = new ArrayList<>();
		List<Ph2RealPsAmountEntity> records = this.photoSynthesisDomain.getRealPsAmountEntity(deviceId, year);
		for(final Ph2RealPsAmountEntity entity : records)
			{
			PhotosynthesisValueDTO item = new PhotosynthesisValueDTO();
			item.setDate(DateTimeUtility.getStringFromDate(entity.getDate()));
			item.setDeviceId(deviceId);
			item.setF(entity.getValueF());
			item.setG(entity.getValueG());
			values.add(item);
			}
		DeviceTermDTO term = this.deviceDomain.getTerm(deviceId, year);
		result.setValues(values);
		result.setMaxDate(term.getEndDate());
		result.setMinDate(term.getStartDate());
		return result;
		}

	// ###############################################
	/**
	 * 光合成推定パラメータセット詳細取得
	 *
	 * @param paramSetId パラメータセットID
	 * @return PhotosynthesisParamSetDTO 光合成推定パラメータセット詳細
	 */
	// ###############################################
	@Override
	public PhotosynthesisParamSetDTO getDetailParamSet(Long paramSetId)
		{
		return this.photoSynthesisDomain.getDetail(paramSetId);
		}

	// ###############################################
	/**
	 * 光合成推定実績値更新
	 *
	 * @param records PhotosynthesisValueDTO 更新データリスト
	 * @throws ParseException
	 */
	// ###############################################
	@Override
	@Transactional(rollbackFor = Exception.class)
	public void setRealValue(List<PhotosynthesisValueDTO> records) throws ParseException
		{
		this.photoSynthesisDomain.update(records);
		}
	// ###############################################
	/**
	 * 光合成推定パラメータセット更新
	 *
	 * @param dto PhotosynthesisParamSetDTO 更新データ
	 */
	// ###############################################
	@Override
	@Transactional(rollbackFor = Exception.class)
	public void updateParamSet(PhotosynthesisParamSetDTO dto)
		{
		this.photoSynthesisDomain.updateParamSet(dto);
		}
	// ###############################################
	/**
	 * 光合成推定パラメータセット追加
	 *
	 * @param PhotosynthesisParamSetDTO 更新データ
	 * @return 追加されたパラメータセットのID
	 */
	// ###############################################
	@Override
	@Transactional(rollbackFor = Exception.class)
	public Long addParamSet(PhotosynthesisParamSetDTO dto)
		{
		return this.photoSynthesisDomain.addParamSet(null, dto);
		}

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
	@Override
	@Transactional(rollbackFor = Exception.class)
	public void setDefault(Long deviceId, Short year, Long paramId) throws ParseException
		{
		this.photoSynthesisDomain.setDefault(deviceId, year, paramId);
		}
	}
