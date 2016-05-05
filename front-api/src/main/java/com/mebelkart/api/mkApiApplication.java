package com.mebelkart.api;

import java.util.EnumSet;

import javax.servlet.DispatcherType;
import javax.servlet.FilterRegistration;

import com.mebelkart.api.product.v1.dao.ProductDao;
import com.mebelkart.api.order.v1.dao.OrderDao;
import com.mebelkart.api.order.v1.resources.OrderResource;
import static org.eclipse.jetty.servlets.CrossOriginFilter.*;


import de.spinscale.dropwizard.jobs.JobsBundle;
import org.eclipse.jetty.servlets.CrossOriginFilter;
import org.skife.jdbi.v2.DBI;

import com.github.rkmk.container.FoldingListContainerFactory;
import com.github.rkmk.mapper.CustomMapperFactory;
import com.mebelkart.api.admin.v1.dao.AdminDAO;
import com.mebelkart.api.admin.v1.resources.AdminResource;
import com.mebelkart.api.category.v1.dao.CategoryDao;
import com.mebelkart.api.category.v1.resources.CategoryResource;
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

import javax.servlet.DispatcherType;
import javax.servlet.FilterRegistration;
import java.util.EnumSet;

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
	public void run(final mkApiConfiguration configuration,final Environment environment) {

		// Enable CORS headers
		final FilterRegistration.Dynamic cors = environment.servlets().addFilter("CORS", CrossOriginFilter.class);

		// Configure CORS parameters
		cors.setInitParameter("allowedOrigins", "*");
		cors.setInitParameter("allowedHeaders", "*");
		cors.setInitParameter("allowedMethods", "OPTIONS,GET,PUT,POST,DELETE,HEAD");

		// Add URL mapping
		cors.addMappingForUrlPatterns(EnumSet.allOf(DispatcherType.class), true, "/*");
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
		final ProductDao productDao =mebelkartProductsDatabaseConfiguration.onDemand(ProductDao.class);
		final CategoryDao categoryDao = mebelkartProductsDatabaseConfiguration.onDemand(CategoryDao.class);
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
		environment.jersey().register(new ProductResource(productDao));
		environment.jersey().register(new HandleNullRequest());
		environment.jersey().register(new OrderResource(orderDaoForOrderResource));
		environment.jersey().register(new CategoryResource(categoryDao));
	}

}