package com.logpose.ph2.api.domain.photosynthesis;

import com.logpose.ph2.api.dao.db.entity.Ph2ModelDataEntity;
import com.logpose.ph2.api.dao.db.mappers.Ph2ModelDataMapper;

public class PSModelDataExporter
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
	public PSModelDataExporter(Ph2ModelDataMapper ph2ModelDataMapper)
		{
		this.ph2ModelDataMapper = ph2ModelDataMapper;
		}

	// ===============================================
	// 公開メソッド
	// ===============================================
	// --------------------------------------------------
	/**
	 * 受取ったモデルデータを更新してDBに追加する
	 * @param modelData 更新するモデルデータ
	 * @param value 推定積算樹冠光合成量データ
	 */
	// --------------------------------------------------
	public void add(Ph2ModelDataEntity modelData, Double value)
		{
// * 推定積算樹冠光合成量データの更新
		modelData.setCulmitiveCnopyPs(value);
// * モデルデータの更新
		this.ph2ModelDataMapper.updateByPrimaryKey(modelData);
		}
	}
