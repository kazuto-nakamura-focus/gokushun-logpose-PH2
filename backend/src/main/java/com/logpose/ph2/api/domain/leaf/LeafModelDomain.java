package com.logpose.ph2.api.domain.leaf;

import java.text.ParseException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.logpose.ph2.api.configration.DefaultLeafCountParameters;
import com.logpose.ph2.api.dao.db.entity.Ph2RealLeafShootsCountEntity;
import com.logpose.ph2.api.dao.db.entity.Ph2RealLeafShootsCountEntityExample;
import com.logpose.ph2.api.dao.db.entity.Ph2WibleMasterEntity;
import com.logpose.ph2.api.dao.db.entity.Ph2WibleMasterEntityExample;
import com.logpose.ph2.api.dao.db.entity.joined.ModelAndDailyDataEntity;
import com.logpose.ph2.api.dao.db.mappers.Ph2RealLeafShootsAreaMapper;
import com.logpose.ph2.api.dao.db.mappers.Ph2RealLeafShootsCountMapper;
import com.logpose.ph2.api.dao.db.mappers.Ph2WibleMasterMapper;
import com.logpose.ph2.api.domain.model.AppliedModel;
import com.logpose.ph2.api.dto.LeafParamSetDTO;
import com.logpose.ph2.api.formula.Formula;

@Component
public class LeafModelDomain
	{
	// ===============================================
	// クラスメンバー
	// ===============================================
	@Autowired
	private AppliedModel appliedModel;
	@Autowired
	private Ph2WibleMasterMapper ph2WibleMasterMapper;
	@Autowired
	protected Ph2RealLeafShootsAreaMapper ph2RealLeafShootsAreaMapper;
	@Autowired
	private LeafParameterDomain leafParameterDomain;
	@Autowired
	private Ph2RealLeafShootsCountMapper ph2RealLeafShootsCountMapper;
	@Autowired
	private DefaultLeafCountParameters defaultLeafCountParameters;

	// ===============================================
	// 公開関数群
	// ===============================================
	// ###############################################
	/**
	 * デバイスのモデルテーブルを更新する
	 * 
	 * @param deviceId デバイスID
	 * @param year 年度
	 * @param data 更新対象となる日ごととモデルのデータ
	 */
	// ###############################################
	public void updateModelData(Long deviceId, Short year, List<ModelAndDailyDataEntity> data)
		{
		this.updateModelData(deviceId, year, null, null, null, data);
		}

	// ###############################################
	/**
	 * デバイスのモデルテーブルを更新する
	 * 
	 * @param deviceId デバイスID
	 * @param year 年度
	 * @param shootCount 新梢数
	 * @param sproutDay 萌芽日
	 * @param parameters パラメータセットの値群
	 * @param data 更新対象となる日ごととモデルのデータ
	 */
	// ###############################################
	public void updateModelData(Long deviceId, Short year, Short shootCount, Short sproutDay,
			LeafParamSetDTO parameters, List<ModelAndDailyDataEntity> data)
		{

// * 日ごとデータがある場合
		if (0 != data.size())
			{
// * 葉面積パラメータセットの取得
			if (null == parameters) parameters = this.leafParameterDomain.getParmaters(deviceId, year);

// * 萌芽日の取得
			if (null == sproutDay)
				{
				sproutDay = this.appliedModel.getSproutDay(deviceId, year);
				}

// * 新梢数が無い場合は取得する
			if (null == shootCount) shootCount = (short) this.getShootCount(deviceId, year);

// * ワイブル分布の取得
			List<Ph2WibleMasterEntity> wibles = this.ph2WibleMasterMapper
					.selectByExample(new Ph2WibleMasterEntityExample());

// * データの生成を行う
			this.updateModelData(sproutDay, shootCount, parameters, wibles, data);
			}
		}

	// ===============================================
	// 保護関数群
	// ===============================================
	// ###############################################
	/**
	 * 各葉面積のモデルデータを作成する
	 * @param lapseDay
	 * @param shootCount
	 * @param parameters
	 * @param wibles ワイブル分布マスター
	 * @param dailyData 更新対象となる日ごととモデルのデータ
	 */
	// ###############################################
	private void updateModelData(
			short lapseDay, // 経過日
			long shootCount, // 実測新梢数
			LeafParamSetDTO parameters, // 葉面積データ
			List<Ph2WibleMasterEntity> wibles,
			List<ModelAndDailyDataEntity> dailyData)
		{
		double cdd = 0;
		for (ModelAndDailyDataEntity data : dailyData)
			{
// * 葉枚数モデル式
			double lc = Formula.toCountLeaf(parameters, data.getTm());
// * 葉枚数の更新
			data.setLeafCount(lc);

// * 樹冠葉面積モデル値
			double tla = 0;
// * 萌芽日からの経過日数を算出
			int lapse_day = data.getLapseDay() - lapseDay;

// * 経過日数が１以上なら萌芽後として以下の処理を行う
			if ( lapse_day > 0)
				{
				cdd += data.getRawCdd();
				// * wible値を取得
				double wible = wibles.get(lapse_day).getValue();
				// * 樹冠葉面積モデル値を算出
				tla = Formula.toLeafArea(shootCount, parameters, cdd, wible);
				}
// * 樹冠葉面積データの更新
			data.setCrownLeafArea(tla);
			}
		}

	// ###############################################
	/**
	 * 対象デバイスの対象年度の新梢数を取得する。
	 * 存在しない場合はデフォルト値を設定して返却
	 * 
	 * @param deviceId-デバイスID
	 * @param year-対象年度
	 * @return 新梢数
	 * @throws ParseException 
	 */
	// ###############################################
	public int getShootCount(Long deviceId, Short year)
		{
		Ph2RealLeafShootsCountEntityExample exm = new Ph2RealLeafShootsCountEntityExample();
		exm.createCriteria().andDeviceIdEqualTo(deviceId)
				.andYearEqualTo(year);
		List<Ph2RealLeafShootsCountEntity> result = this.ph2RealLeafShootsCountMapper
				.selectByExample(exm);
		if (result.size() > 0)
			{
			return result.get(0).getCount();
			}
		else
			{
			return this.defaultLeafCountParameters.getCount();
			}
		}
	}
