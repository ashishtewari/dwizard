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
	private DataSourceFactory mebelkartProductionDatabase = new DataSourceFactory();

/*
	These variables are declared static because we will be accessing these variables in elasticFactory
	and to keep values read from config.yml we have to keep them static so that we can access them statically
	normal variables will loose these values between two objects
 */
	@NotNull
	private static String elasticsearchHost;
	@NotNull
	private static Integer elasticPort;
	@NotNull
	private static String clusterName;
	@NotNull
	private static String productElasticsearchHost;
	@NotNull
	private static Integer productElasticPort;
	@NotNull
	private static String productClusterName;
	
	/*
	 * local elastic getters setters
	 */	
	public static Integer getElasticPort() {
		return elasticPort;
	}

	public void setElasticPort(Integer elasticPort) {
		mkApiConfiguration.elasticPort = elasticPort;
	}
	
	@JsonProperty
	public void setElasticsearchHost(String elasticsearchHost) {
		mkApiConfiguration.elasticsearchHost = elasticsearchHost;
	}

	@JsonProperty
	public static String getElasticsearchHost() {
		return elasticsearchHost;
	}

	@JsonProperty
	public void setClusterName(String clusterName) {
		mkApiConfiguration.clusterName = clusterName;
	}

	@JsonProperty
	public static String getClusterName() {
		return clusterName;
	}
	
	/*
	 * Products elastic getters setters
	 */	
	public static Integer getProductElasticPort() {
		return productElasticPort;
	}

	public void setProductElasticPort(Integer productElasticsearchPort) {
		mkApiConfiguration.productElasticPort = productElasticsearchPort;
	}
	
	@JsonProperty
	public void setProductElasticsearchHost(String productElasticSearchHost) {
		mkApiConfiguration.productElasticsearchHost = productElasticSearchHost;
	}

	@JsonProperty
	public static String getProductElasticsearchHost() {
		return productElasticsearchHost;
	}

	@JsonProperty
	public void setProductClusterName(String productElasticSearchClusterName) {
		mkApiConfiguration.productClusterName = productElasticSearchClusterName;
	}

	@JsonProperty
	public static String getProductClusterName() {
		return productClusterName;
	}

	@JsonProperty("apiAuthenticationDatabase")
	public DataSourceFactory getApiAuthenticationDatabase() {
		return apiAuthenticationDatabase;
	}
	@JsonProperty("apiAuthenticationDatabase")
	public void setDatabase1(DataSourceFactory apiAuthenticationDatabase) {
		this.apiAuthenticationDatabase = apiAuthenticationDatabase;
	}
	@JsonProperty("mebelkartProductionDatabase")
	public DataSourceFactory getMebelkartProductsDatabase() {
		return mebelkartProductionDatabase;
	}
	@JsonProperty("mebelkartProductionDatabase")
	public void setDatabase2(DataSourceFactory mebelkartProductionDatabase) {
		this.mebelkartProductionDatabase = mebelkartProductionDatabase;
	}
}
