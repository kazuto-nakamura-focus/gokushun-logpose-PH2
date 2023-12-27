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
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

import com.zaxxer.hikari.HikariDataSource;

@Configuration
@MapperScan(
		basePackages = {"com.logpose.ph2.api.dao.db.mappers"},
		sqlSessionFactoryRef = "schema2SqlSessionFactory")
public class Ph2MybatisConfigraton
	{
	// ===============================================
	// 定数
	// ===============================================
	private static final String SCHEMA2_DATA_SOURCE_NAME = "schema2DataSource";
	private static final String SCHEMA2_DATA_SOURCE_PROPERTIES_NAME = "schema2DataSourceProperties";
	private static final String SCHEMA2_SQL_SESSION_FACTORY_NAME = "schema2SqlSessionFactory";
	private static final String SCHEMA2_TRANSACTION_NAME = "txManager2";

	// ===============================================
	// 公開関数
	// ===============================================
	// --------------------------------------------------
	/**
	 * SQL Serverのデータソースプロパティを生成する
	 * @return SQL Serverのデータソースプロパティ
	 */
	// --------------------------------------------------
	@Bean(name = { SCHEMA2_DATA_SOURCE_PROPERTIES_NAME })
	@ConfigurationProperties(prefix = "spring.datasource.schema2")
	public DataSourceProperties datasource2Properties()
		{
		return new DataSourceProperties();
		}

	// --------------------------------------------------
	/**
	 * schema2スキーマ用のDataSourceを作成
	 *
	 * @return DataSource
	 */
	// --------------------------------------------------
	@Bean(name = { SCHEMA2_DATA_SOURCE_NAME })
	public DataSource schema2DataSource(
			@Qualifier(SCHEMA2_DATA_SOURCE_PROPERTIES_NAME) DataSourceProperties properties,
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
			DefaultHikariParameter parameters
			)
		{
		hikariDataSource.setMaxLifetime(parameters.getMaxLifetime2());
		hikariDataSource.setMaximumPoolSize(parameters.getMaximumPoolSize2());

		if (Objects.nonNull(parameters.getMinimumIdle2()))
			{
			hikariDataSource.setMinimumIdle(parameters.getMinimumIdle2());
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
	@Bean(name = { SCHEMA2_TRANSACTION_NAME })
	@Primary
	public PlatformTransactionManager txManagerSs(
			@Qualifier(SCHEMA2_DATA_SOURCE_NAME) DataSource dataSource2)
		{
		return new DataSourceTransactionManager(dataSource2);
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

		return sqlSessionFactoryBean.getObject();
		}
	}
