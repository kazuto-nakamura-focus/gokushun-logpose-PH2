package com.logpose.ph2.api.service.impl;

import java.sql.Timestamp;
import java.text.ParseException;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.logpose.ph2.api.dao.db.entity.Ph2RealPsAmountEntity;
import com.logpose.ph2.api.domain.photosynthesis.PhotoGraphDomain;
import com.logpose.ph2.api.domain.photosynthesis.PhotoSynthesisDomain;
import com.logpose.ph2.api.dto.PhotosynthesisParamSetDTO;
import com.logpose.ph2.api.dto.PhotosynthesisValueDTO;
import com.logpose.ph2.api.dto.RealModelGraphDataDTO;
import com.logpose.ph2.api.service.PhotosynthesisService;

/**
 * 光合成推定のサービス
 *
 */
@Service
public class PhotosynthesisServiceImpl implements PhotosynthesisService
	{
	// ===============================================
	// パブリック関数
	// ===============================================
	@Autowired
	private PhotoSynthesisDomain photoSynthesisDomain;
	
	@Autowired
	private PhotoGraphDomain graphDomain;

	// --------------------------------------------------
	/**
	 * 光合成推定グラフデータ取得
	 *
	 * @param fieldId
	 *            圃場ID
	 * @param year
	 *            年度
	 * @return GraphDataDTO
	 * @throws ParseException 
	 */
	// --------------------------------------------------
	@Override
	@Transactional(rollbackFor = Exception.class)
	public RealModelGraphDataDTO GetModelGraphData(Long deviceId, Short year) throws ParseException
		{
		return this.graphDomain.getModelGraph(deviceId, year);
		}
	// --------------------------------------------------
	/**
	 * 光合成推定実績値取得
	 *
	 * @param devieId
	 */
	// --------------------------------------------------
	@Override
	public PhotosynthesisValueDTO getRealValues(Long deviceId, Short year, Date date)
		{
		PhotosynthesisValueDTO result = new PhotosynthesisValueDTO();
		List<Ph2RealPsAmountEntity> records =
				this.photoSynthesisDomain.getRealPsAmountEntity(deviceId, year, date);
		if (records.size() > 0)
			{
			Ph2RealPsAmountEntity entity = records.get(0);
			result.setDeviceId(deviceId);
			result.setF(entity.getValueF());
			result.setG(entity.getValueG());
			}
		return result;
		}

	// --------------------------------------------------
	/**
	 * 光合成推定実績値更新
	 *
	 * @param dto
	 *            PhotosynthesisValueDTO
	 */
	// --------------------------------------------------
	@Override
	@Transactional(rollbackFor = Exception.class)
	public void setRealValue(PhotosynthesisValueDTO dto)
		{
		Ph2RealPsAmountEntity entity = new Ph2RealPsAmountEntity();
		entity.setDeviceId(dto.getDeviceId());
		entity.setValueF(dto.getF());
		entity.setValueG(dto.getG());
		entity.setCreatedAt(new Timestamp(System.currentTimeMillis()));
		entity.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
		this.photoSynthesisDomain.update(entity);
		}

	// --------------------------------------------------
	/**
	 * 光合成推定パラメータセット詳細取得
	 *
	 * @param paramSetId
	 *            パラメータセットID
	 * @return PhotosynthesisParamSetDTO
	 * @throws JsonProcessingException
	 * @throws JsonMappingException
	 */
	// --------------------------------------------------
	@Override
	public PhotosynthesisParamSetDTO getDetailParamSet(Long paramSetId)
		{
		return this.photoSynthesisDomain.getDetail(paramSetId);
		}

	// --------------------------------------------------
	/**
	 * 光合成推定パラメータセット更新
	 *
	 * @param dto
	 *            PhotosynthesisParamSetDTO
	 */
	// --------------------------------------------------
	@Override
	@Transactional(rollbackFor = Exception.class)
	public void updateParamSet(PhotosynthesisParamSetDTO dto)
		{
		this.photoSynthesisDomain.updateParamSet(dto);
		}

	// --------------------------------------------------
	/**
	 * 光合成推定パラメータセット追加
	 *
	 * @param dto
	 *            PhotosynthesisParamSetDTO
	 * @return 
	 */
	// --------------------------------------------------
	@Override
	@Transactional(rollbackFor = Exception.class)
	public Long addParamSet(PhotosynthesisParamSetDTO dto)
		{
		return this.photoSynthesisDomain.addParamSet(null, dto);
		}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void updateDateModel(Long deviceId, Short year, Date date) throws ParseException
		{
		this.photoSynthesisDomain.updateModelTable(deviceId, year, date);
		}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void setDefault(Long deviceId, Short year, Long paramId) throws ParseException
		{
		this.photoSynthesisDomain.setDefault(deviceId, year, paramId);
		
		}
	// --------------------------------------------------
	/**
	 * 基準パラメータセットの取得
	 *
	 * @param deviceId
	 * @param year
	 */
	// --------------------------------------------------
	@Override
	public PhotosynthesisParamSetDTO getDefault(Long deviceId, Short year)
		{
// * デフォルトパラメータの取得
		return this.photoSynthesisDomain.getParmaters(deviceId, year);
		}

	}
