package com.logpose.ph2.api.dao.db.entity;

import java.util.Date;

public class Ph2WeatherForecastEntity {

	/**
	 * This field was generated by MyBatis Generator. This field corresponds to the database column ph2_wheather_forecast.device_id
	 * @mbg.generated
	 */
	private Long deviceId;
	/**
	 * This field was generated by MyBatis Generator. This field corresponds to the database column ph2_wheather_forecast.time
	 * @mbg.generated
	 */
	private Date time;
	/**
	 * This field was generated by MyBatis Generator. This field corresponds to the database column ph2_wheather_forecast.code
	 * @mbg.generated
	 */
	private Short code;
	/**
	 * This field was generated by MyBatis Generator. This field corresponds to the database column ph2_wheather_forecast.url
	 * @mbg.generated
	 */
	private String url;

	/**
	 * This method was generated by MyBatis Generator. This method returns the value of the database column ph2_wheather_forecast.device_id
	 * @return  the value of ph2_wheather_forecast.device_id
	 * @mbg.generated
	 */
	public Long getDeviceId()
		{
		return deviceId;
		}

	/**
	 * This method was generated by MyBatis Generator. This method sets the value of the database column ph2_wheather_forecast.device_id
	 * @param deviceId  the value for ph2_wheather_forecast.device_id
	 * @mbg.generated
	 */
	public void setDeviceId(Long deviceId)
		{
		this.deviceId = deviceId;
		}

	/**
	 * This method was generated by MyBatis Generator. This method returns the value of the database column ph2_wheather_forecast.time
	 * @return  the value of ph2_wheather_forecast.time
	 * @mbg.generated
	 */
	public Date getTime()
		{
		return time;
		}

	/**
	 * This method was generated by MyBatis Generator. This method sets the value of the database column ph2_wheather_forecast.time
	 * @param time  the value for ph2_wheather_forecast.time
	 * @mbg.generated
	 */
	public void setTime(Date time)
		{
		this.time = time;
		}

	/**
	 * This method was generated by MyBatis Generator. This method returns the value of the database column ph2_wheather_forecast.code
	 * @return  the value of ph2_wheather_forecast.code
	 * @mbg.generated
	 */
	public Short getCode()
		{
		return code;
		}

	/**
	 * This method was generated by MyBatis Generator. This method sets the value of the database column ph2_wheather_forecast.code
	 * @param code  the value for ph2_wheather_forecast.code
	 * @mbg.generated
	 */
	public void setCode(Short code)
		{
		this.code = code;
		}

	/**
	 * This method was generated by MyBatis Generator. This method returns the value of the database column ph2_wheather_forecast.url
	 * @return  the value of ph2_wheather_forecast.url
	 * @mbg.generated
	 */
	public String getUrl()
		{
		return url;
		}

	/**
	 * This method was generated by MyBatis Generator. This method sets the value of the database column ph2_wheather_forecast.url
	 * @param url  the value for ph2_wheather_forecast.url
	 * @mbg.generated
	 */
	public void setUrl(String url)
		{
		this.url = url == null ? null : url.trim();
		}
}