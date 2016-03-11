package com.mebelkart.api.customer.v1;

import org.skife.jdbi.v2.DBI;
import com.mebelkart.api.customer.v1.dao.CustomerDAO;
import com.mebelkart.api.customer.v1.resources.CustomerResource;

import io.dropwizard.Application;
import io.dropwizard.jdbi.DBIFactory;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;

public class mkApiApplication extends Application<mkApiConfiguration> {

	public static void main(final String[] args) throws Exception {
		new mkApiApplication().run(args);
		System.out.println("args = " +args);
	}

	@Override
	public String getName() {
		return "mkApi";
	}

	@Override
	public void initialize(final Bootstrap<mkApiConfiguration> bootstrap) {
		// TODO: application initialization
	}

	@Override
	public void run(final mkApiConfiguration configuration,
			final Environment environment) {
		// TODO: implement application
		final DBIFactory factory = new DBIFactory();
		final DBI jdbi = factory.build(environment,
				configuration.getDataSourceFactory(), "mysql");
		final CustomerDAO dao = jdbi.onDemand(CustomerDAO.class);
		environment.jersey().register(new CustomerResource());
	}

}
