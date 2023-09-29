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
import com.logpose.ph2.api.dao.db.entity.Ph2RealLeafShootsAreaEntity;
import com.logpose.ph2.api.dao.db.entity.Ph2RealLeafShootsAreaEntityExample;
import com.logpose.ph2.api.dao.db.entity.Ph2RealLeafShootsCountEntity;
import com.logpose.ph2.api.dao.db.entity.Ph2RealLeafShootsCountEntityExample;
import com.logpose.ph2.api.domain.LeafDomain;
import com.logpose.ph2.api.dto.LeafParamSetDTO;
import com.logpose.ph2.api.dto.LeafShootDTO;
import com.logpose.ph2.api.dto.LeafvaluesDTO;
import com.logpose.ph2.api.dto.RealModelGraphDataDTO;
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

	// ===============================================
	// パブリック関数
	// ===============================================
	// --------------------------------------------------
	/**
	 * 葉面積・葉枚数モデルデータ作成(バッチよりコール)
	 *
	 * @param deviceId デバイスID
	 * @param date 対象日付
	 */
	// --------------------------------------------------
	@Override
	@Transactional(rollbackFor = Exception.class)
	public void updateDateModel(Long deviceId, Date date) throws ParseException
		{
		this.leafDomain.updateModelTable(deviceId, null, date);
		}

	// --------------------------------------------------
	/**
	 * 葉面積モデルグラフデータ取得
	 *
	 * @param deviceId デバイスID
	 * @param year 年度
	 * @return ResponseDTO(GraphDataDTO)
	 * @throws ParseException 
	 */
	// --------------------------------------------------
	@Override
	@Transactional(rollbackFor = Exception.class)
	public List<RealModelGraphDataDTO> getLeaflGraphData(Long deviceId,
			short year) throws ParseException
		{
		return this.leafDomain.getModelGraph(deviceId, year);
		}

	// --------------------------------------------------
	/**
	 * 新梢数検索処理
	 *
	 * @param dto
	 *            LeafShootDTO
	 * @throws ParseException
	 * @return LeafShootDTO
	 */
	// --------------------------------------------------
	@Override
	@Transactional(rollbackFor = Exception.class)
	public LeafShootDTO getShootCount(Long deviceId, String dateSring)
			throws ParseException
		{
		LeafShootDTO result = new LeafShootDTO();
		result.setDeviceId(deviceId);

		Date date = DateTimeUtility.getDateFromString(dateSring);
		List<Ph2RealLeafShootsCountEntity> records = this.leafDomain
				.searchShootCount(deviceId, date,
						new Ph2RealLeafShootsCountEntityExample());
		if (records.size() > 0)
			{
			Ph2RealLeafShootsCountEntity target = records.get(0);
			result.setCount(target.getCount());
			result.setDate(
					DateTimeUtility.getStringFromDate(target.getTargetDate()));
			result.setDeviceId(deviceId);
			}

		return result;
		}

	// --------------------------------------------------
	/**
	 * 新梢数登録処理
	 *
	 * @param dto
	 *            LeafShootDTO
	 * @throws ParseException
	 */
	// --------------------------------------------------
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

	// --------------------------------------------------
	/**
	 * 新梢辺り葉枚数・平均個葉面積検索処理
	 *
	 * @param deviceId
	 * @param date
	 * @throws ParseException
	 */
	// --------------------------------------------------
	@Override
	@Transactional(rollbackFor = Exception.class)
	public LeafvaluesDTO getAreaAndCount(Long deviceId, Date date)
			throws ParseException
		{
		LeafvaluesDTO result = new LeafvaluesDTO();
		result.setDeviceId(deviceId);

		List<Ph2RealLeafShootsAreaEntity> records = this.leafDomain
				.searchShooArea(deviceId, date,
						new Ph2RealLeafShootsAreaEntityExample());
		if (records.size() > 0)
			{
			Ph2RealLeafShootsAreaEntity target = records.get(0);
			result.setCount(target.getCount());
			result.setAverageArea(target.getAverageArea());
			result.setTotalArea(target.getRealArea());
			result.setDate(
					DateTimeUtility.getStringFromDate(target.getTargetDate()));
			result.setDeviceId(deviceId);
			}

		return result;
		}

	// --------------------------------------------------
	/**
	 * 新梢辺り葉枚数・平均個葉面積登録処理
	 *
	 * @param dto
	 *            LeafvaluesDTO
	 * @return ResponseDTO (null)
	 * @throws ParseException
	 */
	// --------------------------------------------------
	@Override
	@Transactional(rollbackFor = Exception.class)
	public Double setAreaAndCount(LeafvaluesDTO dto) throws ParseException
		{
		Date target_date = DateTimeUtility.getDateFromString(dto.getDate());
		// * 新梢数の取得
		Integer new_shoot_count = null;
		List<Ph2RealLeafShootsCountEntity> records = this.leafDomain
				.searchShootCount(dto.getDeviceId(), target_date,
						new Ph2RealLeafShootsCountEntityExample());
		if (records.size() > 0)
			{
			Ph2RealLeafShootsCountEntity entity = records.get(0);
			new_shoot_count = entity.getCount();
			}
		// * 新梢辺り葉枚数・平均個葉面積登録処理
		Ph2RealLeafShootsAreaEntity entity = new Ph2RealLeafShootsAreaEntity();
		entity.setTargetDate(target_date);
		entity.setAverageArea(dto.getAverageArea());
		entity.setCount(dto.getCount());
		entity.setDeviceId(dto.getDeviceId());
		Double area = (new_shoot_count == null) ? null
				: new_shoot_count.doubleValue()
						* dto.getAverageArea().doubleValue()
						* dto.getCount().doubleValue();
		entity.setRealArea(area);
		this.leafDomain.addAreaAndCount(entity);

		return area;
		}

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
	@Override
	@Transactional(rollbackFor = Exception.class)
	public LeafParamSetDTO getDetailParamSet(Long paramSetId)
			throws JsonMappingException, JsonProcessingException
		{
		return this.leafDomain.getDetail(paramSetId);
		}

	// --------------------------------------------------
	/**
	 * 葉面積・葉枚数パラメータセット更新
	 *
	 * @param dto
	 *            LeafParamSetDTO
	 * @return ResponseDTO (null)
	 */
	// --------------------------------------------------
	@Override
	@Transactional(rollbackFor = Exception.class)
	public void updateParamSet(LeafParamSetDTO dto)
		{
		this.leafDomain.updateParamSet(dto);
		}

	// --------------------------------------------------
	/**
	 * 葉面積・葉枚数パラメータセット追加
	 *
	 * @param dto
	 *            LeafParamSetDTO
	 * @return 
	 * @return ResponseDTO (null)
	 */
	// --------------------------------------------------
	@Override
	@Transactional(rollbackFor = Exception.class)
	public Long addParamSet(LeafParamSetDTO dto)
		{
		return this.leafDomain.addParamSet(null, dto);
		}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void setDefault(Long deviceId, Short year, Long paramId) throws ParseException
		{
		this.leafDomain.setDefault(deviceId, year, paramId);
		}
	}
