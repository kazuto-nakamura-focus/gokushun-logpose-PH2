package com.logpose.ph2.api.domain.auth;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.logpose.ph2.api.controller.dto.AuthCookieDTO;
import com.logpose.ph2.api.dao.api.entity.HerokuOauthAccountResponse;
import com.logpose.ph2.api.dao.api.entity.HerokuOauthTokenResponse;
import com.logpose.ph2.api.dao.db.entity.Ph2OauthEntity;
import com.logpose.ph2.api.dao.db.entity.Ph2OauthEntityExample;
import com.logpose.ph2.api.dao.db.entity.Ph2UsersEntity;
import com.logpose.ph2.api.dao.db.entity.Ph2UsersEntityExample;
import com.logpose.ph2.api.dao.db.mappers.Ph2OauthMapper;
import com.logpose.ph2.api.dao.db.mappers.Ph2UsersMapper;

import jakarta.servlet.http.HttpServletRequest;

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
	public static final int NOT_SAME_ADDR = 2;
	public static final int IN_LOGOUT = 4;
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
	 * @param oauthId Ph2OAuthテーブルのID
	 * @return Ph2OauthEntity トークン情報
	 */
	// --------------------------------------------------
	public Ph2OauthEntity getOauthInfo(String oauthId)
		{
		if (null == oauthId) return null;
		return this.ph2OauthMapper.selectByPrimaryKey(oauthId);
		}

	// --------------------------------------------------
	/**
	 * 端末IDを取得する
	 * @param request サーブレット・リクエスト
	 * @return String リモートアドレス
	 */
	// --------------------------------------------------
	public String getRemoteIP(HttpServletRequest request)
		{
		String xForwardedFor = request.getHeader("X-Forwarded-For");
		// ELB等を経由していたらxForwardedForを返す
		if (xForwardedFor != null)
			{
			return xForwardedFor;
			}
		return request.getRemoteAddr();
		}

	// --------------------------------------------------
	/**
	 * クライアントIDとアクセストークンを検証する
	 * @param accessToken アクセストークン
	 * @patam remoteAddr リモートアドレス
	 * @param entity Oauthの情報
	 * @return チェック結果
	 */
	// --------------------------------------------------
	public int checkUser(String accessToken, String remoteAddr, Ph2OauthEntity entity)
		{
// * トークンの確認
		if (!entity.getAccessToken().equals(accessToken))
			{
			return TOKEN_ERR;
			}
// * リモートアドレスの確認
		if ((null == remoteAddr) || (!remoteAddr.equals(entity.getRemoteAddr())))
			{
			return NOT_SAME_ADDR;
			}
// * 有効期限
		Date now = new Date();
		if ((entity.getLoadTime().getTime() + entity.getExpiresIn() * 1000) < now.getTime())
			{
			return OUT_OF_TIME;
			}
// * ログアウト
		if (!entity.getIsEffective())
			{
			return IN_LOGOUT;
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
	 * @param remoteAddr リモートアドレス
	 * @return AuthCookieDTO ユーザーに返すべき情報
	 */
	// --------------------------------------------------
	@Transactional(rollbackFor = Exception.class)
	public AuthCookieDTO registerUser(
			String code,
			HerokuOauthTokenResponse token,
			HerokuOauthAccountResponse user,
			String remoteAddr)
		{
		AuthCookieDTO result = new AuthCookieDTO();
// * 時刻の設定
		Date now = new Date();
// * Authテーブルから既存のレコードを取得する
		Ph2OauthEntityExample exm = new Ph2OauthEntityExample();
		exm.createCriteria().andClientIdEqualTo(token.getUserId()).andRemoteAddrEqualTo(remoteAddr);
		List<Ph2OauthEntity> entities = this.ph2OauthMapper.selectByExample(exm);
		Ph2OauthEntity entity = (entities.size() > 0) ? entities.get(0) : null;
		Ph2OauthEntity newEntity = null;
// * 新規の場合
		if (null == entity)
			{
			newEntity = new Ph2OauthEntity();
			// IDの生成
			UUID uuid = UUID.randomUUID();
			newEntity.setId(uuid.toString());
			newEntity.setRemoteAddr(remoteAddr);
			newEntity.setClientId(token.getUserId());
			}
		else
			{
			newEntity = entity;
			}
// * Ph2OauthEntityの設定
		newEntity.setAccessToken(token.getAccessToken());
		newEntity.setExpiresIn(Long.valueOf(token.getExpiresIn()));
		newEntity.setRefreshToken(token.getRefreshToken());
		newEntity.setLoadTime(now);
		newEntity.setToken(code);
// * ログインの設定
		newEntity.setIsEffective(true);
// * DBへの登録
		if (null == entity)
			{
			this.ph2OauthMapper.insert(newEntity);
			}
		else
			{
			this.ph2OauthMapper.updateByPrimaryKey(newEntity);
			}
// * Userテーブルから既存のレコードを取得する
		Ph2UsersEntityExample uex = new Ph2UsersEntityExample();
		uex.createCriteria().andAuthIdEqualTo(newEntity.getClientId());
		List<Ph2UsersEntity> users = this.ph2UserMapper.selectByExample(uex);
		Ph2UsersEntity newUser = (0 == users.size()) ? new Ph2UsersEntity() : users.get(0);

// * Userの設定
		newUser.setAuthId(newEntity.getClientId());
		newUser.setEmail(user.getEmail());
		if (null == user.getName())
			{
			newUser.setUsername(user.getEmail());
			}
		else
			{
			newUser.setUsername(user.getName());
			}
		newUser.setUpdatedAt(now);
		if (0 == users.size())
			{
			newUser.setCreatedAt(now);
			this.ph2UserMapper.insert(newUser);
			}
		else
			{
			this.ph2UserMapper.updateByPrimaryKey(newUser);
			}
// * Cookie情報を設定する
		result.setAccessToken(token.getAccessToken());
		result.setAuthId(newEntity.getId());
		result.setName(newUser.getUsername());
		return result;
		}

	// --------------------------------------------------
	/**
	 * ユーザ―のトークン情報を無効にする
	 * 
	 * @param id OAuthテーブルID
	 * @return ユーザ―情報
	 */
	// --------------------------------------------------
	@Transactional(rollbackFor = Exception.class)
	public void logout(String id)
		{
		Ph2OauthEntity oauth = this.ph2OauthMapper.selectByPrimaryKey(id);
		
// 該当ユーザーのレコードを全てログアウト状態にする
		Ph2OauthEntityExample exm = new Ph2OauthEntityExample();
		exm.createCriteria().andClientIdEqualTo(oauth.getClientId());
		List<Ph2OauthEntity> entities = this.ph2OauthMapper.selectByExample(exm);
		for(final Ph2OauthEntity entity : entities)
			{
			entity.setIsEffective(false);
			this.ph2OauthMapper.updateByPrimaryKey(entity);
			}
		}

	// --------------------------------------------------
	/**
	 * リフレッシュトークンの情報を更新する
	 * @param entity Oauth情報
	 * @param authRes Herokuからの新しいトークン情報
	 */
	// --------------------------------------------------
	@Transactional(rollbackFor = Exception.class)
	public void upateDB(Ph2OauthEntity entity, HerokuOauthTokenResponse authRes)
		{
		entity.setExpiresIn(Long.valueOf(authRes.getExpiresIn()));
		entity.setRefreshToken(authRes.getRefreshToken());
		entity.setAccessToken(authRes.getAccessToken());
		entity.setLoadTime(new Date());
		this.ph2OauthMapper.updateByPrimaryKey(entity);
		}
	}
