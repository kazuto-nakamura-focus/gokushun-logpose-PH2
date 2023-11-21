package com.logpose.ph2.api.domain.leaf;

import java.util.List;

import com.logpose.ph2.api.dao.db.entity.Ph2ModelDataEntity;
import com.logpose.ph2.api.dao.db.entity.Ph2ModelDataEntityExample;
import com.logpose.ph2.api.dao.db.mappers.Ph2ModelDataMapper;

public class LeafModelDataExporter
	{
	// ===============================================
	// クラスメンバー
	// ===============================================
	private Ph2ModelDataMapper ph2ModelDataMapper;

	// ===============================================
	// コンストラクタ
	// ===============================================
	// --------------------------------------------------
	/**
	 * パラメータの設定と初期化
	 * @param ph2ModelDataMapper
	 */
	// --------------------------------------------------
	public LeafModelDataExporter(Ph2ModelDataMapper ph2ModelDataMapper)
		{
		this.ph2ModelDataMapper = ph2ModelDataMapper;
		}

	// ===============================================
	// 公開メソッド
	// ===============================================
	// --------------------------------------------------
	/**
	 * 受取ったデータをDBに追加する
	 * @param dayId モデルデータの日付ID
	 * @param area 樹冠葉面積データ
	 * @param count 葉枚数
	 */
	// --------------------------------------------------
	public void add(Long dayId, double area, double count)
		{
// * デバイス日付IDに対応するモデルデータを取得する
		Ph2ModelDataEntityExample exm = new Ph2ModelDataEntityExample();
		exm.createCriteria().andDayIdEqualTo(dayId);
		List<Ph2ModelDataEntity> list = this.ph2ModelDataMapper.selectByExample(exm);
// * モデルデータを更新する
		Ph2ModelDataEntity entity = list.get(0);
// * 樹冠葉面積データの更新
		entity.setCrownLeafArea(area);
// * 葉枚数の更新
		entity.setLeafCount(count);
// * モデルデータの更新
		this.ph2ModelDataMapper.updateByExample(entity, exm);
		}
	}
