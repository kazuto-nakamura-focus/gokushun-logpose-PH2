package com.logpose.ph2.api.configration;

import java.util.Objects;

import javax.sql.DataSource;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

import com.zaxxer.hikari.HikariDataSource;

@Configuration
@MapperScan(basePackages = "com.logpose.ph1.api.dao.db.mappers", sqlSessionFactoryRef = "schema1SqlSessionFactory")
public class Ph1MybatisConfigraton
	{
	// ===============================================
	// 定数
	// ===============================================
	private static final String SCHEMA1_DATA_SOURCE_NAME = "schema1DataSource";
	private static final String SCHEMA1_DATA_SOURCE_PROPERTIES_NAME = "schema1DataSourceProperties";
	private static final String SCHEMA1_SQL_SESSION_FACTORY_NAME = "schema1SqlSessionFactory";
	private static final String SCHEMA1_TRANSACTION_NAME = "txManager1";

	// ===============================================
	// 公開関数
	// ===============================================
	// --------------------------------------------------
	/**
	 * SQL Serverのデータソースプロパティを生成する
	 * @return SQL Serverのデータソースプロパティ
	 */
	// --------------------------------------------------
	@Bean(name = { SCHEMA1_DATA_SOURCE_PROPERTIES_NAME })
	@ConfigurationProperties(prefix = "spring.datasource.schema1")
	public DataSourceProperties datasource1Properties()
		{
		return new DataSourceProperties();
		}

	// --------------------------------------------------
	/**
	 * schema1スキーマ用のDataSourceを作成
	 *
	 * @return DataSource
	 */
	// --------------------------------------------------
	@Bean(name = { SCHEMA1_DATA_SOURCE_NAME })
	public DataSource schema1DataSource(
			@Qualifier(SCHEMA1_DATA_SOURCE_PROPERTIES_NAME) DataSourceProperties properties,
			DefaultHikariParameter hikari)
		{
		final HikariDataSource hikariDataSource = properties.initializeDataSourceBuilder()
				.type(HikariDataSource.class).build();
		return this.setPropertiesToHikariDataSource(hikariDataSource, hikari);
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
			DefaultHikariParameter parameters)
		{
		hikariDataSource.setMaxLifetime(parameters.getMaxLifetime1());
		hikariDataSource.setMaximumPoolSize(parameters.getMaximumPoolSize1());

		if (Objects.nonNull(parameters.getMinimumIdle1()))
			{
			hikariDataSource.setMinimumIdle(parameters.getMinimumIdle1());
			}

		return hikariDataSource;
		}

	// --------------------------------------------------
	/**
	 * SQL Serverのトランザクションマネージャを生成する
	 * @param dataSourceSs SQL Serverのデータソース
	 * @return SQL Serverのトランザクションマネージャ
	 */
	// --------------------------------------------------
	@Bean(name = { SCHEMA1_TRANSACTION_NAME })
	public PlatformTransactionManager txManagerSs(
			@Qualifier(SCHEMA1_DATA_SOURCE_NAME) DataSource dataSource1)
		{
		return new DataSourceTransactionManager(dataSource1);
		}

	// --------------------------------------------------
	/**
	 * schema1スキーマ用のSQLセッションファクトリを作成
	 *
	 * @return SQLセッションファクトリ
	 * @throws Exception 例外
	 */
	// --------------------------------------------------
	@Bean(name = SCHEMA1_SQL_SESSION_FACTORY_NAME)
	public SqlSessionFactory schema1SqlSessionFactory(
			@Qualifier(SCHEMA1_DATA_SOURCE_NAME) DataSource dataSource) throws Exception
		{
		SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean();
		sqlSessionFactoryBean.setDataSource(dataSource);

		return sqlSessionFactoryBean.getObject();
		}
	}
