package com.logpose.ph2.api.service.impl;

import java.text.ParseException;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.logpose.ph2.api.dao.db.entity.Ph2ParamsetGrowthEntity;
import com.logpose.ph2.api.dao.db.entity.Ph2RealGrowthFStageEntity;
import com.logpose.ph2.api.domain.GrowthDomain;
import com.logpose.ph2.api.dto.EventDaysDTO;
import com.logpose.ph2.api.dto.FDataListDTO;
import com.logpose.ph2.api.dto.GrowthParamSetDTO;
import com.logpose.ph2.api.dto.RealModelGraphDataDTO;
import com.logpose.ph2.api.dto.ValueDateDTO;
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

	// ===============================================
	// パブリック関数
	// ===============================================
	// --------------------------------------------------
	/**
	 * 生育推定モデルデータの作成
	 *
	 * @param deviceId デバイスID
	 * @param date 対象日付
	 * @throws ParseException 
	 */
	// --------------------------------------------------
	@Override
	@Transactional(rollbackFor = Exception.class)
	public void updateDateModel(Long deviceId, Short year, Date date) throws ParseException
		{
		this.growthDomain.updateModelTable(deviceId, year, date);
		}

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
	@Override
	@Transactional(rollbackFor = Exception.class)
	public void setDefaultParamSet(Long deviceId, Short year, Long paramId)
			throws ParseException
		{
		this.growthDomain.setDefault(deviceId, year, paramId);
		}

	// --------------------------------------------------
	/**
	 * 生育推定モデルグラフデータ取得
	 *    モデルテーブルから検索してデータを取得する
	 *
	 * @param deviceId デバイスID
	 * @param year 対象年度
	 * @return 生育推定モデルグラフデータ
	 * @throws ParseException 
	 */
	// --------------------------------------------------
	@Override
	@Transactional(rollbackFor = Exception.class)
	public RealModelGraphDataDTO getModelGraph(Long deviceId, Short year)
			throws ParseException
		{
		return this.growthDomain.getModelGraph(deviceId, year);
		}

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
			Long deviceId, Short year, Long paramId) throws ParseException
		{
		return this.growthDomain.getSimulateModelGraph(deviceId, year, paramId);
		}

	// --------------------------------------------------
	/**
	 * 生育推定イベントデータ取得
	 *
	 * @param deviceId
	 * @param year 年度
	 * @param paramSetId パラメータセットID
	 * @return EventDaysDTO
	 */
	// --------------------------------------------------
	@Override
	@Transactional(rollbackFor = Exception.class)
	public List<EventDaysDTO> getEvent(Long deviceId, Short year)
		{
		return this.growthDomain.getEvent(deviceId, year);
		}

	// --------------------------------------------------
	/**
	 * 生育推定F値データ取得
	 *
	 * @param deviceId デバイスID
	 * @param year 年度
	 * @return List<FDataDTO>
	 * @throws ParseException 
	 */
	// --------------------------------------------------
	@Override
	@Transactional(rollbackFor = Exception.class)
	public List<Ph2RealGrowthFStageEntity> getAllF(Long deviceId,
			Short year) throws ParseException
		{
		return this.growthDomain.getALlFValus(deviceId, year);
		}

	// --------------------------------------------------
	/**
	 * 生育推定実績値取得
	 *
	 * @param deviceId デバイスID
	 * @param date 日付
	 * @return F値
	 * @throws ParseException 
	 */
	// --------------------------------------------------
	@Override
	@Transactional(rollbackFor = Exception.class)
	public ValueDateDTO getFData(Long deviceId, Date date) throws Exception
		{
		return this.growthDomain.getRealFData(deviceId, date);
		}

	// --------------------------------------------------
	/**
	 * 生育推定実績値更新
	 *
	 * @param dto FDataListDTO
	 */
	// --------------------------------------------------
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
	@Override
	@Transactional(rollbackFor = Exception.class)
	public void setParameter(Long deviceId, Short year, Long paramId) throws ParseException
	{
	this.growthDomain.setDefault(deviceId, year, paramId);
	}
	
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
	@Override
	@Transactional(rollbackFor = Exception.class)
	public GrowthParamSetDTO getDetail(Long paramSetId)
		{
		return this.growthDomain.getDetail(paramSetId);
		}

	// --------------------------------------------------
	/**
	 * デフォルト値の設定
	 *
	 * @param paramId パラメータセットID
	 * @throws ParseException 
	 */
	// --------------------------------------------------
	@Override
	@Transactional(rollbackFor = Exception.class)
	public void setDefault(Long deviceId, Short year, Long paramId)
			throws ParseException
		{
		this.growthDomain.setDefault(deviceId, year, paramId);
		}

	// --------------------------------------------------
	/**
	 * 生育推定パラメータセット更新
	 *
	 * @param dto GrowthParamSetDTO
	 * @throws ParseException 
	 */
	// --------------------------------------------------
	@Override
	@Transactional(rollbackFor = Exception.class)
	public void updateParamSet(GrowthParamSetDTO dto) throws ParseException
		{
		this.growthDomain.updateParamSet(dto);
		}

	// --------------------------------------------------
	/**
	 * 生育推定パラメータセット追加
	 *
	 * @param dto GrowthParamSetDTO
	 * @return 
	 */
	// --------------------------------------------------
	@Override
	@Transactional(rollbackFor = Exception.class)
	public Long addParamSet(GrowthParamSetDTO dto)
		{
		return this.growthDomain.addParamSet(null, dto);
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
	public Ph2ParamsetGrowthEntity getDefault(Long deviceId, Short year)
		{
// * デフォルトパラメータの取得
		return this.growthDomain.getParmaters(deviceId, year);
		}
	}
