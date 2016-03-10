package com.mebelkart.api.admin.v1;

import org.skife.jdbi.v2.DBI;

import com.mebelkart.api.admin.v1.dao.AdminDAO;
import com.mebelkart.api.admin.v1.resources.AdminResource;

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
        // TODO: application initialization
    }

    @Override
    public void run(final mkApiConfiguration configuration,
                    final Environment environment) {
        // TODO: implement application
    	final DBIFactory factory = new DBIFactory();
	    final DBI jdbi = factory.build(environment, configuration.getDataSourceFactory(), "mysql");
	    final AdminDAO dao = jdbi.onDemand(AdminDAO.class);
		environment.jersey().register(new AdminResource(dao));
    }

}
