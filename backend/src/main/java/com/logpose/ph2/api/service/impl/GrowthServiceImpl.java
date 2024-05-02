package com.logpose.ph2.api.service.impl;

import java.text.ParseException;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.logpose.ph2.api.dao.db.entity.Ph2RealGrowthFStageEntity;
import com.logpose.ph2.api.domain.growth.FValueDomain;
import com.logpose.ph2.api.domain.growth.GrowthDomain;
import com.logpose.ph2.api.domain.growth.GrowthGraphDomain;
import com.logpose.ph2.api.domain.growth.GrowthParameterDomain;
import com.logpose.ph2.api.domain.leaf.LeafDomain;
import com.logpose.ph2.api.domain.photosynthesis.PhotoSynthesisDomain;
import com.logpose.ph2.api.dto.EventDaysDTO;
import com.logpose.ph2.api.dto.FDataListDTO;
import com.logpose.ph2.api.dto.GrowthParamSetDTO;
import com.logpose.ph2.api.dto.ValueDateDTO;
import com.logpose.ph2.api.dto.graph.ModelGraphDataDTO;
import com.logpose.ph2.api.dto.growth.FValuesDTO;
import com.logpose.ph2.api.service.GrowthService;

/**
 * 生育推定のサービス
 *
 */
@Service
public class GrowthServiceImpl implements GrowthService
	{
	// ===============================================
	// クラスメンバー
	// ===============================================
	@Autowired
	private GrowthDomain growthDomain;
	@Autowired
	private LeafDomain leafDomain;
	@Autowired
	private FValueDomain fValueDomain;
	@Autowired
	private GrowthGraphDomain growthGraphDomain;
	@Autowired
	private PhotoSynthesisDomain photoSynthesisDomain;
	@Autowired
	private GrowthParameterDomain growthParameterDomain;

	// ===============================================
	// パブリック関数群（検索系)
	// ===============================================
	// ###############################################
	/**
	 * 生育推定モデルグラフデータ取得
	 *    モデルテーブルから検索してデータを取得する
	 *
	 * @param deviceId デバイスID
	 * @param year 対象年度
	 * @return ModelGraphDataDTO
	 */
	// ###############################################
	@Override
	@Transactional(readOnly = true)
	public ModelGraphDataDTO getModelGraph(Long deviceId, Short year)
		{
		return this.growthGraphDomain.getModelGraph(deviceId, year);
		}

	// ###############################################
	/**
	 * 生育推定イベントデータ取得
	 *
	 * @param deviceId デバイスID
	 * @param year 年度
	 * @return EventDaysDTO
	 */
	// ###############################################
	@Override
	@Transactional(readOnly = true)
	public List<EventDaysDTO> getEvent(Long deviceId, Short year)
		{
		return this.growthGraphDomain.getEvent(deviceId, year);
		}

	// ###############################################
	/**
	 * 生育推定F値データ取得
	 *
	 * @param deviceId デバイスID
	 * @param year 年度
	 * @return List<Ph2RealGrowthFStageEntity>
	 */
	// ###############################################
	@Override
	@Transactional(readOnly = true)
	public List<Ph2RealGrowthFStageEntity> getAllF(Long deviceId, Short year)
		{
		return this.growthDomain.getALlFValus(deviceId, year);
		}

	// ###############################################
	/**
	 * 生育推定実績値取得
	 *
	 * @param deviceId デバイスID
	 * @param date 日付
	 * @return ValueDateDTO
	 */
	// ###############################################
	@Override
	@Transactional(readOnly = true)
	public ValueDateDTO getFData(Long deviceId, Date date)
		{
		return this.growthDomain.getRealFData(deviceId, date);
		}
	// ===============================================
	// パブリック関数群（更新系)
	// ===============================================
	// ###############################################
	/**
	 * 生育推定実績値更新
	 *
	 * @param dto FDataListDTO
	 */
	// ###############################################
	@Override
	@Transactional(rollbackFor = Exception.class)
	public void updateFData(FDataListDTO dto)
		{
		this.growthDomain.updateFValues(dto);
		
		}

	// --------------------------------------------------
	/**
	 * デフォルトパラメータの変更
	 *
	 * @param deviceId デバイスID
	 * @param year 年度
	 * @param paramId -パラメータセットID
	 * @throws ParseException 
	 */
	// --------------------------------------------------
/*
 * @Override
 * @Transactional(rollbackFor = Exception.class)
 * public void setParameter(Long deviceId, Short year, Long paramId) throws
 * ParseException
 * {
 * this.growthDomain.setDefault(deviceId, year, paramId);
 * }
 */

	// ###############################################
	/**
	 * 生育推定パラメータセット詳細取得
	 *
	 * @param paramSetId パラメータセットID
	 * @return GrowthParamSetDTO
	 */
	// ###############################################
	@Override
	@Transactional(rollbackFor = Exception.class)
	public GrowthParamSetDTO getDetail(Long paramSetId)
		{
		return this.growthParameterDomain.getDetail(paramSetId);
		}

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
	@Override
	@Transactional(rollbackFor = Exception.class)
	public void setDefault(Long deviceId, Short year, Long paramId)
			throws ParseException
		{
		this.growthParameterDomain.setDefault(deviceId, year, paramId);
		this.growthDomain.updateModelTable(deviceId, year);
		this.fValueDomain.resetActualDate(deviceId, year);
		this.leafDomain.updateModelTable(deviceId, year);
		this.photoSynthesisDomain.updateModelTable(deviceId, year);
		}

	// ###############################################
	/**
	 * 生育推定パラメータセット更新
	 *
	 * @param dto GrowthParamSetDTO
	 * @throws ParseException 
	 */
	// ###############################################
	@Override
	@Transactional(rollbackFor = Exception.class)
	public void updateParamSet(GrowthParamSetDTO dto) throws ParseException
		{
		if (this.growthParameterDomain.updateParamSet(dto))
			{
			this.growthDomain.updateModelTable(dto.getDeviceId(), dto.getYear());
			this.fValueDomain.resetActualDate(dto.getDeviceId(), dto.getYear());
			this.leafDomain.updateModelTable(dto.getDeviceId(), dto.getYear());
			this.photoSynthesisDomain.updateModelTable(dto.getDeviceId(), dto.getYear());
			}
		}

	// ###############################################
	/**
	 * 生育推定パラメータセット追加
	 *
	 * @param dto GrowthParamSetDTO
	 * @return パラメータセットID
	 */
	// ###############################################
	@Override
	@Transactional(rollbackFor = Exception.class)
	public Long addParamSet(GrowthParamSetDTO dto)
		{
		return this.growthParameterDomain.addParamSet(null, dto);
		}

	// ###############################################
	/**
	 * 日付からF値の情報を得る
	 *
	 * @param id 
	 * @param date
	 * @throws ParseException 
	 */
	// ###############################################
	@Override
	public FValuesDTO checkFValueByDate(Long id, String date) throws ParseException
		{
		return this.fValueDomain.checkFValueByDate(id, date);
		}
	}
