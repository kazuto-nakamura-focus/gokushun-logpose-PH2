package com.logpose.ph2.api.domain;

import java.sql.Timestamp;
import java.text.ParseException;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.logpose.ph2.api.dao.db.entity.Ph2ParamsetCatalogEntity;
import com.logpose.ph2.api.dao.db.entity.Ph2ParamsetCatalogEntityExample;
import com.logpose.ph2.api.dao.db.entity.Ph2ParamsetHistoryEntity;
import com.logpose.ph2.api.dao.db.entity.Ph2ParamsetHistoryEntityExample;
import com.logpose.ph2.api.dao.db.mappers.Ph2ParamsetCatalogMapper;
import com.logpose.ph2.api.dao.db.mappers.Ph2ParamsetHistoryMapper;
import com.logpose.ph2.api.dao.db.mappers.joined.Ph2ParamSetJoinMapper;
import com.logpose.ph2.api.dto.HistoryDTO;
import com.logpose.ph2.api.dto.ParamSetDTO;
import com.logpose.ph2.api.dto.ParamSetExtendDTO;

/**
 * パラメータセットサービス
 *
 */
@Component
public class ParameterSetDomain
	{
	// ===============================================
	// クラスメンバ
	// ===============================================
	@Autowired
	private Ph2ParamsetCatalogMapper ph2ParamsetCatalogMapper;
	@Autowired
	private Ph2ParamSetJoinMapper ph2ParamSetJoinMapper;
	@Autowired
	private Ph2ParamsetHistoryMapper ph2ParamsetHistoryMapper;

	// ===============================================
	// パブリック関数
	// ===============================================
	// --------------------------------------------------
	/**
	 * パラメータセットリスト取得
	 *
	 * @param modelId-モデルID
	 * @return List<ParamSetDTO>
	 */
	// --------------------------------------------------
	public List<ParamSetExtendDTO> getParamSetList(Integer modelId)
		{
		return this.ph2ParamSetJoinMapper.listSimpleParamerSet(modelId);
		}
	// --------------------------------------------------
	/**
	 * デフォルトパラメータIDの取得
	 *
	 * @param deviceId
	 * @param year
	 * @return デフォルトパラメータID
	 */
	// --------------------------------------------------
	public Long getDefaultParam(Integer modelId, Long deviceId, Short year)
		{
		Ph2ParamsetCatalogEntityExample exm = new Ph2ParamsetCatalogEntityExample();
		exm.createCriteria().andDeviceIdEqualTo(deviceId)
		.andYearEqualTo(year)
		.andModelIdEqualTo(modelId)
		.andDefaultFlagEqualTo(true);
		return this.ph2ParamsetCatalogMapper.selectByExample(exm).get(0).getId();
		}
	// --------------------------------------------------
	/**
	 * パラメータセット更新履歴取得
	 *
	 * @param paramSetId
	 * @return List<HistoryDTO>
	 */
	// --------------------------------------------------
	public List<HistoryDTO> getHistory(Long paramSetId)
		{
		return this.ph2ParamSetJoinMapper.selectHistory(paramSetId);
		}
	// --------------------------------------------------
	/**
	 * 基準パラメータセットの設定
	 * @param modelId　成長モデル番号
	 * @param deviceId  対象デバイスID
	 * @param year　対象年度
	 * @param paramId 基準	パラメータセットID
	 */
	// --------------------------------------------------
	public void setDefautParamSet(Integer modelId, Long deviceId, Short year, Long paramId)
		{
// * 引数のターゲットに対して、デフォルトフラグを初期化する
		Ph2ParamsetCatalogEntityExample exm = new Ph2ParamsetCatalogEntityExample();
		exm.createCriteria().andDeviceIdEqualTo(deviceId)
			.andYearEqualTo(year)
			.andModelIdEqualTo(modelId);
		Ph2ParamsetCatalogEntity entity = new Ph2ParamsetCatalogEntity();
		entity.setDefaultFlag(false);
		entity.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
		this.ph2ParamsetCatalogMapper.updateByExampleSelective(entity, exm);
		
// * 引数のパラメータIDに対してデフォルト設定する
	    entity = this.ph2ParamsetCatalogMapper.selectByPrimaryKey(paramId);
		entity.setDefaultFlag(true);
		entity.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
		this.ph2ParamsetCatalogMapper.updateByPrimaryKey(entity);		
		}
	// --------------------------------------------------
	/**
	 * パラメータセットの削除
	 *
	 * @param ParamSetDTO パラメータセット
	 * @param Integer 使用されるモデルID
	 */
	// --------------------------------------------------
	public void removeParamSet(long paramId)
		{
		this.ph2ParamsetCatalogMapper.deleteByPrimaryKey(paramId);
		}
	// ===============================================
	// 保護関数群
	// ===============================================]
	// --------------------------------------------------
	/**
	 * パラメータセットのカタログ情報取得
	 *
	 * @param paramSetId パラメータセットID
	 * @return Ph2ParamsetCatalogEntity
	 */
	// --------------------------------------------------
	public void fetchDetail(Long paramSetId, ParamSetDTO dto)
		{
		Ph2ParamsetCatalogEntity catalog = this.ph2ParamsetCatalogMapper.selectByPrimaryKey(paramSetId);
		HistoryDTO latest = this.ph2ParamSetJoinMapper.selectLatestHistory(paramSetId);

		dto.setComment(latest.getComment());
		dto.setDate(latest.getDate());
		dto.setId(catalog.getId());
		dto.setYear(catalog.getYear());
		dto.setDeviceId(catalog.getDeviceId());
		dto.setName(latest.getName());
		dto.setParameterName(catalog.getName());
		dto.setUserId(latest.getUserId());
		dto.setDefaultFlg(catalog.getDefaultFlag());
		}

	// --------------------------------------------------
	/**
	 * パラメータセットの更新
	 *
	 * @param ParamSetDTO パラメータセット
	 * @param Integer 使用されるモデルID
	 */
	// --------------------------------------------------
	public boolean update(ParamSetDTO dto, Integer modelId)
		{
		Ph2ParamsetCatalogEntity target = this.ph2ParamsetCatalogMapper.selectByPrimaryKey(dto.getId());
		this.setCatalogData(target, modelId, dto);
		this.ph2ParamsetCatalogMapper.updateByPrimaryKey(target);

		this.addHistory(dto, dto.getId());
		// * 更新されたものがデフォルトかどうかを返却する
		return target.getDefaultFlag();
		}

	// --------------------------------------------------
	/**
	 * パラメータセットの追加
	 *
	 * @param ParamSetDTO パラメータセット
	 * @param Integer 使用されるモデルID
	 */
	// --------------------------------------------------
	public Long add(ParamSetDTO dto, Integer modelId)
		{
		Ph2ParamsetCatalogEntity target = new Ph2ParamsetCatalogEntity();
		// * 追加した時点ではデフォルト設定はfalseとする
		this.setCatalogData(target, modelId, dto);
		target.setCreatedAt(new Timestamp(System.currentTimeMillis()));
		Long id = this.ph2ParamsetCatalogMapper.insert(target);

		this.addHistory(dto, id);
		return id;
		}

	// --------------------------------------------------
	/**
	 * 指定されたモデルの指定されたデバイスIDに対して、指定年までのデフォルト
	 * パラメータセットのIDを年度順に取得する
	 *
	 * @param modelId モデルID
	 * @param deviceId 取得対象となるデバイスID
	 * @param year 対象年度
	 * @return パラメータセット・カタログのリスト
	 */
	// --------------------------------------------------
	public List<Ph2ParamsetCatalogEntity> getParamSetCatalogsByYear(
			Integer modelId, Long deviceId, short year)
		{
		return this.ph2ParamsetCatalogMapper.selectByNearestYear(modelId, deviceId, year);
		}

	// --------------------------------------------------
	/**
	 * 指定されたモデルの指定されたデバイスIDに対して、指定年までのデフォルト
	 * パラメータセットのIDを年度順に取得する
	 *
	 * @param modelId モデルID
	 * @param deviceId 取得対象となるデバイスID
	 * @param year 対象年度
	 * @return パラメータセット・カタログのリスト
	 * @throws ParseException 
	 */
	// --------------------------------------------------
	public Long addCatalogData(Integer modelId, Long deviceId, short year)
		{
		ParamSetDTO dto = new ParamSetDTO();
		dto.setDate(new Date());
		dto.setComment("パラメータはシステムによるデフォルトの初期設定を使用しています。");
		dto.setUserId((long) 1);
		dto.setName("system");
		dto.setDeviceId(deviceId);
		dto.setYear(year);
		dto.setParameterName("Default Parameters");
		dto.setDefaultFlg(true);
		return this.add(dto, modelId);
		}

	// ===============================================
	// プライベート関数
	// ===============================================
	// --------------------------------------------------
	/**
	 * パラメータセットカタログに与えられた情報を設定する
	 * @param entity 設定するパラメータセットカタログ
	 * @param modelId entityが属するモデルID
	 * @param dto 設定情報
	 */
	// --------------------------------------------------
	private void setCatalogData(Ph2ParamsetCatalogEntity entity, Integer modelId, ParamSetDTO dto)
		{
		entity.setDeviceId(dto.getDeviceId());
		entity.setModelId(modelId);
		entity.setYear(dto.getYear());
		entity.setName(dto.getParameterName());
		entity.setDefaultFlag(dto.getDefaultFlg());
		entity.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
		}
	// --------------------------------------------------
	/**
	 * 履歴を追加する
	 * @param dto 追加情報
	 * @param paramSetId 追加先のパラメータセットID
	 */
	// --------------------------------------------------
	private void addHistory(ParamSetDTO dto, Long paramSetId)
		{
		Ph2ParamsetHistoryEntityExample exm = new Ph2ParamsetHistoryEntityExample();
		exm.createCriteria().andLatestEqualTo(true).andParamsetIdEqualTo(paramSetId);
		List<Ph2ParamsetHistoryEntity> records = this.ph2ParamsetHistoryMapper.selectByExample(exm);
		if (records.size() > 0)
			{
			Ph2ParamsetHistoryEntity oldLatest = records.get(0);
			oldLatest.setLatest(false);
			this.ph2ParamsetHistoryMapper.updateByExample(oldLatest, exm);
			}

		Ph2ParamsetHistoryEntity history = new Ph2ParamsetHistoryEntity();
		history.setParamsetId(paramSetId);
		history.setComment(dto.getComment());
		history.setLatest(true);
		history.setUserId(dto.getUserId());
		history.setCreatedAt(new Timestamp(System.currentTimeMillis()));
		history.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
		this.ph2ParamsetHistoryMapper.insert(history);
		}
	// --------------------------------------------------
	}
