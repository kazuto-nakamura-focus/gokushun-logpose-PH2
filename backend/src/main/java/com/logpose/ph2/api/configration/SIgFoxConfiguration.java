package com.logpose.ph2.api.configration;

import java.util.Base64;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SIgFoxConfiguration
	{
	// ===============================================
	// 公開関数
	// ===============================================
	// --------------------------------------------------
	/**
	 * SigFoxAPIを作成する。
	 * 
	 * @param params (DefaultSigFoxParameters)
	 * @return SigFoxAPI
	 */
	// --------------------------------------------------
	@Bean
	public DefaultSigFoxParameters createSigFoxAPI(DefaultSigFoxParameters params)
		{
// * TokyoデータのAuthorizationデータの作成
		String user = System.getenv(params.getUser());
		String password = System.getenv(params.getPassword());
		String auth = user + ":" + password;
		String encodedStr =
	            Base64.getEncoder().encodeToString(auth.getBytes());
		params.setBaseAuthTK(encodedStr);
// * NZデータのAuthorizationデータの作成		
		user = System.getenv(params.getUserNz());
		password = System.getenv(params.getPasswordNz());
		auth = user + ":" + password;
		encodedStr =
	            Base64.getEncoder().encodeToString(auth.getBytes());
		params.setBaseAuthTK(encodedStr);		
		return params;
		}
	}
