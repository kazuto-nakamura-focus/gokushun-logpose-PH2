package com.logpose.ph2.api.configration;

import java.util.Base64;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 *  APIアクセスのコンフィグレーションを行う
 *  @version 1.0
 *  @since 2024/01/31
 */
@Configuration
public class ExternalAPIConfiguration
	{
	// ===============================================
	// 公開関数
	// ===============================================
	// --------------------------------------------------
	/**
	 * APIへの認証情報を作成する
	 * 
	 * @param params (DefaultSigFoxParameters)
	 * @return DefaultSecurityParameters
	 */
	// --------------------------------------------------
	@Bean
	public DefaultSecurityParameters createSigFoxAPI(DefaultSigFoxParameters params)
		{
		DefaultSecurityParameters dsp = new DefaultSecurityParameters();
		
// * SigFoxのTokyoデータのAuthorizationデータの作成
		String user = params.getUser();
		String password = params.getPassword();
		String auth = user + ":" + password;
		String encodedStr = Base64.getEncoder().encodeToString(auth.getBytes());
		dsp.setBaseAuthSigFoxTK(encodedStr);
		
// * SigFoxのNZデータのAuthorizationデータの作成
		user = params.getUserNz();
		password = params.getPasswordNz();
		auth = user + ":" + password;
		encodedStr = Base64.getEncoder().encodeToString(auth.getBytes());
		dsp.setBaseAuthSigFoxNZ(encodedStr);
		
		return dsp;
		}
	}
