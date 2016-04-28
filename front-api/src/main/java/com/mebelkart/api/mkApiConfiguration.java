package com.mebelkart.api;

import io.dropwizard.Configuration;
import io.dropwizard.db.DataSourceFactory;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

public class mkApiConfiguration extends Configuration {


	@Valid
	@NotNull
	private DataSourceFactory apiAuthenticationDatabase = new DataSourceFactory();
	private DataSourceFactory mebelkartProductsDatabase = new DataSourceFactory();

/*
	These variables are declared static because we will be accessing these variables in elasticFactory
	and to keep values read from config.yml we have to keep them static so that we can access them statically
	normal variables will loose these values between two objects
 */
	@NotNull
	private static String elasticsearchHost;
	@NotNull
	private static Integer elasticPort;

	private static String mkProdUrl;
	private static String mkProdUserName;
	private static String mkProdPassword;

	public static String getMkProdDriverClass() {
		return mkProdUrl;
	}

	public static void setMkProdDriverClass(String mkProdDriverClass) {
		mkApiConfiguration.mkProdUrl = mkProdDriverClass;
	}

	public static String getMkProdUserName() {
		return mkProdUserName;
	}

	public static void setMkProdUserName(String mkProdUserName) {
		mkApiConfiguration.mkProdUserName = mkProdUserName;
	}

	public static String getMkProdPassword() {
		return mkProdPassword;
	}

	public static void setMkProdPassword(String mkProdPassword) {
		mkApiConfiguration.mkProdPassword = mkProdPassword;
	}

	public static Integer getElasticPort() {
		return elasticPort;
	}

	public void setElasticPort(Integer elasticPort) {
		mkApiConfiguration.elasticPort = elasticPort;
	}

	@NotNull
	private static String clusterName;

	@JsonProperty
	public void setElasticsearchHost(String elasticsearchHost) {
//		System.out.println("mebelkart database user name "+mebelkartProductsDatabase.getUser());
//		System.out.println("mebelkart database user PASS "+mebelkartProductsDatabase.getPassword());
//		System.out.println("mebelkart db driver class "+mebelkartProductsDatabase.getDriverClass());
		mkApiConfiguration.elasticsearchHost = elasticsearchHost;
	}

	@JsonProperty
	public void setClusterName(String clusterName) {
		mkApiConfiguration.clusterName = clusterName;
	}

	@JsonProperty
	public static String getElasticsearchHost() {
		return elasticsearchHost;
	}

	@JsonProperty
	public static String getClusterName() {
		return clusterName;
	}

	@JsonProperty("apiAuthenticationDatabase")
	public DataSourceFactory getApiAuthenticationDatabase() {
		return apiAuthenticationDatabase;
	}
	@JsonProperty("apiAuthenticationDatabase")
	public void setDatabase1(DataSourceFactory apiAuthenticationDatabase) {
		this.apiAuthenticationDatabase = apiAuthenticationDatabase;
	}
	@JsonProperty("mebelkartProductsDatabase")
	public DataSourceFactory getMebelkartProductsDatabase() {
		mkApiConfiguration.mkProdUrl=mebelkartProductsDatabase.getUrl();
		mkApiConfiguration.mkProdUserName=mebelkartProductsDatabase.getUser();
		mkApiConfiguration.mkProdPassword=mebelkartProductsDatabase.getPassword();
		System.out.println("mebelkart database user name "+mkProdUrl);
		System.out.println("mebelkart database user PASS "+mkProdUserName);
		System.out.println("mebelkart db driver class "+mkProdPassword);
		return mebelkartProductsDatabase;
	}
	@JsonProperty("mebelkartProductsDatabase")
	public void setDatabase2(DataSourceFactory mebelkartProductsDatabase) {
		this.mebelkartProductsDatabase = mebelkartProductsDatabase;
	}
}
