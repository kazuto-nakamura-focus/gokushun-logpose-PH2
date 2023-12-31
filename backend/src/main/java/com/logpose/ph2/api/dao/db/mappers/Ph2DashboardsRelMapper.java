package com.logpose.ph2.api.dao.db.mappers;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.logpose.ph2.api.dao.db.entity.Ph2DashboardsRelEntity;
import com.logpose.ph2.api.dao.db.entity.Ph2DashboardsRelEntityExample;
@Mapper
public interface Ph2DashboardsRelMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table ph2_dashboards_rel
     *
     * @mbg.generated Sat Jul 15 16:35:56 JST 2023
     */
    long countByExample(Ph2DashboardsRelEntityExample example);
	/**
	 * This method was generated by MyBatis Generator. This method corresponds to the database table ph2_dashboards_rel
	 * @mbg.generated  Thu Aug 03 01:50:05 JST 2023
	 */
	int deleteByExample(Ph2DashboardsRelEntityExample example);

	/**
	 * This method was generated by MyBatis Generator. This method corresponds to the database table ph2_dashboards_rel
	 * @mbg.generated  Thu Aug 03 01:50:05 JST 2023
	 */
	int insert(Ph2DashboardsRelEntity row);

	/**
	 * This method was generated by MyBatis Generator. This method corresponds to the database table ph2_dashboards_rel
	 * @mbg.generated  Thu Aug 03 01:50:05 JST 2023
	 */
	int insertSelective(Ph2DashboardsRelEntity row);

	/**
	 * This method was generated by MyBatis Generator. This method corresponds to the database table ph2_dashboards_rel
	 * @mbg.generated  Thu Aug 03 01:50:05 JST 2023
	 */
	List<Ph2DashboardsRelEntity> selectByExample(
			Ph2DashboardsRelEntityExample example);

	/**
	 * This method was generated by MyBatis Generator. This method corresponds to the database table ph2_dashboards_rel
	 * @mbg.generated  Thu Aug 03 01:50:05 JST 2023
	 */
	int updateByExampleSelective(@Param("row") Ph2DashboardsRelEntity row,
			@Param("example") Ph2DashboardsRelEntityExample example);

	/**
	 * This method was generated by MyBatis Generator. This method corresponds to the database table ph2_dashboards_rel
	 * @mbg.generated  Thu Aug 03 01:50:05 JST 2023
	 */
	int updateByExample(@Param("row") Ph2DashboardsRelEntity row,
			@Param("example") Ph2DashboardsRelEntityExample example);
}