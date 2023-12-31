package com.logpose.ph2.batch.dao.db.mappers;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.logpose.ph2.batch.dao.db.entity.Ph2BrandMasterEntity;
import com.logpose.ph2.batch.dao.db.entity.Ph2BrandMasterEntityExample;
@Mapper
public interface Ph2BrandMasterMapper {

	/**
	 * This method was generated by MyBatis Generator. This method corresponds to the database table ph2_brand_master
	 * @mbg.generated  Thu Aug 03 01:50:05 JST 2023
	 */
	long countByExample(Ph2BrandMasterEntityExample example);

	/**
	 * This method was generated by MyBatis Generator. This method corresponds to the database table ph2_brand_master
	 * @mbg.generated  Thu Aug 03 01:50:05 JST 2023
	 */
	int deleteByExample(Ph2BrandMasterEntityExample example);

	/**
	 * This method was generated by MyBatis Generator. This method corresponds to the database table ph2_brand_master
	 * @mbg.generated  Thu Aug 03 01:50:05 JST 2023
	 */
	int deleteByPrimaryKey(Integer id);

	/**
	 * This method was generated by MyBatis Generator. This method corresponds to the database table ph2_brand_master
	 * @mbg.generated  Thu Aug 03 01:50:05 JST 2023
	 */
	int insert(Ph2BrandMasterEntity row);

	/**
	 * This method was generated by MyBatis Generator. This method corresponds to the database table ph2_brand_master
	 * @mbg.generated  Thu Aug 03 01:50:05 JST 2023
	 */
	int insertSelective(Ph2BrandMasterEntity row);

	/**
	 * This method was generated by MyBatis Generator. This method corresponds to the database table ph2_brand_master
	 * @mbg.generated  Thu Aug 03 01:50:05 JST 2023
	 */
	List<Ph2BrandMasterEntity> selectByExample(
			Ph2BrandMasterEntityExample example);

	/**
	 * This method was generated by MyBatis Generator. This method corresponds to the database table ph2_brand_master
	 * @mbg.generated  Thu Aug 03 01:50:05 JST 2023
	 */
	Ph2BrandMasterEntity selectByPrimaryKey(Integer id);

	/**
	 * This method was generated by MyBatis Generator. This method corresponds to the database table ph2_brand_master
	 * @mbg.generated  Thu Aug 03 01:50:05 JST 2023
	 */
	int updateByExampleSelective(@Param("row") Ph2BrandMasterEntity row,
			@Param("example") Ph2BrandMasterEntityExample example);

	/**
	 * This method was generated by MyBatis Generator. This method corresponds to the database table ph2_brand_master
	 * @mbg.generated  Thu Aug 03 01:50:05 JST 2023
	 */
	int updateByExample(@Param("row") Ph2BrandMasterEntity row,
			@Param("example") Ph2BrandMasterEntityExample example);

	/**
	 * This method was generated by MyBatis Generator. This method corresponds to the database table ph2_brand_master
	 * @mbg.generated  Thu Aug 03 01:50:05 JST 2023
	 */
	int updateByPrimaryKeySelective(Ph2BrandMasterEntity row);

	/**
	 * This method was generated by MyBatis Generator. This method corresponds to the database table ph2_brand_master
	 * @mbg.generated  Thu Aug 03 01:50:05 JST 2023
	 */
	int updateByPrimaryKey(Ph2BrandMasterEntity row);
}