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
	private DataSourceFactory mebelkartProductsDatabase = new DataSourceFactory();

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
		return mebelkartProductsDatabase;
	}
	@JsonProperty("mebelkartProductsDatabase")
	public void setDatabase2(DataSourceFactory mebelkartProductsDatabase) {
		this.mebelkartProductsDatabase = mebelkartProductsDatabase;
	}
}
