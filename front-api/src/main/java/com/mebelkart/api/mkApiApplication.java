package com.mebelkart.api;

import org.skife.jdbi.v2.DBI;

import com.github.rkmk.container.FoldingListContainerFactory;
import com.github.rkmk.mapper.CustomMapperFactory;
import com.mebelkart.api.admin.v1.dao.AdminDAO;
import com.mebelkart.api.admin.v1.resources.AdminResource;
import com.mebelkart.api.customer.v1.dao.CustomerAuthenticationDAO;
import com.mebelkart.api.customer.v1.dao.CustomerDetailsDAO;
import com.mebelkart.api.customer.v1.resources.CustomerResource;
import com.mebelkart.api.util.HandleNullRequest;

import io.dropwizard.Application;
import io.dropwizard.jdbi.DBIFactory;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;

public class mkApiApplication extends Application<mkApiConfiguration> {

	public static void main(final String[] args) throws Exception {
		new mkApiApplication().run(args);
	}

	@Override
	public String getName() {
		return "mkApi";
	}

	@Override
	public void initialize(final Bootstrap<mkApiConfiguration> bootstrap) {
		
	}

	@Override
	public void run(final mkApiConfiguration configuration,
			final Environment environment) {
		final DBIFactory factory = new DBIFactory();
		/*
		 * configuring the both authentication database and mebelkart products database.
		 */
		final DBI apiAuthenticationDatabaseConfiguration = factory.build(environment,configuration.getDatabase1(), "mk_api");
		final DBI mebelkartProductsDatabaseConfiguration = factory.build(environment,configuration.getDatabase2(), "mebelkart_prod");
		/*
		 * creating object to the database classes and initializing them
		 */
		final CustomerAuthenticationDAO customerAuthdao = apiAuthenticationDatabaseConfiguration.onDemand(CustomerAuthenticationDAO.class);
		final CustomerDetailsDAO customerDao = mebelkartProductsDatabaseConfiguration.onDemand(CustomerDetailsDAO.class);
		final AdminDAO adminDao = apiAuthenticationDatabaseConfiguration.onDemand(AdminDAO.class);
		/*
		 * Registering the database mapper classes
		 */
		mebelkartProductsDatabaseConfiguration.registerMapper(new CustomMapperFactory());
		mebelkartProductsDatabaseConfiguration.registerContainerFactory(new FoldingListContainerFactory());
		/*
		 * registering the resource classes
		 */
		environment.jersey().register(new AdminResource(adminDao));
		environment.jersey().register(new CustomerResource(customerAuthdao,customerDao));
		environment.jersey().register(new HandleNullRequest());
	}

}