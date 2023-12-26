package com.logpose.ph2.api.configration;

import org.springframework.boot.context.properties.ConfigurationProperties;

import com.logpose.ph2.api.configration.DataSourceParameters.Properties;
import com.logpose.ph2.api.configration.DataSourceParameters.Properties.ConnectionProperties;
import com.logpose.ph2.api.configration.DataSourceParameters.Properties.HikariDataSourceProperties;

/**
 * DBへアクセスするためのパラメータ
 * @since 2023/12/25
 * @version 1.0
 */
@ConfigurationProperties(prefix = "spring.datasource")
public record DataSourceParameters(Properties schema1, Properties schema2)
	{
	public record Properties(
			ConnectionProperties connectionProperties,
			HikariDataSourceProperties hikariDataSourceProperties)
		{
		public record ConnectionProperties(String jdbcUrl, String username, String password)
			{
			}

		public record HikariDataSourceProperties(
				Integer maxLifetime,
				Integer minimumIdle,
				Integer maximumPoolSize)
			{
			}
		}
	}