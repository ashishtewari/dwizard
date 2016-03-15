package com.mebelkart.api;

import com.github.rkmk.container.FoldingListContainerFactory;
import com.github.rkmk.mapper.CustomMapperFactory;
import com.mebelkart.api.customer.v1.dao.CustomerAuthenticationDAO;
import com.mebelkart.api.customer.v1.dao.CustomerDetailsDAO;
import com.mebelkart.api.customer.v1.resources.CustomerResource;

import org.skife.jdbi.v2.DBI;

import io.dropwizard.Application;
import io.dropwizard.jdbi.DBIFactory;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;

public class mkApiApplication extends Application<mkApiConfiguration> {

    public static void main(final String[] args) throws Exception {
        new mkApiApplication().run(args);
        System.out.println("entered in main");
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
    public void run(final mkApiConfiguration configuration,final Environment environment) {
        // TODO: implement application
    	System.out.println("entered in run");
    	final DBIFactory factory = new DBIFactory();
		final DBI mkApiDatabaseConfiguration = factory.build(environment,configuration.getDatabase1(), "mk_api");
		final DBI mebelkartProdDatabaseConfiguration = factory.build(environment,configuration.getDatabase2(), "mebelkart_prod");
		final CustomerAuthenticationDAO customerAuthdao = mkApiDatabaseConfiguration.onDemand(CustomerAuthenticationDAO.class);
		final CustomerDetailsDAO customerDao = mebelkartProdDatabaseConfiguration.onDemand(CustomerDetailsDAO.class);
		mebelkartProdDatabaseConfiguration.registerMapper(new CustomMapperFactory());
		mebelkartProdDatabaseConfiguration.registerContainerFactory(new FoldingListContainerFactory());
		environment.jersey().register(new CustomerResource(customerAuthdao,customerDao));
		System.out.println("entered in last");
    }

}
