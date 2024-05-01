package com.logpose.ph2.api.service.impl;

import java.text.ParseException;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.logpose.ph2.api.configration.DefaultLeafCountParameters;
import com.logpose.ph2.api.dao.db.entity.Ph2RealLeafShootsCountEntity;
import com.logpose.ph2.api.domain.DeviceDomain;
import com.logpose.ph2.api.domain.leaf.LeafDomain;
import com.logpose.ph2.api.domain.leaf.LeafGraphDomain;
import com.logpose.ph2.api.domain.photosynthesis.PhotoSynthesisDomain;
import com.logpose.ph2.api.dto.LeafParamSetDTO;
import com.logpose.ph2.api.dto.device.DeviceTermDTO;
import com.logpose.ph2.api.dto.graph.ModelGraphDataDTO;
import com.logpose.ph2.api.dto.leaf.LeafAreaValueDTO;
import com.logpose.ph2.api.dto.leaf.LeafAreaValueListDTO;
import com.logpose.ph2.api.dto.leaf.LeafShootDTO;
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
	private DeviceDomain deviceDomain;
	@Autowired
	private LeafDomain leafDomain;
	@Autowired
	private LeafGraphDomain graphDomain;
	@Autowired
	private DefaultLeafCountParameters defaultLeafCountParameters;
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
	 * @throws ParseException 
	 */
	// ###############################################
	@Override
	@Transactional(readOnly = true)
	public LeafShootDTO getShootCount(Long deviceId, Short year)
			throws ParseException
		{
		LeafShootDTO result = new LeafShootDTO();

// * 基準日の取得と返却オブジェクトへの設定
		DeviceTermDTO term = this.deviceDomain.getTerm(deviceId, year);
		result.setStartDate(term.getStartDate());
		result.setEndDate(term.getEndDate());

// * 該当オブジェクトの検索と値の設定
		Ph2RealLeafShootsCountEntity entity = this.leafDomain.searchShootCount(deviceId, year);
		if (null != entity)
			{
			result.setCount(entity.getCount());
			result.setDate(DateTimeUtility.getStringFromDate(entity.getTargetDate()));
			}
// * 返却オブジェクトの生成と返却
		else
			{
			int defaultCount = this.defaultLeafCountParameters.getCount();
			result.setCount(defaultCount);
			result.setDate(term.getStartDate());
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
	 * @return LeafAreaValueListDTO
	 * @throws ParseException 
	 */
	// ###############################################
	@Override
	@Transactional(rollbackFor = Exception.class)
	public LeafAreaValueListDTO getAllAreaAndCount(Long deviceId, Short year) throws ParseException
		{
		LeafAreaValueListDTO result = new LeafAreaValueListDTO();
// * 年度の期間を取得
		DeviceTermDTO term = this.deviceDomain.getTerm(deviceId, year);
		result.setStartDate(term.getStartDate());
		result.setEndDate(term.getEndDate());

// * 葉面積実測値の取得
		List<LeafAreaValueDTO> values = this.leafDomain.searchShootAreaAll(deviceId, year);
		result.setValues(values);

		return result;
		}

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
	@Override
	@Transactional(readOnly = true)
	public Double getModelValue(Long deviceId, Short year, Date date)
			throws ParseException
		{
		return this.leafDomain.getModelValue(deviceId, year, date);
		}

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
	@Override
	@Transactional(rollbackFor = Exception.class)
	public void addShoot(Long deviceId, Short year, LeafShootDTO dto) throws ParseException
		{
		Ph2RealLeafShootsCountEntity entity = new Ph2RealLeafShootsCountEntity();
		entity.setDeviceId(deviceId);
		entity.setYear(year);
		entity.setTargetDate(DateTimeUtility.getDateFromString(dto.getDate()));
		entity.setCount(dto.getCount());
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
	public void setAreaAndCount(Long deviceId, Short year, List<LeafAreaValueDTO> values) throws ParseException
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
