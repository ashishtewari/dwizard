package com.mebelkart.api.customer.v1;

import io.dropwizard.Configuration;
import io.dropwizard.db.DataSourceFactory;

import com.fasterxml.jackson.annotation.JsonProperty;


import javax.validation.Valid;
import javax.validation.constraints.*;

public class mkApiConfiguration extends Configuration {
    // TODO: implement service configuration
	@Valid
	@NotNull
	private DataSourceFactory database = new DataSourceFactory();

	@JsonProperty("database")
	public void setDataSourceFactory(DataSourceFactory factory) {
		this.database = factory;
	}

	@JsonProperty("database")
	public DataSourceFactory getDataSourceFactory() {
		return database;
	}
}
