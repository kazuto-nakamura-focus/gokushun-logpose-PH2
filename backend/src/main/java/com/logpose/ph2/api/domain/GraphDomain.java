package com.logpose.ph2.api.domain;

import org.springframework.beans.factory.annotation.Autowired;

import com.logpose.ph2.api.dao.db.mappers.joined.Ph2ParamSetJoinMapper;
import com.logpose.ph2.api.dto.RealModelGraphDataDTO;

public class GraphDomain
	{
	// ===============================================
	// クラスメンバー
	// ===============================================
	@Autowired
	private Ph2ParamSetJoinMapper ph2ParamSetJoinMapper;

	// ===============================================
	// 公開関数群
	// ===============================================
	// --------------------------------------------------
	/**
	 * グラフにコメントを付与する
	 * 
	 * @param parameterId
	 * @param dto
	 */
	// --------------------------------------------------
	public void setComment(Long device, Short year, RealModelGraphDataDTO dto)
		{
		String comment = this.ph2ParamSetJoinMapper.selectLatestComment(device, year).get(0);
		dto.setComment(comment);
		}
	}
