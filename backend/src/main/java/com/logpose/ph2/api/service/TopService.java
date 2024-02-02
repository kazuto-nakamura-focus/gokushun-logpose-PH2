package com.logpose.ph2.api.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.logpose.ph2.api.dao.db.entity.Ph2ModelMasterEntity;
import com.logpose.ph2.api.dao.db.entity.Ph2ModelMasterEntityExample;
import com.logpose.ph2.api.dao.db.mappers.Ph2ModelMasterMapper;
import com.logpose.ph2.api.domain.TopDomain;
import com.logpose.ph2.api.dto.DataSummaryDTO;
import com.logpose.ph2.api.dto.ModelTargetDTO;
import com.logpose.ph2.api.dto.SelectionDTO;
import com.logpose.ph2.api.dto.element.Label;
import com.logpose.ph2.api.dto.rawData.RawDataList;
import com.logpose.ph2.api.dto.top.FieldDataWithSensor;

/**
 * トップサービスや基本サービスの提供を行う
 *
 */
@Service
public class TopService
	{
	// ===============================================
	// クラスメンバー
	// ===============================================
	@Autowired
	private Ph2ModelMasterMapper ph2ModelMasterMapper;
	@Autowired
	private TopDomain topDomain;

	// ===============================================
	// パブリック関数
	// ===============================================
	// --------------------------------------------------
	/**
	 * 全圃場サマリーデータ取得
	 *
	 * @return List<DataSummaryDTO>
	 */
	// -------------------------------------------------
	@Transactional(readOnly = true)
	public List<DataSummaryDTO> getFieldData()
		{
		return this.topDomain.getFieldData();
		}

	// --------------------------------------------------
	/**
	 * 検知データ圃場別取得
	 *
	 * @param contentId 検知するデータタイプのID
	 * @return List<FieldDataWithSensor>
	 */
	// --------------------------------------------------
	@Transactional(readOnly = true)
	public List<FieldDataWithSensor> getSummaryByFields(Long detectId)
		{
		return this.topDomain.getDeviceDataList(detectId);
		}
	// --------------------------------------------------
	/**
	 *  生データ取得API
	 *
	 * @param startDate 開始日
	 * @param endDate 終了日
	 * @param deviceId デバイスID
	 * @return RawDataList
	 */
	// --------------------------------------------------
	@Transactional(readOnly = true)
	public RawDataList getRawData(Date startDate, Date endDate, Long deviceId)
		{
		return this.topDomain.getRawData(startDate, endDate, deviceId);
		}
	// ###############################################
	/**
	 * モデル選択情報取得
	 *
	 * @return SelectionDTO
	 */
	// ###############################################
	@Transactional(readOnly = true)
	public SelectionDTO getModels(boolean isModel)
		{
		SelectionDTO result = new SelectionDTO();
		// * モデルの取得
		Ph2ModelMasterEntityExample exm = new Ph2ModelMasterEntityExample();
		exm.setOrderByClause("display_order");
		List<Ph2ModelMasterEntity> modelRecords = this.ph2ModelMasterMapper
				.selectByExample(exm);
		List<Label> models = new ArrayList<>();
		for (Ph2ModelMasterEntity item : modelRecords)
			{
			Label label = new Label();
			label.setId((long) item.getId());
			label.setName(item.getName());
			models.add(label);
			}
		result.setModels(models);

		List<ModelTargetDTO> targets = this.topDomain.getModelTargets(isModel);
		result.setTargets(targets);
		
		return result;
		}
	}
