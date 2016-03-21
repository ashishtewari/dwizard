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
	public DataSourceFactory getDatabase1() {
		return apiAuthenticationDatabase;
	}
	@JsonProperty("apiAuthenticationDatabase")
	public void setDatabase1(DataSourceFactory database1) {
		this.apiAuthenticationDatabase = database1;
	}
	@JsonProperty("mebelkartProductsDatabase")
	public DataSourceFactory getDatabase2() {
		return mebelkartProductsDatabase;
	}
	@JsonProperty("mebelkartProductsDatabase")
	public void setDatabase2(DataSourceFactory database2) {
		this.mebelkartProductsDatabase = database2;
	}
}
