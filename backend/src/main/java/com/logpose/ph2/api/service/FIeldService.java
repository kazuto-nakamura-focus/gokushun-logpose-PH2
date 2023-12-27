package com.logpose.ph2.api.service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.logpose.ph2.api.dao.db.entity.Ph2FieldsEntity;
import com.logpose.ph2.api.dao.db.entity.Ph2FieldsEntityExample;
import com.logpose.ph2.api.dao.db.entity.Ph2RelFieldDeviceEntityExample;
import com.logpose.ph2.api.dao.db.mappers.Ph2FieldsMapper;
import com.logpose.ph2.api.dao.db.mappers.Ph2RelFieldDeviceMapper;
import com.logpose.ph2.api.dao.db.mappers.joined.Ph2FieldDeviceJoinMapper;
import com.logpose.ph2.api.domain.DeviceDomain;
import com.logpose.ph2.api.dto.FieldDetailDTO;
import com.logpose.ph2.api.dto.FieldInfoDTO;
import com.logpose.ph2.api.dto.element.DeviceInfo;



/**
 * 圃場に関する参照・更新サービス
 *
 */
@Service
public class FIeldService
	{
	// ===============================================
	// クラスメンバー
	// ===============================================
	@Autowired
	private Ph2FieldsMapper ph2FieldsMapper;
	@Autowired
	private Ph2FieldDeviceJoinMapper ph2FieldDeviceSetJoinMapper;
	@Autowired
	private DeviceDomain deviceDomain;
	@Autowired
	private Ph2RelFieldDeviceMapper  ph2RelFieldDeviceMapper;

	// ===============================================
	// パブリック関数
	// ===============================================
	// ###############################################
	/**
	 * 圃場一覧取得
	 *
	 * @return List<FieldInfoDTO>
	 */
	// ###############################################
	public List<FieldInfoDTO> list()
		{
		List<FieldInfoDTO> result = new ArrayList<>();
		Ph2FieldsEntityExample field = new Ph2FieldsEntityExample();
		field.setOrderByClause("name asc");
		List<Ph2FieldsEntity> fieldRecords = this.ph2FieldsMapper.selectByExample(field);
		for (Ph2FieldsEntity item : fieldRecords)
			{
			FieldInfoDTO info = new FieldInfoDTO();
			info.setContructor(item.getClient());
			info.setId(item.getId());
			info.setName(item.getName());
			info.setLocation(item.getLocation());
			info.setLatitude(item.getLatitude());
			info.setRegisteredDate(item.getCreatedAt().toLocaleString());
			result.add(info);
			}
		return result;
		}

	// ###############################################
	/**
	 * 圃場削除
	 *
	 * @param fieldId 圃場ID
	 */
	// ###############################################
	public void delete(Long fieldId)
		{
		this.ph2FieldsMapper.deleteByPrimaryKey(fieldId);
		
		Ph2RelFieldDeviceEntityExample exm  = new Ph2RelFieldDeviceEntityExample();
		exm.createCriteria().andFieldIdEqualTo(fieldId);
		this.ph2RelFieldDeviceMapper.deleteByExample(exm);
		
		this.deviceDomain.deleteByFieldId(fieldId);
		}

	// ###############################################
	/**
	 * 圃場情報詳細取得
	 *
	 * @param fieldId 圃場ID
	 * @return FieldDetailDTO
	 */
	// ###############################################
	public FieldDetailDTO getInfo(Long fieldId)
		{
		// * 圃場情報の取得
		Ph2FieldsEntity field = this.ph2FieldsMapper.selectByPrimaryKey(fieldId);
		// * デバイス情報の取得
		List<DeviceInfo> devices = this.ph2FieldDeviceSetJoinMapper.selectDeviceInfoByField(fieldId);
		// * 返却データの作成
		FieldDetailDTO dto = new FieldDetailDTO();
		dto.setContructor(field.getClient());
		dto.setDeviceList(devices);
		dto.setId(field.getId());
		dto.setLocation(field.getLocation());
		dto.setName(field.getName());
		dto.setLatitude(field.getLatitude());
		dto.setLongitude(field.getLongitude());
		dto.setRegisteredDate(field.getCreatedAt().toLocaleString());
		return dto;
		}

	// ###############################################
	/**
	 * 圃場情報追加
	 *
	 * @param dto FieldInfoDTO
	 */
	// ###############################################
	public Long addInfo(FieldInfoDTO dto)
		{
		// * 圃場情報の作成
		Ph2FieldsEntity field = new Ph2FieldsEntity();
		field.setClient(dto.getContructor());
		field.setLatitude(dto.getLatitude());
		field.setLocation(dto.getLocation());
		field.setLongitude(dto.getLongitude());
		field.setName(dto.getName());
		field.setCommenceDate(dto.getContructor());
		field.setCreatedAt(new Timestamp(System.currentTimeMillis()));
		field.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
		
		return this.ph2FieldsMapper.insert(field);
		}

	// ###############################################
	/**
	 * 圃場情報更新
	 *
	 * @param dto FieldInfoDTO
	 */
	// ###############################################
	public void updateInfo(FieldInfoDTO dto)
		{
		// * 圃場情報の取得
		Ph2FieldsEntity field = this.ph2FieldsMapper.selectByPrimaryKey(dto.getId());
		// * 圃場情報の更新
		field.setClient(dto.getContructor());
		field.setLatitude(dto.getLatitude());
		field.setLocation(dto.getLocation());
		field.setLongitude(dto.getLongitude());
		field.setName(dto.getName());
		field.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
		this.ph2FieldsMapper.updateByPrimaryKey(field);
		}

	}
