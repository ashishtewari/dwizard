package com.mebelkart.api.util.cronTasks.Tasks;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.search.SearchHit;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mebelkart.api.util.cronTasks.classes.Manufacturer;
import com.mebelkart.api.util.cronTasks.classes.ManufacturerAddresses;
import com.mebelkart.api.util.cronTasks.classes.ManufacturerCompanyInfo;
import com.mebelkart.api.util.cronTasks.classes.ManufacturerProducts;
import com.mebelkart.api.util.cronTasks.dao.ManufacturerDao;
import com.mebelkart.api.util.factories.ElasticFactory;

import de.spinscale.dropwizard.jobs.Job;

/**
 * Created by vinitpayal on 01/04/16.
 */
//@OnApplicationStart
public class ManufacturerIndex extends Job{

	Client elasticInstance= ElasticFactory.getElasticClient();
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
				List<ManufacturerAddresses>manufacturerAddressesList = new ArrayList<ManufacturerAddresses>();
				List<ManufacturerProducts>manufacturerProductsList = new ArrayList<ManufacturerProducts>();
				List<ManufacturerCompanyInfo>manufacturerCompanyInfoList = new ArrayList<ManufacturerCompanyInfo>();
				List<Object>manufacturerOrdersList = new ArrayList<Object>();
				
				Manufacturer manufacturer = new Manufacturer();
				Integer manufacturerId = manufacturerResultSet.getInt("id_manufacturer");
				manufacturer.setManufacturerId(manufacturerId);
				manufacturer.setName(manufacturerResultSet.getString("name"));
				manufacturer.setEmail(manufacturerResultSet.getString("email"));
				manufacturer.setDateAdd(manufacturerResultSet.getDate("date_add"));
				manufacturer.setDateUpd(manufacturerResultSet.getDate("date_upd"));
				/*
				 * getting addresses of a manufacturer and adding it to a list
				 */
				ResultSet manufacturerAddressResultSet = manufacturerDao.getManufacturerAddressDetails(manufacturerId);
				ManufacturerAddresses manufacturerAddress = null;
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
				manufacturerAddress = new ManufacturerAddresses(addressId,isDefault,active,countryId,stateId,supplierId
						,alias,firstName,lastName,address1,address2,postCode,city,other,phone,mobile,dateAdded,dateUpdated); 
				manufacturerAddressesList.add(manufacturerAddress);
				}
				manufacturer.setManufacturerAddresses(manufacturerAddressesList);
				
				/*
				 * getting company info of a manufacturer and adding it to a list
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
					manufacturerCompanyInfoList.add(manufacturerCompanyInfo);
				}
				manufacturer.setManufacturerCompanyInfo(manufacturerCompanyInfoList);
				
				/*
				 * getting productId of a manufacturer and adding them to a list
				 */
				ResultSet productIdResultSet = manufacturerDao.getManufacturerProductId(manufacturerId);
				ManufacturerProducts manufacturerProducts = null;
				while(productIdResultSet.next()){
					Integer productId = productIdResultSet.getInt("id_product");
					System.out.println("productId = " + productId);
					manufacturerProducts = new ManufacturerProducts(productId);
					manufacturerProductsList.add(manufacturerProducts);
				}
				manufacturer.setManufacturerProducts(manufacturerProductsList);
				
				/*
				 * getting all order details of a manufacturer
				 */
				ResultSet orderIdResultSet = manufacturerDao.getProductOrderId(manufacturerId);
				while(orderIdResultSet.next()){
					Integer orderId = orderIdResultSet.getInt("id_order");
					System.out.println("orderId = " + orderId);
					SearchResponse response = manufacturerDao.getManufacturerOrderDetails(orderId);
					SearchHit[] searchHits = response.getHits().getHits();
					for(int i=0;i<searchHits.length;i++){
						manufacturerOrdersList.add(searchHits[i].getSource());
					}
				}
				manufacturer.setManufacturerOrders(manufacturerOrdersList);
				/*
				 * 
				 */
				 byte[] manufacturerJson = pojoToJsonMapper.writeValueAsBytes(manufacturer);
	                IndexRequest indexRequest = new IndexRequest("mk", "manufacturer", String.valueOf(manufacturerResultSet.getInt("id_manufacturer")))
	                        .source(manufacturerJson);
	                UpdateRequest updateRequest = new UpdateRequest("mk", "manufacturer",String.valueOf(manufacturerResultSet.getInt("id_manufacturer")))
	                        .doc(manufacturerJson)
	                        .upsert(indexRequest);

	                UpdateResponse updateResponse=elasticInstance.update(updateRequest).get();
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
