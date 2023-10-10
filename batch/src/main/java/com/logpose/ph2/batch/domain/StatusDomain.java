package com.logpose.ph2.batch.domain;

import java.util.Calendar;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.logpose.ph2.common.dao.db.entity.Ph2SystemStatusEntity;
import com.logpose.ph2.common.dao.db.mappers.Ph2SystemStatusMapper;

/**
 * SystemStatusテーブルのドメイン
 */
@Component
public final class StatusDomain
	{
	// ===============================================
	// クラスメンバー
	// ===============================================
	@Autowired
	private Ph2SystemStatusMapper ph2SystemStatusMapper;

	// ===============================================
	// 公開関数群
	// ===============================================
	/** --------------------------------------------------
	 * システムステータステーブル(ph2_system_status)から最終処理レコードの受信時刻を得る。
	 *
	 * @param taskName タスク名
	 * @return タスクの最終処理レコードの受信時刻を含んだシステムステータスのエンティティ
	 ------------------------------------------------------ */
	public Ph2SystemStatusEntity getSystemStatus(String taskName)
		{
		// * ステータステーブルから指定されたタスクの状態を取得する
		Ph2SystemStatusEntity status = this.ph2SystemStatusMapper
				.selectByPrimaryKey(taskName);
		// * タスクが存在しなければ、レコードを追加する
		if (null == status)
			{
			status = new Ph2SystemStatusEntity();
			status.setTableName(taskName);
			this.ph2SystemStatusMapper.insert(status);
			}
		// * 最終処理レコードの受信時刻が未設定の場合、最も古い時刻に設定する

		if (null == status.getLastTime())
			{
			Calendar cal = Calendar.getInstance();
			cal.set(Calendar.YEAR, 2020);
			status.setLastTime(cal.getTime());
			}
		return status;
		}

	/** --------------------------------------------------
	 * システムステータステーブル(ph2_system_status)の最終処理レコードの時刻設定する
	 *
	 * @param entity システムステータスのエンティティ
	 ------------------------------------------------------ */
	public void update(Ph2SystemStatusEntity entity)
		{
		this.ph2SystemStatusMapper.updateByPrimaryKey(entity);
		}
	}
