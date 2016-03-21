package com.mebelkart.api;

import io.dropwizard.Configuration;
import io.dropwizard.db.DataSourceFactory;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

public class mkApiConfiguration extends Configuration {
    // TODO: implement service configuration
	@Valid
	@NotNull
	private DataSourceFactory database1 = new DataSourceFactory();
	private DataSourceFactory database2 = new DataSourceFactory();

	@JsonProperty("database1")
	public DataSourceFactory getDatabase1() {
		return database1;
	}
	@JsonProperty("database1")
	public void setDatabase1(DataSourceFactory database1) {
		this.database1 = database1;
	}
	@JsonProperty("database2")
	public DataSourceFactory getDatabase2() {
		return database2;
	}
	@JsonProperty("database2")
	public void setDatabase2(DataSourceFactory database2) {
		this.database2 = database2;
	}
}
