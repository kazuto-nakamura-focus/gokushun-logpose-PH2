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
	public DefaultSecurityParameters createSigFoxAPI(DefaultSigFoxParameters params)
		{
		DefaultSecurityParameters dsp = new DefaultSecurityParameters();
// * TokyoデータのAuthorizationデータの作成
		String user = params.getUser();
		String password = params.getPassword();
		String auth = user + ":" + password;
		String encodedStr =
	            Base64.getEncoder().encodeToString(auth.getBytes());
		 dsp.setBaseAuthSigFoxTK(encodedStr);
// * NZデータのAuthorizationデータの作成		
		user =params.getUserNz();
		password = params.getPasswordNz();
		auth = user + ":" + password;
		encodedStr =
	            Base64.getEncoder().encodeToString(auth.getBytes());
		dsp.setBaseAuthSigFoxNZ(encodedStr);		
		return dsp;
		}
	}
