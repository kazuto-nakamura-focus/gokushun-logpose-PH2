package com.logpose.ph2.batch.dao.db.mappers;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.logpose.ph2.batch.dao.db.entity.Ph2RealLeafShootsAreaEntity;
import com.logpose.ph2.batch.dao.db.entity.Ph2RealLeafShootsAreaEntityExample;
@Mapper
public interface Ph2RealLeafShootsAreaMapper {

	/**
	 * This method was generated by MyBatis Generator. This method corresponds to the database table ph2_real_leaf_shoots_area
	 * @mbg.generated  Thu Aug 03 01:50:05 JST 2023
	 */
	long countByExample(Ph2RealLeafShootsAreaEntityExample example);

	/**
	 * This method was generated by MyBatis Generator. This method corresponds to the database table ph2_real_leaf_shoots_area
	 * @mbg.generated  Thu Aug 03 01:50:05 JST 2023
	 */
	int deleteByExample(Ph2RealLeafShootsAreaEntityExample example);

	/**
	 * This method was generated by MyBatis Generator. This method corresponds to the database table ph2_real_leaf_shoots_area
	 * @mbg.generated  Thu Aug 03 01:50:05 JST 2023
	 */
	int insert(Ph2RealLeafShootsAreaEntity row);

	/**
	 * This method was generated by MyBatis Generator. This method corresponds to the database table ph2_real_leaf_shoots_area
	 * @mbg.generated  Thu Aug 03 01:50:05 JST 2023
	 */
	int insertSelective(Ph2RealLeafShootsAreaEntity row);

	/**
	 * This method was generated by MyBatis Generator. This method corresponds to the database table ph2_real_leaf_shoots_area
	 * @mbg.generated  Thu Aug 03 01:50:05 JST 2023
	 */
	List<Ph2RealLeafShootsAreaEntity> selectByExample(
			Ph2RealLeafShootsAreaEntityExample example);

	/**
	 * This method was generated by MyBatis Generator. This method corresponds to the database table ph2_real_leaf_shoots_area
	 * @mbg.generated  Thu Aug 03 01:50:05 JST 2023
	 */
	int updateByExampleSelective(@Param("row") Ph2RealLeafShootsAreaEntity row,
			@Param("example") Ph2RealLeafShootsAreaEntityExample example);

	/**
	 * This method was generated by MyBatis Generator. This method corresponds to the database table ph2_real_leaf_shoots_area
	 * @mbg.generated  Thu Aug 03 01:50:05 JST 2023
	 */
	int updateByExample(@Param("row") Ph2RealLeafShootsAreaEntity row,
			@Param("example") Ph2RealLeafShootsAreaEntityExample example);
}