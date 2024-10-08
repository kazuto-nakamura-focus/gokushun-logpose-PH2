package com.logpose.ph2.api.dao.db.entity;

import java.util.Date;

public class Ph2DeviceLogEntity {

	/**
	 * This field was generated by MyBatis Generator. This field corresponds to the database column ph2_device_log.device_id
	 * @mbg.generated
	 */
	private Long deviceId;
	/**
	 * This field was generated by MyBatis Generator. This field corresponds to the database column ph2_device_log.time
	 * @mbg.generated
	 */
	private Date time;
	/**
	 * This field was generated by MyBatis Generator. This field corresponds to the database column ph2_device_log.message
	 * @mbg.generated
	 */
	private String message;
	/**
	 * This field was generated by MyBatis Generator. This field corresponds to the database column ph2_device_log.class_name
	 * @mbg.generated
	 */
	private String className;
	/**
	 * This field was generated by MyBatis Generator. This field corresponds to the database column ph2_device_log.status
	 * @mbg.generated
	 */
	private Integer status;
	/**
	 * This field was generated by MyBatis Generator. This field corresponds to the database column ph2_device_log.mode
	 * @mbg.generated
	 */
	private Short mode;

	/**
	 * This method was generated by MyBatis Generator. This method returns the value of the database column ph2_device_log.device_id
	 * @return  the value of ph2_device_log.device_id
	 * @mbg.generated
	 */
	public Long getDeviceId()
		{
		return deviceId;
		}

	/**
	 * This method was generated by MyBatis Generator. This method sets the value of the database column ph2_device_log.device_id
	 * @param deviceId  the value for ph2_device_log.device_id
	 * @mbg.generated
	 */
	public void setDeviceId(Long deviceId)
		{
		this.deviceId = deviceId;
		}

	/**
	 * This method was generated by MyBatis Generator. This method returns the value of the database column ph2_device_log.time
	 * @return  the value of ph2_device_log.time
	 * @mbg.generated
	 */
	public Date getTime()
		{
		return time;
		}

	/**
	 * This method was generated by MyBatis Generator. This method sets the value of the database column ph2_device_log.time
	 * @param time  the value for ph2_device_log.time
	 * @mbg.generated
	 */
	public void setTime(Date time)
		{
		this.time = time;
		}

	/**
	 * This method was generated by MyBatis Generator. This method returns the value of the database column ph2_device_log.message
	 * @return  the value of ph2_device_log.message
	 * @mbg.generated
	 */
	public String getMessage()
		{
		return message;
		}

	/**
	 * This method was generated by MyBatis Generator. This method sets the value of the database column ph2_device_log.message
	 * @param message  the value for ph2_device_log.message
	 * @mbg.generated
	 */
	public void setMessage(String message)
		{
		this.message = message == null ? null : message.trim();
		}

	/**
	 * This method was generated by MyBatis Generator. This method returns the value of the database column ph2_device_log.class_name
	 * @return  the value of ph2_device_log.class_name
	 * @mbg.generated
	 */
	public String getClassName()
		{
		return className;
		}

	/**
	 * This method was generated by MyBatis Generator. This method sets the value of the database column ph2_device_log.class_name
	 * @param className  the value for ph2_device_log.class_name
	 * @mbg.generated
	 */
	public void setClassName(String className)
		{
		this.className = className == null ? null : className.trim();
		}

	/**
	 * This method was generated by MyBatis Generator. This method returns the value of the database column ph2_device_log.status
	 * @return  the value of ph2_device_log.status
	 * @mbg.generated
	 */
	public Integer getStatus()
		{
		return status;
		}

	/**
	 * This method was generated by MyBatis Generator. This method sets the value of the database column ph2_device_log.status
	 * @param status  the value for ph2_device_log.status
	 * @mbg.generated
	 */
	public void setStatus(Integer status)
		{
		this.status = status;
		}

	/**
	 * This method was generated by MyBatis Generator. This method returns the value of the database column ph2_device_log.mode
	 * @return  the value of ph2_device_log.mode
	 * @mbg.generated
	 */
	public Short getMode()
		{
		return mode;
		}

	/**
	 * This method was generated by MyBatis Generator. This method sets the value of the database column ph2_device_log.mode
	 * @param mode  the value for ph2_device_log.mode
	 * @mbg.generated
	 */
	public void setMode(Short mode)
		{
		this.mode = mode;
		}
}