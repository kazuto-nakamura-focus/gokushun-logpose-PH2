package com.logpose.ph2.api.domain.model;

import java.sql.Timestamp;
import java.text.ParseException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.logpose.ph2.api.configration.DefaultFtageValues;
import com.logpose.ph2.api.dao.db.entity.Ph2RealGrowthFStageEntity;
import com.logpose.ph2.api.dao.db.entity.Ph2RealGrowthFStageEntityExample;
import com.logpose.ph2.api.dao.db.entity.joined.ModelAndDailyDataEntity;
import com.logpose.ph2.api.dao.db.mappers.Ph2ModelDataMapper;
import com.logpose.ph2.api.dao.db.mappers.Ph2RealGrowthFStageMapper;
import com.logpose.ph2.api.domain.growth.FValueDomain;
import com.logpose.ph2.api.domain.growth.GrowthModelDomain;
import com.logpose.ph2.api.domain.leaf.LeafModelDomain;
import com.logpose.ph2.api.domain.photosynthesis.PSModelDataDomain;
import com.logpose.ph2.api.dto.GrowthParamSetDTO;
import com.logpose.ph2.api.dto.LeafParamSetDTO;
import com.logpose.ph2.api.dto.photosynthesis.PhotosynthesisParamSetDTO;

@Service
public class ModelDataDomain
	{
	// ===============================================
	// クラスメンバー
	// ===============================================
	@Autowired
	private DefaultFtageValues fstageValues;
	@Autowired
	private GrowthModelDomain growthModelDomain;
	@Autowired
	private FValueDomain fValueDomain;
	@Autowired
	private LeafModelDomain leafModelDomain;
	@Autowired
	private PSModelDataDomain pSModelDataDomain;
	@Autowired
	private Ph2ModelDataMapper ph2ModelDataMapper;
	@Autowired
	private Ph2RealGrowthFStageMapper ph2RealGrowthFStageMapper;
	// ===============================================
	// 公開関数群
	// ===============================================
	// ###############################################
	/**
	 * モデルデータの取得を行う
	 * @param deviceId-デバイスID
	 * @param year-対象年度
	 */
	// ###############################################
	public List<ModelAndDailyDataEntity> get(long deviceId, short year)
		{
		return this.ph2ModelDataMapper.selectModelAndDailyData(deviceId, year);
		}
	// ###############################################
	/**
	 * モデルデータの更新を行う
	 * @param dailyData List<ModelAndDailyDataEntity>
	 */
	// ###############################################
	public void upate(List<ModelAndDailyDataEntity> dailyData) 
		{
		for(final ModelAndDailyDataEntity entity : dailyData)
			{
			this.ph2ModelDataMapper.updateByPrimaryKey(entity);
			}
		}
	// ###############################################
	/**
	 * モデルデータを作成し、DBを更新する
	 * 
	 * @param deviceId
	 * @param year
	 * @throws ParseException
	 */
	// ###############################################
	@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRES_NEW)
	public void doService(long deviceId, short year) throws ParseException
		{
// * モデルデータを日ごとデータとともに取得する
		List<ModelAndDailyDataEntity> data = this.get(deviceId, year);
// * FTableの初期化
		this.setUpFTable(deviceId, year);
// * 各モデルデータの作成
		this.growthModelDomain.updateModelData(deviceId, year, data);
		this.fValueDomain.resetActualDate(deviceId, year);
		this.leafModelDomain.updateModelData(deviceId, year, data);
		this.pSModelDataDomain.updateModelData(deviceId, year, data);
		
// * モデルデータを更新する
		this.upate(data);
		}
	// ###############################################
	/**
	 * 生育推定パラメータセット更新時のモデルデータ変更
	 *
	 * @param deviceId
	 * @param year
	 * @param paramInfo 生育推定パラメータセット
	 */
	// ###############################################
	public void updateByGrowthParams(long deviceId, short year, GrowthParamSetDTO paramInfo) 
		{
// * モデルデータを日ごとデータとともに取得する
		List<ModelAndDailyDataEntity> data = this.get(deviceId, year);
// * 生育推定モデルの更新
		this.growthModelDomain.updateModelData(deviceId, year, paramInfo, data);
// * 葉面積モデルの更新
		this.leafModelDomain.updateModelData(deviceId, year, data);
// * 光合成推定モデルの更新
		this.pSModelDataDomain.updateModelData(deviceId, year, data);
// * モデルデータを更新する
		this.upate(data);		
		}
	// ###############################################
	/**
	 * 新梢数登録処理時のモデルデータ変更
	 *
	 * @param deviceId
	 * @param year
	 * @param shootCount 新梢数
	 */
	// ###############################################
	public void updateByShootCount(long deviceId, short year, Short shootCount) 
		{
// * モデルデータを日ごとデータとともに取得する
		List<ModelAndDailyDataEntity> data = this.get(deviceId, year);
// * 葉面積モデルの更新
		this.leafModelDomain.updateModelData(deviceId, year, shootCount, null, null, data);
// * 光合成推定モデルの更新
		this.pSModelDataDomain.updateModelData(deviceId, year, data);
// * モデルデータを更新する
		this.upate(data);		
		}
	// ###############################################
	/**
	 * 葉面積基準パラメータセット更新時のモデルデータ変更
	 *
	 * @param deviceId
	 * @param year
	 * @param paramInfo 葉面積基準パラメータセット
	 */
	// ###############################################
	public void updateByLeafParams(long deviceId, short year, LeafParamSetDTO paramInfo) 
		{
// * モデルデータを日ごとデータとともに取得する
		List<ModelAndDailyDataEntity> data = this.get(deviceId, year);
// * 葉面積モデルの更新
		this.leafModelDomain.updateModelData(deviceId, year, null, null, paramInfo, data);
// * 光合成推定モデルの更新
		this.pSModelDataDomain.updateModelData(deviceId, year, data);
// * モデルデータを更新する
		this.upate(data);		
		}
	// ###############################################
	/**
	 * 光合成推定実績値更新時のモデルデータ変更
	 *
	 * @param deviceId
	 * @param year
	 */
	// ###############################################
	public void updateByPsActualValue(long deviceId, short year) 
		{
// * モデルデータを日ごとデータとともに取得する
		List<ModelAndDailyDataEntity> data = this.get(deviceId, year);
// * 光合成推定モデルの更新
		this.pSModelDataDomain.updateModelData(deviceId, year, data);
// * モデルデータを更新する
		this.upate(data);		
		}
	
	// ###############################################
	/**
	 * 光合成推定パラメータセット更新時のモデルデータ変更
	 *
	 * @param deviceId
	 * @param year
	 * @param param
	 */
	// ###############################################
	public void updateByPsParam(long deviceId, short year, PhotosynthesisParamSetDTO param) 
		{
// * モデルデータを日ごとデータとともに取得する
		List<ModelAndDailyDataEntity> data = this.get(deviceId, year);
// * 光合成推定モデルの更新
		this.pSModelDataDomain.updateModelData(deviceId, year, param.getFieldF(), param.getFieldG(), null, data);
// * モデルデータを更新する
		this.upate(data);		
		}
	// ===============================================
	// 保護関数群
	// ===============================================
	// ###############################################
	/**
	 * Fデータテーブルの初期化
	 * 
	 * @param deviceId-デバイスID
	 * @param year-対象年度
	 */
	// ###############################################
	public void setUpFTable(Long deviceId, Short year)
		{
		// * 検索条件の設定
		Ph2RealGrowthFStageEntityExample exm = new Ph2RealGrowthFStageEntityExample();
		exm.createCriteria().andDeviceIdEqualTo(deviceId).andYearEqualTo(year);
		exm.setOrderByClause("stage_start asc");
		// * 検索実行
		List<Ph2RealGrowthFStageEntity> records = this.ph2RealGrowthFStageMapper
				.selectByExample(exm);
		// * 該当が無い場合
		if (0 == records.size())
			{
			int sum = this.fstageValues.getStart().size();
			for (int i = 0; i < sum; i++)
				{
				// * デフォルトのFStageデータを作成し、DBに登録する。
				Ph2RealGrowthFStageEntity entity = new Ph2RealGrowthFStageEntity();
				entity.setDeviceId(deviceId);
				entity.setYear(year);
				entity.setAccumulatedF(this.fstageValues.getSig().get(i));
				entity.setIntervalF(this.fstageValues.getInterval().get(i));
				entity.setStageStart(this.fstageValues.getStart().get(i));
				entity.setStageEnd(this.fstageValues.getEnd().get(i));
				entity.setStageName(this.fstageValues.getName().get(i));
				entity.setColor(this.fstageValues.getColors().get(i));
				entity.setCreatedAt(new Timestamp(System.currentTimeMillis()));
				entity.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
				this.ph2RealGrowthFStageMapper.insert(entity);
				}
			}
		}

	}
