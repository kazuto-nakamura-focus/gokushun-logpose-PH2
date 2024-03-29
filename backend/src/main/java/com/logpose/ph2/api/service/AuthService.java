package com.logpose.ph2.api.service;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.logpose.ph2.api.controller.dto.AuthCookieDTO;
import com.logpose.ph2.api.dao.api.entity.HerokuOauthAccountResponse;
import com.logpose.ph2.api.dao.api.entity.HerokuOauthTokenResponse;
import com.logpose.ph2.api.dao.db.entity.Ph2OauthEntity;
import com.logpose.ph2.api.domain.auth.HerokuOAuthAPIDomain;
import com.logpose.ph2.api.domain.auth.HerokuOAuthLogicDomain;

import lombok.Getter;

/**
 * ログイン・ログアウトの処理を行う
 *
 */
@Service
public class AuthService
	{
	// ===============================================
	// クラスメンバー
	// ===============================================
	@Autowired
	@Getter
	private HerokuOAuthAPIDomain herokuOAuthAPIDomain;
	@Autowired
	private HerokuOAuthLogicDomain herokuOAuthLogicDomain;

	// ===============================================
	// パブリック関数
	// ===============================================
	// --------------------------------------------------
	/**
	 * ログイン時の処理を行う
	 *@param code
	 *@param antiFoorgeryToken
	 * @return AuthCookieDTO
	 */
	// -------------------------------------------------
	public AuthCookieDTO login(String code, String antiFoorgeryToken)
		{
// * codeからアクセストークンを得る。
		HerokuOauthTokenResponse res = this.herokuOAuthAPIDomain.getAccessToken(code, antiFoorgeryToken);
// * アクセストークンからユーザー情報を取得する。
		HerokuOauthAccountResponse userInfo = this.herokuOAuthAPIDomain.getUserInfo(res.getUserId(),
				res.getAccessToken());
// * Authテーブルにトークン情報を設定する
		return this.herokuOAuthLogicDomain.registerUser(code, res, userInfo);
		}

	// --------------------------------------------------
	/**
	 * CookieデータをURLに変換する
	 * @param AuthCookieDTO
	 * @return String
	 */
	// -------------------------------------------------
	public String convertToURL(AuthCookieDTO cookie)
		{
		return this.herokuOAuthAPIDomain.getOriginURL(cookie);
		}

	// --------------------------------------------------
	/**
	 * ログインアウト処理を行う
	 *@param appUserId LogposeユーザーID
	 */
	// -------------------------------------------------
	public void logout(String appUserId)
		{
// * Authテーブルからトークンを削除する。
		Ph2OauthEntity auth = this.herokuOAuthLogicDomain.revokeUser(Long.valueOf(appUserId));
// * Herokuからトークンを無効化する
//		this.herokuOAuthAPIDomain.logout(auth.getUserId(), auth.getToken());
//		return this.herokuOAuthAPIDomain.getHerokuLogin();
		this.logoutFromHeroku(auth);
		}
	public  void logoutFromHeroku(Ph2OauthEntity auth)
		{
		String herokuLogoutUrl = "https://api.heroku.com/logout"; // Herokuログアウトのエンドポイント

		// HttpClientのインスタンスを生成
		HttpClient httpClient = HttpClients.createDefault();

		// HTTP DELETEリクエストを作成
		HttpDelete httpDelete = new HttpDelete(herokuLogoutUrl);

		// リクエストヘッダーに必要な情報を追加（例：認証トークン）
		httpDelete.setHeader("Accept", "application/vnd.heroku+json; version=3");
		httpDelete.setHeader("Authorization", "Bearer " +auth.getToken()); // アクセストークンを適切な値に置き換える

		try
			{
			// HTTPリクエストを実行し、レスポンスを取得
			HttpResponse response = httpClient.execute(httpDelete);

			// レスポンスのステータスコードを取得
			int statusCode = response.getStatusLine().getStatusCode();

			// レスポンスの内容を表示
			System.out.println("Herokuログアウトリクエストのレスポンスコード: " + statusCode);

			// レスポンスのエンティティを取得し、内容を表示
			HttpEntity entity = response.getEntity();
			if (entity != null)
				{
				String responseBody = EntityUtils.toString(entity);
				System.out.println("Herokuログアウトリクエストのレスポンスボディ: " + responseBody);
				}
			}
		catch (Exception e)
			{
			e.printStackTrace();
			}
		}

	}
