package com.logpose.ph2.batch.dao.db.entity;

import java.util.Date;

public class Ph2UsersEntity {

	/**
	 * This field was generated by MyBatis Generator. This field corresponds to the database column ph2_users.id
	 * @mbg.generated  Thu Aug 03 01:50:05 JST 2023
	 */
	private Long id;
	/**
	 * This field was generated by MyBatis Generator. This field corresponds to the database column ph2_users.username
	 * @mbg.generated  Thu Aug 03 01:50:05 JST 2023
	 */
	private String username;
	/**
	 * This field was generated by MyBatis Generator. This field corresponds to the database column ph2_users.email
	 * @mbg.generated  Thu Aug 03 01:50:05 JST 2023
	 */
	private String email;
	/**
	 * This field was generated by MyBatis Generator. This field corresponds to the database column ph2_users.password_digest
	 * @mbg.generated  Thu Aug 03 01:50:05 JST 2023
	 */
	private String passwordDigest;
	/**
	 * This field was generated by MyBatis Generator. This field corresponds to the database column ph2_users.created_at
	 * @mbg.generated  Thu Aug 03 01:50:05 JST 2023
	 */
	private Date createdAt;
	/**
	 * This field was generated by MyBatis Generator. This field corresponds to the database column ph2_users.updated_at
	 * @mbg.generated  Thu Aug 03 01:50:05 JST 2023
	 */
	private Date updatedAt;

	/**
	 * This method was generated by MyBatis Generator. This method returns the value of the database column ph2_users.id
	 * @return  the value of ph2_users.id
	 * @mbg.generated  Thu Aug 03 01:50:05 JST 2023
	 */
	public Long getId()
		{
		return id;
		}

	/**
	 * This method was generated by MyBatis Generator. This method sets the value of the database column ph2_users.id
	 * @param id  the value for ph2_users.id
	 * @mbg.generated  Thu Aug 03 01:50:05 JST 2023
	 */
	public void setId(Long id)
		{
		this.id = id;
		}

	/**
	 * This method was generated by MyBatis Generator. This method returns the value of the database column ph2_users.username
	 * @return  the value of ph2_users.username
	 * @mbg.generated  Thu Aug 03 01:50:05 JST 2023
	 */
	public String getUsername()
		{
		return username;
		}

	/**
	 * This method was generated by MyBatis Generator. This method sets the value of the database column ph2_users.username
	 * @param username  the value for ph2_users.username
	 * @mbg.generated  Thu Aug 03 01:50:05 JST 2023
	 */
	public void setUsername(String username)
		{
		this.username = username == null ? null : username.trim();
		}

	/**
	 * This method was generated by MyBatis Generator. This method returns the value of the database column ph2_users.email
	 * @return  the value of ph2_users.email
	 * @mbg.generated  Thu Aug 03 01:50:05 JST 2023
	 */
	public String getEmail()
		{
		return email;
		}

	/**
	 * This method was generated by MyBatis Generator. This method sets the value of the database column ph2_users.email
	 * @param email  the value for ph2_users.email
	 * @mbg.generated  Thu Aug 03 01:50:05 JST 2023
	 */
	public void setEmail(String email)
		{
		this.email = email == null ? null : email.trim();
		}

	/**
	 * This method was generated by MyBatis Generator. This method returns the value of the database column ph2_users.password_digest
	 * @return  the value of ph2_users.password_digest
	 * @mbg.generated  Thu Aug 03 01:50:05 JST 2023
	 */
	public String getPasswordDigest()
		{
		return passwordDigest;
		}

	/**
	 * This method was generated by MyBatis Generator. This method sets the value of the database column ph2_users.password_digest
	 * @param passwordDigest  the value for ph2_users.password_digest
	 * @mbg.generated  Thu Aug 03 01:50:05 JST 2023
	 */
	public void setPasswordDigest(String passwordDigest)
		{
		this.passwordDigest = passwordDigest == null ? null
				: passwordDigest.trim();
		}

	/**
	 * This method was generated by MyBatis Generator. This method returns the value of the database column ph2_users.created_at
	 * @return  the value of ph2_users.created_at
	 * @mbg.generated  Thu Aug 03 01:50:05 JST 2023
	 */
	public Date getCreatedAt()
		{
		return createdAt;
		}

	/**
	 * This method was generated by MyBatis Generator. This method sets the value of the database column ph2_users.created_at
	 * @param createdAt  the value for ph2_users.created_at
	 * @mbg.generated  Thu Aug 03 01:50:05 JST 2023
	 */
	public void setCreatedAt(Date createdAt)
		{
		this.createdAt = createdAt;
		}

	/**
	 * This method was generated by MyBatis Generator. This method returns the value of the database column ph2_users.updated_at
	 * @return  the value of ph2_users.updated_at
	 * @mbg.generated  Thu Aug 03 01:50:05 JST 2023
	 */
	public Date getUpdatedAt()
		{
		return updatedAt;
		}

	/**
	 * This method was generated by MyBatis Generator. This method sets the value of the database column ph2_users.updated_at
	 * @param updatedAt  the value for ph2_users.updated_at
	 * @mbg.generated  Thu Aug 03 01:50:05 JST 2023
	 */
	public void setUpdatedAt(Date updatedAt)
		{
		this.updatedAt = updatedAt;
		}
}