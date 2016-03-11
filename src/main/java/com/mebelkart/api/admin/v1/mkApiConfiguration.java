package com.mebelkart.api.admin.v1;

import io.dropwizard.Configuration;
import io.dropwizard.db.DataSourceFactory;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

public class mkApiConfiguration extends Configuration {
    // TODO: implement service configuration
	@Valid
	@NotNull
	private DataSourceFactory database = new DataSourceFactory();

	/**
	 * @param factory
	 */
	@JsonProperty("database")
	public void setDataSourceFactory(DataSourceFactory factory) {
		this.database = factory;
	}

	/**
	 * @return
	 */
	@JsonProperty("database")
	public DataSourceFactory getDataSourceFactory() {
		return database;
	}
}
