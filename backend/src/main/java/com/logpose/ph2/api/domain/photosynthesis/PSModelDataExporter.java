package com.logpose.ph2.api.domain.photosynthesis;

import java.util.List;

import com.logpose.ph2.api.dao.db.entity.Ph2ModelDataEntity;
import com.logpose.ph2.api.dao.db.entity.Ph2ModelDataEntityExample;
import com.logpose.ph2.api.dao.db.mappers.Ph2ModelDataMapper;

public class PSModelDataExporter
	{
	// ===============================================
	// クラスメンバー
	// ===============================================
	private Ph2ModelDataMapper ph2ModelDataMapper;
	private double lastValue = 0;
	// ===============================================
	// コンストラクタ
	// ===============================================
	// --------------------------------------------------
	/**
	 * パラメータの設定と初期化
	 * @param ph2ModelDataMapper
	 */
	// --------------------------------------------------
	public PSModelDataExporter(Ph2ModelDataMapper ph2ModelDataMapper)
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
	 * @param value 推定積算樹冠光合成量データ
	 */
	// --------------------------------------------------
	public void add(Long dayId, Double value)
		{
// * デバイス日付IDに対応するモデルデータを取得する
		Ph2ModelDataEntityExample exm = new Ph2ModelDataEntityExample();
		exm.createCriteria().andDayIdEqualTo(dayId);
		List<Ph2ModelDataEntity> list = this.ph2ModelDataMapper.selectByExample(exm);
// * モデルデータを更新する
		Ph2ModelDataEntity entity = list.get(0);
// * 推定積算樹冠光合成量データの更新
		lastValue += entity.getCulmitiveCnopyPs();
		entity.setCrownLeafArea(lastValue);
// * モデルデータの更新
		this.ph2ModelDataMapper.updateByExample(entity, exm);
		}
	}
