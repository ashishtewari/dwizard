package com.mebelkart.api.util.cronTasks.Tasks;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.concurrent.ExecutionException;

import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.Client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mebelkart.api.util.cronTasks.classes.Manufacturer;
import com.mebelkart.api.util.cronTasks.classes.ManufacturerAddresses;
import com.mebelkart.api.util.cronTasks.classes.ManufacturerCompanyInfo;
import com.mebelkart.api.util.cronTasks.classes.ManufacturerOrders;
import com.mebelkart.api.util.cronTasks.classes.ManufacturerProducts;
import com.mebelkart.api.util.cronTasks.dao.ManufacturerDao;
import com.mebelkart.api.util.factories.ElasticFactory;
import de.spinscale.dropwizard.jobs.Job;
import de.spinscale.dropwizard.jobs.annotations.OnApplicationStart;

public class ManufacturerIndex extends Job{

	Client elasticInstance = ElasticFactory.getElasticClient();
    @SuppressWarnings("unused")
	@Override
    public void doJob() {
        System.out.println("Manufacturer indexing started");
        try {
			ManufacturerDao manufacturerDao = new ManufacturerDao();
			ResultSet manufacturerResultSet = manufacturerDao.getManufacturerDetails();
			ObjectMapper pojoToJsonMapper = new ObjectMapper();
			/*
			 * Indexing all the manufacturers into elastic search
			 */
			while(manufacturerResultSet.next()){
				int totalAddresses=0,totalProducts=0,totalOrders=0;
				Manufacturer manufacturer = new Manufacturer();
				Integer manufacturerId = manufacturerResultSet.getInt("id_manufacturer");
				manufacturer.setManufacturerId(manufacturerId);
				manufacturer.setName(manufacturerResultSet.getString("name"));
				manufacturer.setEmail(manufacturerResultSet.getString("email"));
				manufacturer.setIsCommisionableVendor(manufacturerResultSet.getInt("is_commissionable_vendor"));
				manufacturer.setIsWholesaleVendor(manufacturerResultSet.getInt("is_wholesale_vendor"));
				manufacturer.setDateAdd(manufacturerResultSet.getDate("date_add"));
				manufacturer.setDateUpd(manufacturerResultSet.getDate("date_upd"));
				System.out.println("Indexing for manufacturerId "+manufacturerId+" started");
				/*
				 * getting company info of a manufacturer and setting that object to manufacturer
				 */
				ResultSet manufacturerCompanyInfoResultSet = manufacturerDao.getManufacturerCompanyInfoDetails(manufacturerId);
				ManufacturerCompanyInfo manufacturerCompanyInfo = null;
				while(manufacturerCompanyInfoResultSet.next()){
					String companyName = manufacturerCompanyInfoResultSet.getString("company_name");
					String companyDescription = manufacturerCompanyInfoResultSet.getString("company_description");
					String displayName = manufacturerCompanyInfoResultSet.getString("display_name");
					String companyPolicy = manufacturerCompanyInfoResultSet.getString("company_policy");
					boolean isActive = manufacturerCompanyInfoResultSet.getBoolean("active");
					manufacturerCompanyInfo = new ManufacturerCompanyInfo(companyName, companyDescription, displayName, companyPolicy, isActive);
					manufacturer.setManufacturerCompanyInfo(manufacturerCompanyInfo);
				}
	
				/*
				 * Indexing manufacturer personal details into manufacturer index of type personalDetails
				 */
				 byte[] manufacturerJson = pojoToJsonMapper.writeValueAsBytes(manufacturer);
	                IndexRequest manufacturerPresonalIndexRequest = new IndexRequest("manufacturer", "info", String.valueOf(manufacturerId))
	                        .source(manufacturerJson);
	                UpdateRequest manufacturerPersonalUpdateRequest = new UpdateRequest("manufacturer", "info",String.valueOf(manufacturerId))
	                        .doc(manufacturerJson)
	                        .upsert(manufacturerPresonalIndexRequest);

	                UpdateResponse manufacturerPersonalUpdateResponse=elasticInstance.update(manufacturerPersonalUpdateRequest).get();
	                System.out.println("Indexing for manufacturer info of manufacturerId "+manufacturerId+" completed");
	                
	    			
	    			/*
	    			 * getting addresses of a manufacturer and adding it to a type of manufacturer index in elastic
	    			 */
	                System.out.println("Manufacturer addresses indexing started for manufacturerId "+manufacturerId);
	    			ResultSet manufacturerAddressResultSet = manufacturerDao.getManufacturerAddressDetails(manufacturerId);
	    			ManufacturerAddresses manufacturerAddresses = null;
	    			while(manufacturerAddressResultSet.next()){	
	    				Integer addressId = manufacturerAddressResultSet.getInt("id_address");
	    				boolean isDefault = manufacturerAddressResultSet.getBoolean("is_default");
	    				boolean active = manufacturerAddressResultSet.getBoolean("active");
	    				Integer countryId = manufacturerAddressResultSet.getInt("id_country");
	    				Integer stateId = manufacturerAddressResultSet.getInt("id_state");
	    				Integer supplierId = manufacturerAddressResultSet.getInt("id_supplier");
	    				String alias = manufacturerAddressResultSet.getString("alias");
	    				String firstName = manufacturerAddressResultSet.getString("firstname");
	    				String lastName = manufacturerAddressResultSet.getString("lastname");
	    				String address1 = manufacturerAddressResultSet.getString("address1");
	    				String address2 = manufacturerAddressResultSet.getString("address2");
	    				String postCode = manufacturerAddressResultSet.getString("postcode");
	    				String city = manufacturerAddressResultSet.getString("city");
	    				String other = manufacturerAddressResultSet.getString("other");
	    				String phone = manufacturerAddressResultSet.getString("phone");
	    				String mobile = manufacturerAddressResultSet.getString("phone_mobile");
	    				Date dateAdded = manufacturerAddressResultSet.getDate("date_add");
	    				Date dateUpdated = manufacturerAddressResultSet.getDate("date_upd");
	    			manufacturerAddresses = new ManufacturerAddresses(addressId,isDefault,active,manufacturerId,countryId,stateId,supplierId
	    					,alias,firstName,lastName,address1,address2,postCode,city,other,phone,mobile,dateAdded,dateUpdated);
	    			
	    			 byte[] manufacturerAddressesJson = pojoToJsonMapper.writeValueAsBytes(manufacturerAddresses);
	                 IndexRequest manufacturerAddressesIndexRequest = new IndexRequest("manufacturer", "addresses", String.valueOf(addressId))
	                         .source(manufacturerAddressesJson);
	                 UpdateRequest manufacturerAddressesUpdateRequest = new UpdateRequest("manufacturer", "addresses",String.valueOf(addressId))
	                         .doc(manufacturerAddressesJson)
	                         .upsert(manufacturerAddressesIndexRequest);

	                 UpdateResponse manufacturerAddressesUpdateResponse=elasticInstance.update(manufacturerAddressesUpdateRequest).get();
	                 totalAddresses++;
	    			}
	    				System.out.println("total addresses for manufacturerId "+manufacturerId+" are "+totalAddresses);
	                    System.out.println("manufacturer addresses indexing completed for manufacturerId "+manufacturerId);
	                    
	                    
	        			/*
	        			 * Indexing product details into manufacturer index of type manufacturerProducts
	        			 */
	                    System.out.println("Manufacturer products indexing started for manufacturerId "+manufacturerId);
	        			ResultSet productDetailsResultSet = manufacturerDao.getManufacturerProductId(manufacturerId);
	        			ManufacturerProducts manufacturerProducts = null;
	        			while(productDetailsResultSet.next()){
	        				Integer productId = productDetailsResultSet.getInt("id_product");
	        				Integer categoryId = productDetailsResultSet.getInt("id_category_default");
	        				double price = productDetailsResultSet.getDouble("price");
	        				String productName = productDetailsResultSet.getString("name"); 
	        				manufacturerProducts = new ManufacturerProducts(productId,categoryId,price,productName,manufacturerId);
	        				
	        				 byte[] manufacturerProductsJson = pojoToJsonMapper.writeValueAsBytes(manufacturerProducts);
	        	                IndexRequest manufacturerProductsIndexRequest = new IndexRequest("manufacturer", "products", String.valueOf(productId))
	        	                        .source(manufacturerProductsJson);
	        	                UpdateRequest manufacturerProductsUpdateRequest = new UpdateRequest("manufacturer", "products",String.valueOf(productId))
	        	                        .doc(manufacturerProductsJson)
	        	                        .upsert(manufacturerProductsIndexRequest);

	        	                UpdateResponse manufacturerProductsUpdateResponse=elasticInstance.update(manufacturerProductsUpdateRequest).get();
	        	                totalProducts++;
	        			}
	        				System.out.println("total products for manufacturerId "+manufacturerId+" are "+totalProducts);
	        				System.out.println("Manufacturer products indexing completed for manufacturerId "+manufacturerId);        
	                    
	                    
	        				
        				/*
        				 * Indexing all order details of a manufacturer into manufacturer index of typer orders
        				 */
	        				System.out.println("Manufacturer orders indexing started for manufacturerId "+manufacturerId);
        				ResultSet orderDetailsResultSet = manufacturerDao.getOrderDetails(manufacturerId);
        				ManufacturerOrders manufacturerOrders = null;
        				while(orderDetailsResultSet.next()){
        					Integer orderId = orderDetailsResultSet.getInt("id_order");
        					Date dateAdd = orderDetailsResultSet.getDate("date_add");
        					manufacturerOrders = new ManufacturerOrders(orderId,manufacturerId,dateAdd);
        					
        				 byte[] manufacturerOrdersJson = pojoToJsonMapper.writeValueAsBytes(manufacturerOrders);
        	             IndexRequest manufacturerOrdersIndexRequest = new IndexRequest("manufacturer", "orders", String.valueOf(orderId))
        	                     .source(manufacturerOrdersJson);
        	             UpdateRequest manufacturerOrdersUpdateRequest = new UpdateRequest("manufacturer", "orders",String.valueOf(orderId))
        	                     .doc(manufacturerOrdersJson)
        	                     .upsert(manufacturerOrdersIndexRequest);

        	             UpdateResponse manufacturerOrdersUpdateResponse=elasticInstance.update(manufacturerOrdersUpdateRequest).get();
        	             totalOrders++;
        				}
        				System.out.println("total orders for manufacturerId "+manufacturerId+" are "+totalOrders);
        				System.out.println("Manufacturer orders indexing completed for manufacturerId "+manufacturerId);
                System.out.println("manufacturerId "+manufacturerId+" was indexed.");
			}
			
			System.out.println("Manufacturers indexing completed");
			
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		} 


    }
}