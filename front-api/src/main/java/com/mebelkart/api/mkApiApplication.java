package com.mebelkart.api;

import com.mebelkart.api.mobileapi.dao.MobileDao;
import com.mebelkart.api.mobileapi.resources.MobileResource;
import com.mebelkart.api.order.v1.dao.OrderDao;
import com.mebelkart.api.order.v1.resources.OrderResource;
import de.spinscale.dropwizard.jobs.JobsBundle;

import org.skife.jdbi.v2.DBI;

import com.github.rkmk.container.FoldingListContainerFactory;
import com.github.rkmk.mapper.CustomMapperFactory;
import com.mebelkart.api.admin.v1.dao.AdminDAO;
import com.mebelkart.api.admin.v1.resources.AdminResource;
import com.mebelkart.api.customer.v1.dao.CustomerDetailsDAO;
import com.mebelkart.api.customer.v1.resources.CustomerResource;
import com.mebelkart.api.manufacturer.v1.dao.ManufacturerDetailsDAO;
import com.mebelkart.api.manufacturer.v1.resources.ManufacturerResource;
import com.mebelkart.api.product.v1.resources.ProductResource;
import com.mebelkart.api.util.exceptions.HandleNullRequest;

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
		try {
			/**
			 *  Registering jobs bundle to run all cron jobs
			 */
			//bootstrap.addBundle(new JobsBundle("com.mebelkart.api.util.cronTasks.Tasks"));
			//bootstrap.addBundle(new JobsBundle("com.mebelkart.api.util.cronTasks.jedis"));
		}
		catch (Exception e){
			System.out.println("Initialization Not Done ...........");
			e.printStackTrace();
		}
	}

	@Override
	public void run(final mkApiConfiguration configuration,final Environment environment) {
		final DBIFactory factory = new DBIFactory();
		/*
		 * configuring the both admin database and products database.
		 */
		final DBI apiAuthenticationDatabaseConfiguration = factory.build(environment,configuration.getApiAuthenticationDatabase(), "adminDatabase");
		final DBI mebelkartProductsDatabaseConfiguration = factory.build(environment,configuration.getMebelkartProductsDatabase(), "productsDatabase");
		/*
		 * creating object to the database classes and initializing them
		 */
		final CustomerDetailsDAO customerDao = mebelkartProductsDatabaseConfiguration.onDemand(CustomerDetailsDAO.class);
		final AdminDAO adminDao = apiAuthenticationDatabaseConfiguration.onDemand(AdminDAO.class);
		final OrderDao orderDaoForOrderResource= mebelkartProductsDatabaseConfiguration.onDemand(OrderDao.class);
		final ManufacturerDetailsDAO ManufacturerDao = mebelkartProductsDatabaseConfiguration.onDemand(ManufacturerDetailsDAO.class);
		final MobileDao mobileDao=mebelkartProductsDatabaseConfiguration.onDemand(MobileDao.class);
		/*
		 * Registering the database mapper classes
		 */
		mebelkartProductsDatabaseConfiguration.registerMapper(new CustomMapperFactory());
		mebelkartProductsDatabaseConfiguration.registerContainerFactory(new FoldingListContainerFactory());
		/*
		 * registering the resource classes
		 */
		environment.jersey().register(new AdminResource(adminDao));
		environment.jersey().register(new CustomerResource(customerDao));
		environment.jersey().register(new ManufacturerResource(ManufacturerDao));
		//environment.jersey().register(new ProductResource());
		environment.jersey().register(new HandleNullRequest());
		environment.jersey().register(new OrderResource(orderDaoForOrderResource));
		environment.jersey().register(new MobileResource(mobileDao));
	}

}