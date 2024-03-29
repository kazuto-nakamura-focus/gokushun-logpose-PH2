package com.logpose.ph2.api.domain.auth;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.logpose.ph2.api.controller.dto.AuthCookieDTO;
import com.logpose.ph2.api.dao.api.entity.HerokuOauthAccountResponse;
import com.logpose.ph2.api.dao.api.entity.HerokuOauthTokenResponse;
import com.logpose.ph2.api.dao.db.entity.Ph2OauthEntity;
import com.logpose.ph2.api.dao.db.entity.Ph2UsersEntity;
import com.logpose.ph2.api.dao.db.entity.Ph2UsersEntityExample;
import com.logpose.ph2.api.dao.db.mappers.Ph2OauthMapper;
import com.logpose.ph2.api.dao.db.mappers.Ph2UsersMapper;

/**
 * Oauthのロジック処理を行う
 */
@Component
public class HerokuOAuthLogicDomain
	{
	// ===============================================
	// クラスメンバー
	// ===============================================
	public static final int NO_USER = -1;
	public static final int TOKEN_ERR = -2;
	public static final int OUT_OF_TIME = 1;
	public static final int OUT_OF_TERM = 2;
	public static final long DIFF = 1000 * 60 * 15;
	@Autowired
	private Ph2OauthMapper ph2OauthMapper;
	@Autowired
	private Ph2UsersMapper ph2UserMapper;

	// ===============================================
	// パブリック関数群
	// ===============================================
	// --------------------------------------------------
	/**
	 * トークン情報を取得する
	 * @param appId LogposeのユーザーID
	 * @return Ph2OauthEntity トークン情報
	 */
	// --------------------------------------------------
	public Ph2OauthEntity getOauthInfo(Long appId)
		{
		return this.ph2OauthMapper.selectByAppId(appId).get(0);
		}

	// --------------------------------------------------
	/**
	 * クライアントIDとアクセストークンを検証する
	 * @param accessToken アクセストークン
	 * @param entity Oauthの情報
	 * @return チェック結果
	 */
	// --------------------------------------------------
	public int checkUser(List<String> accessToken, Ph2OauthEntity entity)
		{
// * トークンの確認
		boolean istc = false;
		for (String tc : accessToken)
			{
			if (entity.getAccessToken().equals(tc))
				{
				istc = true;
				break;
				}
			}
		if (!istc)
			return TOKEN_ERR;

// * 有効期限
		Date now = new Date();
		if ((entity.getLoadTime().getTime() + entity.getExpiresIn()*1000) < now.getTime())
			{
			return OUT_OF_TIME;
			}
// * チェック時刻
		if (entity.getCheckTime().getTime() + DIFF < now.getTime())
			{
			return OUT_OF_TERM;
			}
		return 0;
		}

	// --------------------------------------------------
	/**
	 * ログイン時のユーザー情報の登録
	 * 
	 * @param code Herokuから与えられたコード
	 * @param token Herokuから与えられたトークン情報
	 * @param user Herokuから与えられたユーザー情報
	 * @return AuthCookieDTO ユーザーに返すべき情報
	 */
	// --------------------------------------------------
	@Transactional(rollbackFor = Exception.class)
	public AuthCookieDTO registerUser(String code, HerokuOauthTokenResponse token, HerokuOauthAccountResponse user)
		{
		AuthCookieDTO result = new AuthCookieDTO();
// * 時刻の設定
		Date now = new Date();
// * Authテーブルから既存のレコードを取得する
		Ph2OauthEntity entity = this.ph2OauthMapper.selectByPrimaryKey(token.getUserId());
		Ph2OauthEntity newEntity = null;
		if (null == entity)
			{
			newEntity = new Ph2OauthEntity();
			newEntity.setUserId(token.getUserId());
			}
		else
			{
			newEntity = entity;
			}
// * Ph2OauthEntityの設定
		this.setPh2OauthEntity(token, newEntity);
		newEntity.setCheckTime(now);
		newEntity.setLoadTime(now);
		newEntity.setToken(code);
		if (null == entity)
			{
			this.ph2OauthMapper.insert(newEntity);
			}
		else
			{
			this.ph2OauthMapper.updateByPrimaryKey(newEntity);
			}
// * Userテーブルから既存のレコードを取得する
		Ph2UsersEntityExample exm = new Ph2UsersEntityExample();
		exm.createCriteria().andAuthIdEqualTo(newEntity.getUserId());
		List<Ph2UsersEntity> users = this.ph2UserMapper.selectByExample(exm);
		Ph2UsersEntity newUser = (0 == users.size()) ? new Ph2UsersEntity() : users.get(0);

// * Userの設定
		newUser.setAuthId(newEntity.getUserId());
		newUser.setEmail(user.getEmail());
		if (null == user.getName())
			{
			newUser.setUsername(user.getEmail());
			}
		else
			{
			newUser.setUsername(user.getName());
			}
		newUser.setCreatedAt(now);
		newUser.setUpdatedAt(now);
		long id;
		if (0 == users.size())
			{
			id = this.ph2UserMapper.insert(newUser);
			}
		else
			{
			id = newUser.getId();
			this.ph2UserMapper.updateByPrimaryKey(newUser);
			}
// * Cookie情報の設定
		result.setAccessToken(token.getAccessToken());
		result.setId(id);
		result.setName(newUser.getUsername());
		return result;
		}

	// --------------------------------------------------
	/**
	 * ユーザ―のトークン情報を削除する
	 * 
	 * @param id ユーザーID
	 * @return アクセストークン
	 */
	// --------------------------------------------------
	@Transactional(rollbackFor = Exception.class)
	public Ph2OauthEntity getUser(Long id)
		{
		Ph2UsersEntity usr = this.ph2UserMapper.selectByPrimaryKey(id);
	    return this.ph2OauthMapper.selectByPrimaryKey(usr.getAuthId());
	//	this.ph2OauthMapper.deleteByPrimaryKey(usr.getAuthId());
	//	return oauth;
		}

	// --------------------------------------------------
	/**
	 * リフレッシュトークンの情報を更新する
	 * @param authDTO
	 * @param authRes
	 */
	// --------------------------------------------------
	@Transactional(rollbackFor = Exception.class)
	public void upateDB(Ph2OauthEntity entity, HerokuOauthTokenResponse authRes)
		{
		entity.setExpiresIn(Long.valueOf(authRes.getExpiresIn()));
		entity.setRefreshToken(authRes.getRefresh_token());
		entity.setAccessToken(authRes.getAccessToken());
		Date now = new Date();
		entity.setLoadTime(now);
		now.setTime(now.getTime() + DIFF);
		this.ph2OauthMapper.updateByPrimaryKey(entity);
		}

	// ===============================================
	// プライベート関数群
	// ===============================================
	// --------------------------------------------------
	/**
	 * Ph2OauthEntityを設定する
	 * @param code
	 * @param authRes
	 * @param entity
	 */
	// --------------------------------------------------
	private void setPh2OauthEntity(HerokuOauthTokenResponse authRes, Ph2OauthEntity entity)
		{
		entity.setAccessToken(authRes.getAccessToken());
		entity.setExpiresIn(Long.valueOf(authRes.getExpiresIn()));
		entity.setRefreshToken(authRes.getRefresh_token());
		}
	}
