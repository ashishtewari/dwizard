package com.mebelkart.api.admin.v1;

import org.skife.jdbi.v2.DBI;

import com.mebelkart.api.admin.v1.dao.AdminDAO;
import com.mebelkart.api.admin.v1.resources.AdminResource;

import io.dropwizard.Application;
import io.dropwizard.jdbi.DBIFactory;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;

public class mkApiApplication extends Application<mkApiConfiguration> {

    /**
     * args will be server and relavant config file
     * @param args
     * @throws Exception
     */
    public static void main(final String[] args) throws Exception {
        new mkApiApplication().run(args);
    }

    /* (non-Javadoc)
     * @see io.dropwizard.Application#getName()
     */
    @Override
    public String getName() {
        return "mkApi";
    }

    /* (non-Javadoc)
     * @see io.dropwizard.Application#initialize(io.dropwizard.setup.Bootstrap)
     */
    @Override
    public void initialize(final Bootstrap<mkApiConfiguration> bootstrap) {
        // TODO: application initialization
    }

    /* (non-Javadoc)
     * @see io.dropwizard.Application#run(io.dropwizard.Configuration, io.dropwizard.setup.Environment)
     */
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
