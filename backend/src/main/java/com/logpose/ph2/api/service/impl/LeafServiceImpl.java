package com.logpose.ph2.api.service.impl;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.logpose.ph2.api.configration.DefaultLeafCountParameters;
import com.logpose.ph2.api.dao.db.entity.Ph2RealLeafShootsCountEntity;
import com.logpose.ph2.api.dao.db.mappers.Ph2DeviceDayMapper;
import com.logpose.ph2.api.domain.leaf.LeafDomain;
import com.logpose.ph2.api.domain.leaf.LeafGraphDomain;
import com.logpose.ph2.api.domain.photosynthesis.PhotoSynthesisDomain;
import com.logpose.ph2.api.dto.LeafParamSetDTO;
import com.logpose.ph2.api.dto.LeafShootDTO;
import com.logpose.ph2.api.dto.LeafvaluesDTO;
import com.logpose.ph2.api.dto.MaxMinDateDTO;
import com.logpose.ph2.api.dto.graph.ModelGraphDataDTO;
import com.logpose.ph2.api.dto.leaf.LeafAreaValuesDTO;
import com.logpose.ph2.api.service.LeafService;
import com.logpose.ph2.api.utility.DateTimeUtility;

/**
 * 葉面積・葉枚数のサービス
 *
 */
@Service
public class LeafServiceImpl implements LeafService
	{
	// ===============================================
	// クラスメンバー
	// ===============================================
	@Autowired
	private LeafDomain leafDomain;
	@Autowired
	private LeafGraphDomain graphDomain;
	@Autowired
	private DefaultLeafCountParameters defaultLeafCountParameters;
	@Autowired
	private Ph2DeviceDayMapper ph2DeviceDayMapper;
	@Autowired
	private PhotoSynthesisDomain photoSynthesisDomain;

	// ===============================================
	// パブリック関数
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
	@Override
	@Transactional(rollbackFor = Exception.class)
	public List<ModelGraphDataDTO> getLeaflGraphData(Long deviceId,
			short year) throws ParseException
		{
		return this.graphDomain.getModelGraph(deviceId, year);
		}

	// ###############################################
	/**
	 * 新梢数取得処理
	 *
	 * @param deviceId デバイスID
	 * @param year 対象年度
	 * @param date 対象日付
	 * @throws ParseException 
	 */
	// ###############################################
	@Override
	@Transactional(readOnly = true)
	public LeafShootDTO getShootCount(Long deviceId, Short year, Date date)
			throws ParseException
		{
		Ph2RealLeafShootsCountEntity entity;
// * 該当オブジェクトの検索
		if (date != null)
			{
			entity = this.leafDomain.searchShootCountByDate(deviceId, year, date);
			}
		else
			{
			entity = this.leafDomain.searchShootCount(deviceId, year);
			}
		String dateName = null;
		if (null == entity)
			{
			entity = new Ph2RealLeafShootsCountEntity();
			}
		else
			{
			dateName = DateTimeUtility.getStringFromDate(entity.getTargetDate());
			}
// * 返却オブジェクトの生成と返却
		LeafShootDTO result = new LeafShootDTO();
		result.setDeviceId(deviceId);
		result.setDate(dateName);
		result.setCount(entity.getCount());
		if (null == result.getCount())
			{
			int defaultCount = this.defaultLeafCountParameters.getCount();
			result.setCount(defaultCount);
			}
		return result;
		}

	// ###############################################
	/**
	 * 新梢辺り葉枚数・平均個葉面積検索処理
	 *
	 * @param deviceId データを表示するデバイスのID
	 * @param date 実績の日付
	 * 	@param date 対象日付
	 * @throws ParseException
	 */
	// ###############################################
	@Override
	@Transactional(rollbackFor = Exception.class)
	public LeafvaluesDTO getAreaAndCount(Long deviceId, Short year, Date date)
			throws ParseException
		{
// * デバイス期間の取得
		MaxMinDateDTO min_max_date = this.ph2DeviceDayMapper.selectMaxMinDate(deviceId, year);
		// * 実施日の設定
		Date measure_date;
		Date startDate = DateTimeUtility.getDateFromString(min_max_date.getStartDate());
		Date endDate = DateTimeUtility.getDateFromString(min_max_date.getEndDate());
		if (date.getTime() < startDate.getTime())
			{
			measure_date = startDate;
			}
		else if (date.getTime() > endDate.getTime())
			{
			measure_date = endDate;
			}
		else
			measure_date = date;
// * 葉面積実測値の取得
		LeafvaluesDTO result = this.leafDomain.searchShootArea(deviceId, year, measure_date);
// * 基本情報の設定
		if (null != result)
			{
			result.setDeviceId(deviceId);
			result.setYear(year);
			result.setStartDate(min_max_date.getStartDate());
			result.setEndDate(min_max_date.getStartDate());
			result.setDate(DateTimeUtility.getStringFromDate(measure_date));
			}
		return result;
		}

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
	@Override
	@Transactional(rollbackFor = Exception.class)
	public LeafParamSetDTO getDetailParamSet(Long paramSetId)
			throws JsonMappingException, JsonProcessingException
		{
		return this.leafDomain.getDetail(paramSetId);
		}

	// ###############################################
	/**
	 * 新梢辺り葉枚数・平均個葉面積定義検索処理
	 *
	 * @param deviceId
	 * @param year
	 */
	// ###############################################
	@Override
	@Transactional(rollbackFor = Exception.class)
	public List<LeafvaluesDTO> getAllAreaAndCount(Long deviceId, Short year)
		{
// * デバイス期間の取得
		MaxMinDateDTO min_max_date = this.ph2DeviceDayMapper.selectMaxMinDate(deviceId, year);
// * 葉面積実測値の取得
		List<LeafvaluesDTO> results = this.leafDomain.searchShootAreaAll(deviceId, year);
		for (LeafvaluesDTO item : results)
			{
			item.setDeviceId(deviceId);
			item.setYear(year);
			item.setStartDate(min_max_date.getStartDate());
			item.setEndDate(min_max_date.getEndDate());
			}
		return results;
		}

	// ###############################################
	/**
	 * 新梢数登録処理
	 *
	 * @param dto LeafShootDTO
	 * @throws ParseException
	 */
	// ###############################################
	@Override
	@Transactional(rollbackFor = Exception.class)
	public void addShoot(LeafShootDTO dto) throws ParseException
		{
		Ph2RealLeafShootsCountEntity entity = new Ph2RealLeafShootsCountEntity();
		entity.setDeviceId(dto.getDeviceId());
		entity.setTargetDate(DateTimeUtility.getDateFromString(dto.getDate()));
		entity.setCount(dto.getCount());
		// TODO Year はいるのかな？
		Calendar cal = Calendar.getInstance();
		cal.setTime(entity.getTargetDate());
		entity.setYear((short) cal.get(Calendar.YEAR));
		this.leafDomain.addShootCount(entity);
		}

	// ###############################################
	/**
	 * 新梢辺り葉枚数・平均個葉面積登録処理
	 *
	 * @param deviceId デバイスID
	 * @param year 年度
	 * @param values List<LeafAreaValuesDTO>
	 * @throws ParseException
	 */
	// ###############################################
	@Override
	@Transactional(rollbackFor = Exception.class)
	public void setAreaAndCount(Long deviceId, Short year, List<LeafAreaValuesDTO> values) throws ParseException
		{
		// * 新梢辺り葉枚数・平均個葉面積登録処理
		this.leafDomain.addAreaAndCount(deviceId, year, values);
		}

	// ###############################################
	/**
	 * 基準パラメータセットの設定
	 * @param deviceId デバイスID
	 * @param year 対象年度
	 * @param paramSetId パラメータセットID
	 * @throws ParseException 
	 */
	// ###############################################
	@Override
	@Transactional(rollbackFor = Exception.class)
	public void setDefault(Long deviceId, Short year, Long paramId) throws ParseException
		{
		this.leafDomain.setDefault(deviceId, year, paramId);
		this.photoSynthesisDomain.updateModelTable(deviceId, year);
		}

	// ###############################################
	/**
	 * 葉面積・葉枚数パラメータセット更新
	 *
	 * @param dto LeafParamSetDTO
	 * @return ResponseDTO (null)
	 * @throws ParseException 
	 */
	// ###############################################
	@Override
	@Transactional(rollbackFor = Exception.class)
	public void updateParamSet(LeafParamSetDTO dto) throws ParseException
		{
		if (this.leafDomain.updateParamSet(dto))
			{
			this.photoSynthesisDomain.updateModelTable(dto.getDeviceId(), dto.getYear());
			}
		}

	// ###############################################
	/**
	 * 葉面積・葉枚数パラメータセット追加
	 *
	 * @param dto LeafParamSetDTO
	 * @return ResponseDTO (null)
	 */
	// ###############################################
	@Override
	@Transactional(rollbackFor = Exception.class)
	public Long addParamSet(LeafParamSetDTO dto)
		{
		return this.leafDomain.addParamSet(null, dto);
		}

	}
