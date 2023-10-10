package com.logpose.ph2.batch.domain;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.logpose.ph2.common.dao.db.entity.Ph2RelBaseDataEntityExample;
import com.logpose.ph2.common.dao.db.mappers.Ph2RelBaseDataMapper;

/**
 * BaseDataテーブルのドメイン
 */
@Component
public final class BaseDataDomain
	{
	// ===============================================
	// クラスメンバー
	// ===============================================
	@Autowired
	private Ph2RelBaseDataMapper ph2BaseRelDataMapper;

	// ===============================================
	// 公開関数群
	// ===============================================
	// --------------------------------------------------
	/**
	 * 基礎データテーブルの受信時刻移行のデータ削除。
	 *
	 * @param lastTime-最終処理レコードの受信時刻
	 */
	// --------------------------------------------------
	public void delete(Date lastTime)
		{
		if (null != lastTime)
			{
			Ph2RelBaseDataEntityExample exm = new Ph2RelBaseDataEntityExample();
			exm.createCriteria().andCastedAtGreaterThan(lastTime);
			this.ph2BaseRelDataMapper.deleteByExample(exm);
			}
		}
	}
