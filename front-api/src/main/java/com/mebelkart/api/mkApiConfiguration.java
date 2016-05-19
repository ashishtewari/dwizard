package com.mebelkart.api;

import io.dropwizard.Configuration;
import io.dropwizard.db.DataSourceFactory;

import com.fasterxml.jackson.annotation.JsonProperty;
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
	@NotNull
	private static String redisHost;
	@NotNull
	private static Integer redisPort;
	@NotNull
	private static String redisPassword;
	
	private static String apiInterfaceAdmin;
	private static String apiInterfaceSuperAdmin;
	
	public void setApiInterfaceSuperAdmin(String user){
		mkApiConfiguration.apiInterfaceSuperAdmin = user;
	}
	public static String getApiInterfaceSuperAdmin(){
		return apiInterfaceSuperAdmin;
	}
	
	public void setApiInterfaceAdmin(String user){
		mkApiConfiguration.apiInterfaceAdmin = user;
	}
	public static String getApiInterfaceAdmin(){
		return apiInterfaceAdmin;
	}
	
	public static String getRedisPassword(){
		return redisPassword;
	}
	
	public void setRedisPassword(String redisPassword){
		mkApiConfiguration.redisPassword = redisPassword;
	}
	
	public static Integer getRedisPort() {
		return redisPort;
	}

	public void setRedisPort(Integer redisPort) {
		mkApiConfiguration.redisPort = redisPort;
	}

	@JsonProperty
	public void setRedisHost(String redisHost) {
		mkApiConfiguration.redisHost = redisHost;
	}

	@JsonProperty
	public static String getRedisHost() {
		return redisHost;
	}

	private static String mkProdUrl;
	private static String mkProdUserName;
	private static String mkProdPassword;

	public static String getMkProdDriverClass() {
		return mkProdUrl;
	}

	public static String getMkProdUserName() {
		return mkProdUserName;
	}

	public static String getMkProdPassword() {
		return mkProdPassword;
	}
	
	private static String mkAuthUrl;
	private static String mkAuthUserName;
	private static String mkAuthPassword;
	
	public static String getMkAuthDriverClass() {
		return mkAuthUrl;
	}

	public static String getMkAuthUserName() {
		return mkAuthUserName;
	}

	public static String getMkAuthPassword() {
		return mkAuthPassword;
	}
	
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
		/*
			Intializing prod databasae variables to access them in any other class
		 */
		mkApiConfiguration.mkAuthUrl = apiAuthenticationDatabase.getUrl();
		mkApiConfiguration.mkAuthUserName = apiAuthenticationDatabase.getUser();
		mkApiConfiguration.mkAuthPassword = apiAuthenticationDatabase.getPassword();
		return apiAuthenticationDatabase;
	}
	@JsonProperty("apiAuthenticationDatabase")
	public void setDatabase1(DataSourceFactory apiAuthenticationDatabase) {
		this.apiAuthenticationDatabase = apiAuthenticationDatabase;
	}
	@JsonProperty("mebelkartProductionDatabase")
	public DataSourceFactory getMebelkartProductsDatabase() {
		/*
			Intializing prod databasae variables to access them in any other class
		 */
		mkApiConfiguration.mkProdUrl=mebelkartProductionDatabase.getUrl();
		mkApiConfiguration.mkProdUserName=mebelkartProductionDatabase.getUser();
		mkApiConfiguration.mkProdPassword=mebelkartProductionDatabase.getPassword();
		return mebelkartProductionDatabase;
	}
	@JsonProperty("mebelkartProductionDatabase")
	public void setDatabase2(DataSourceFactory mebelkartProductionDatabase) {
		this.mebelkartProductionDatabase = mebelkartProductionDatabase;
	}
}
