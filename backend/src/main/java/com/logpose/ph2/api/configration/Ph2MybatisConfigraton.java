package com.logpose.ph2.api.configration;

import java.util.Objects;

import javax.sql.DataSource;

import org.apache.ibatis.scripting.defaults.RawLanguageDriver;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import com.zaxxer.hikari.HikariDataSource;

import lombok.RequiredArgsConstructor;

@Configuration
@MapperScan(
	basePackages = "com.logpose.ph2.api.dao.db.mappers",
	sqlSessionFactoryRef = "schema2SqlSessionFactory")
@RequiredArgsConstructor
public class Ph2MybatisConfigraton
	{
	// ===============================================
	// 定数
	// ===============================================
	private static final String SCHEMA2_DATA_SOURCE_NAME = "schema2DataSource";
	private static final String SCHEMA2_DATA_SOURCE_PROPERTIES_NAME = "schema2DataSourceProperties";
	private static final String SCHEMA2_SQL_SESSION_FACTORY_NAME = "schema2SqlSessionFactory";
	private static final String DRIVER_CLASS_NAME = "com.mysql.cj.jdbc.Driver";
	// ===============================================
	// クラスメンバー
	// ===============================================
	private final DataSourceParameters dataSourceParameters;
	// ===============================================
	// 公開関数
	// ===============================================
	// --------------------------------------------------
	/**
	 * schema2スキーマ用のDataSourcePropertiesを取得
	 *
	 * @return schema2スキーマ用のDataSourceProperties
	 */
	// --------------------------------------------------
	public DataSourceProperties getSchema2DataSourceProperties()
		{
		return mapToDataSourceProperties(
				dataSourceParameters.schema2().connectionProperties().username(),
				dataSourceParameters.schema2().connectionProperties().password(),
				dataSourceParameters.schema2().connectionProperties().jdbcUrl());
		}
	// --------------------------------------------------
	/**
	 * DataSourcePropertiesを取得
	 *
	 * @param url DatabaseのエンドポイントURL
	 * @return DataSourceProperties
	 */
	// --------------------------------------------------
	private DataSourceProperties mapToDataSourceProperties(String username, String password,
			String url)
		{
		DataSourceProperties dataSourceProperties = new DataSourceProperties();
		dataSourceProperties.setDriverClassName(DRIVER_CLASS_NAME);
		dataSourceProperties.setUsername(username);
		dataSourceProperties.setPassword(password);
		dataSourceProperties.setUrl(url);

		return dataSourceProperties;
		}
	// --------------------------------------------------
	/**
	 * schema2スキーマ用のDataSourceのプロパティを作成
	 *
	 * @return DataSourceのプロパティ
	 */
	@Bean(name = SCHEMA2_DATA_SOURCE_PROPERTIES_NAME)
	@Primary
	public DataSourceProperties schema2DataSourceProperties()
		{
		return getSchema2DataSourceProperties();
		}
	// --------------------------------------------------
	/**
	 * schema2スキーマ用のDataSourceを作成
	 *
	 * @return DataSource
	 */
	// --------------------------------------------------
	@Bean(name = SCHEMA2_DATA_SOURCE_NAME)
	@Primary
	public DataSource schema2DataSource(
			@Qualifier(SCHEMA2_DATA_SOURCE_PROPERTIES_NAME) DataSourceProperties dataSourceProperties)
		{
		final HikariDataSource hikariDataSource = dataSourceProperties.initializeDataSourceBuilder()
				.type(HikariDataSource.class).build();
		return this.setPropertiesToHikariDataSource(hikariDataSource,
				dataSourceParameters.schema2().hikariDataSourceProperties());
		}
	// --------------------------------------------------
	/**
	 * HikariDataSourceにプロパティをセットする　　②
	 *
	 * @param hikariDataSource セット先HikariDataSource
	 * @param properties セットしたいプロパティ
	 * @return プロパティをセットしたHikariDataSource
	 */
	// --------------------------------------------------
	private HikariDataSource setPropertiesToHikariDataSource(
			HikariDataSource hikariDataSource,
			DataSourceParameters.Properties.HikariDataSourceProperties properties)
		{
		hikariDataSource.setMaxLifetime(properties.maxLifetime());
		hikariDataSource.setMaximumPoolSize(properties.maximumPoolSize());

		if (Objects.nonNull(properties.minimumIdle()))
			{
			hikariDataSource.setMinimumIdle(properties.minimumIdle());
			}

		return hikariDataSource;
		}
	// --------------------------------------------------
	/**
	 * schema2スキーマ用のSQLセッションファクトリを作成
	 *
	 * @return SQLセッションファクトリ
	 * @throws Exception 例外
	 */
	// --------------------------------------------------
	@Bean(name = SCHEMA2_SQL_SESSION_FACTORY_NAME)
	@Primary
	public SqlSessionFactory schema2SqlSessionFactory(
			@Qualifier(SCHEMA2_DATA_SOURCE_NAME) DataSource dataSource) throws Exception
		{
		SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean();
		sqlSessionFactoryBean.setDataSource(dataSource);

		final SqlSessionFactory sqlSessionFactory = sqlSessionFactoryBean.getObject();
		if (Objects.isNull(sqlSessionFactory))
			{
			throw new NullPointerException();
			}

		sqlSessionFactory.getConfiguration().setCacheEnabled(false);
		sqlSessionFactory.getConfiguration().setMapUnderscoreToCamelCase(true);
		sqlSessionFactory.getConfiguration().setDefaultScriptingLanguage(RawLanguageDriver.class);

		return sqlSessionFactory;
		}
	}
