package com.mebelkart.api.util.cronTasks.Tasks;

import java.sql.ResultSet;

import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.Client;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mebelkart.api.util.cronTasks.classes.Manufacturer;
import com.mebelkart.api.util.cronTasks.classes.ManufacturerAddresses;
import com.mebelkart.api.util.cronTasks.classes.ManufacturerBankAccountInfo;
import com.mebelkart.api.util.cronTasks.classes.ManufacturerCompanyInfo;
import com.mebelkart.api.util.cronTasks.classes.ManufacturerLang;
import com.mebelkart.api.util.cronTasks.classes.ManufacturerOrders;
import com.mebelkart.api.util.cronTasks.classes.ManufacturerProducts;
import com.mebelkart.api.util.cronTasks.classes.ManufacturerProfile;
import com.mebelkart.api.util.cronTasks.dao.ManufacturerDao;
import com.mebelkart.api.util.factories.ElasticFactory;

import de.spinscale.dropwizard.jobs.Job;
import de.spinscale.dropwizard.jobs.annotations.OnApplicationStart;

@OnApplicationStart
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
				try {
					int totalAddresses=0,totalProducts=0,totalOrders=0;
					Manufacturer manufacturer = new Manufacturer();
					Integer manufacturerId = manufacturerResultSet.getInt("id_manufacturer");
					manufacturer.setManufacturerId(manufacturerId);
					manufacturer.setName(manufacturerResultSet.getString("name"));
					manufacturer.setEmail(manufacturerResultSet.getString("email"));
					manufacturer.setIsCommisionableVendor(manufacturerResultSet.getInt("is_commissionable_vendor"));
					manufacturer.setIsWholesaleVendor(manufacturerResultSet.getInt("is_wholesale_vendor"));
					manufacturer.setDateAdd(manufacturerResultSet.getString("date_add"));
					manufacturer.setDateUpd(manufacturerResultSet.getString("date_upd"));
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
						 * getting bank account info of a manufacturer and setting that object to manufacturer
						 */
					System.out.println("Indexing bankAccountInfo of manufacturerId "+manufacturerId+" started");
					ResultSet manufacturerBankAccountInfoResultSet = manufacturerDao.getManufacturerBankAccountInfoDetails(manufacturerId);
					ManufacturerBankAccountInfo manufacturerBankAccountInfo = null;
					while(manufacturerBankAccountInfoResultSet.next()){
						String accountName = manufacturerBankAccountInfoResultSet.getString("account_name");
						String bankName = manufacturerBankAccountInfoResultSet.getString("bank_name");
						Integer accountType = manufacturerBankAccountInfoResultSet.getInt("account_type");
						String accountNumber = manufacturerBankAccountInfoResultSet.getString("account_number");
						String branchCity = manufacturerBankAccountInfoResultSet.getString("branch_city");
						String branchName = manufacturerBankAccountInfoResultSet.getString("branch_name");
						String ifscCode = manufacturerBankAccountInfoResultSet.getString("ifsc_code");
						boolean active = manufacturerBankAccountInfoResultSet.getBoolean("active");
						manufacturerBankAccountInfo = new ManufacturerBankAccountInfo(manufacturerId, accountName, bankName, accountType, accountNumber, branchCity, ifscCode, active);
						manufacturer.setManufacturerBankAccountInfo(manufacturerBankAccountInfo);
					}
						
						/*
						 * getting lang info of a manufacturer and setting that object to manufacturer
						 */
					System.out.println("Indexing lang of manufacturerId "+manufacturerId+" started");
					ResultSet manufacturerLangResultSet = manufacturerDao.getManufacturerLangDetails(manufacturerId);
					ManufacturerLang manufacturerLang = null;
					while(manufacturerLangResultSet.next()){
						Integer langId = manufacturerLangResultSet.getInt("id_lang");
						String description = manufacturerLangResultSet.getString("description");
						String shortDescription = manufacturerLangResultSet.getString("short_description");
						String metaTitle = manufacturerLangResultSet.getString("meta_title");
						String metaKeyWords = manufacturerLangResultSet.getString("meta_keywords");
						String metaDescription = manufacturerLangResultSet.getString("meta_description");
						manufacturerLang = new ManufacturerLang(manufacturerId, langId, description, shortDescription, metaTitle, metaKeyWords, metaDescription);
						manufacturer.setManufacturerLang(manufacturerLang);
					}
					
						/*
						 * getting profile info of a manufacturer and setting that object to manufacturer
						 */
					System.out.println("Indexing profile of manufacturerId "+manufacturerId+" started");
					ResultSet manufacturerProfileResultSet = manufacturerDao.getManufacturerProfileDetails(manufacturerId);
					ManufacturerProfile manufacturerProfile = null;
					while(manufacturerProfileResultSet.next()){
						Integer manufacturerProfileId = manufacturerProfileResultSet.getInt("id_manufacturer_profile");
						Integer employeeId = manufacturerProfileResultSet.getInt("id_employee");
						manufacturerProfile = new ManufacturerProfile(manufacturerProfileId, employeeId, manufacturerId);
						manufacturer.setManufacturerProfile(manufacturerProfile);
					}
		
						/*
						 * Indexing manufacturer personal details into manufacturer index of type personalDetails
						 */
					 byte[] manufacturerJson = pojoToJsonMapper.writeValueAsBytes(manufacturer);
		                IndexRequest manufacturerPresonalIndexRequest = new IndexRequest("manufacturer", "manufacturerInfo", String.valueOf(manufacturerId))
		                        .source(manufacturerJson);
		                UpdateRequest manufacturerPersonalUpdateRequest = new UpdateRequest("manufacturer", "manufacturerInfo",String.valueOf(manufacturerId))
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
		    				String dateAdded = manufacturerAddressResultSet.getString("date_add");
		    				String dateUpdated = manufacturerAddressResultSet.getString("date_upd");
		    			manufacturerAddresses = new ManufacturerAddresses(addressId,isDefault,active,manufacturerId,countryId,stateId,supplierId
		    					,alias,firstName,lastName,address1,address2,postCode,city,other,phone,mobile,dateAdded,dateUpdated);
		    			
		    			 byte[] manufacturerAddressesJson = pojoToJsonMapper.writeValueAsBytes(manufacturerAddresses);
		                 IndexRequest manufacturerAddressesIndexRequest = new IndexRequest("manufacturer", "manufacturerAddresses", String.valueOf(addressId))
		                         .source(manufacturerAddressesJson);
		                 UpdateRequest manufacturerAddressesUpdateRequest = new UpdateRequest("manufacturer", "manufacturerAddresses",String.valueOf(addressId))
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
		        			ResultSet productDetailsResultSet = manufacturerDao.getManufacturerProductDetails(manufacturerId);
		        			ManufacturerProducts manufacturerProducts = null;
		        			while(productDetailsResultSet.next()){
		        				Integer productId = productDetailsResultSet.getInt("id_product");
		        				Integer categoryId = productDetailsResultSet.getInt("id_category_default");
		        				double price = productDetailsResultSet.getDouble("price");
		        				String productName = productDetailsResultSet.getString("name"); 
		        				manufacturerProducts = new ManufacturerProducts(productId,categoryId,price,productName,manufacturerId);
		        				
		        				 byte[] manufacturerProductsJson = pojoToJsonMapper.writeValueAsBytes(manufacturerProducts);
		        	                IndexRequest manufacturerProductsIndexRequest = new IndexRequest("manufacturer", "manufacturerProducts", String.valueOf(productId))
		        	                        .source(manufacturerProductsJson);
		        	                UpdateRequest manufacturerProductsUpdateRequest = new UpdateRequest("manufacturer", "manufacturerProducts",String.valueOf(productId))
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
	        					String dateAdd = orderDetailsResultSet.getString("date_add");
	        					manufacturerOrders = new ManufacturerOrders(orderId,manufacturerId,dateAdd);
	        					
	        				 byte[] manufacturerOrdersJson = pojoToJsonMapper.writeValueAsBytes(manufacturerOrders);
	        	             IndexRequest manufacturerOrdersIndexRequest = new IndexRequest("manufacturer", "manufacturerOrders", String.valueOf(orderId))
	        	                     .source(manufacturerOrdersJson);
	        	             UpdateRequest manufacturerOrdersUpdateRequest = new UpdateRequest("manufacturer", "manufacturerOrders",String.valueOf(orderId))
	        	                     .doc(manufacturerOrdersJson)
	        	                     .upsert(manufacturerOrdersIndexRequest);
	
	        	             UpdateResponse manufacturerOrdersUpdateResponse=elasticInstance.update(manufacturerOrdersUpdateRequest).get();
	        	             totalOrders++;
	        				}
	        				System.out.println("total orders for manufacturerId "+manufacturerId+" are "+totalOrders);
	        				System.out.println("Manufacturer orders indexing completed for manufacturerId "+manufacturerId);
	        				System.out.println("manufacturerId "+manufacturerId+" was indexed.");
				}catch(Exception e){
					e.printStackTrace();
					System.out.println("Exception occured while indexing Manufacturer");
				}
			}
			elasticInstance.admin().indices().prepareAliases()
            .addAlias("manufacturer", "mkmanufacturer")
            //.removeAlias("my_old_index", "my_alias")
            .execute().actionGet();
			
			System.out.println("Manufacturers indexing completed");
			
		} catch (Exception e) {
			e.printStackTrace();
		}

    }
}