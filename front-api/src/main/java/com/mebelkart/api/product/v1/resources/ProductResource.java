package com.mebelkart.api.product.v1.resources;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.mebelkart.api.product.v1.api.CategoryFeatured;
import com.mebelkart.api.product.v1.core.TopProductsWrapper;
import com.mebelkart.api.product.v1.dao.ProductDao;
import com.mebelkart.api.util.classes.InvalidInputReplyClass;

import com.mebelkart.api.util.classes.PaginationReply;

import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mebelkart.api.product.v1.api.ProductDetailsResponse;
import com.mebelkart.api.util.classes.Reply;
import com.mebelkart.api.util.factories.ElasticFactory;
import com.mebelkart.api.util.helpers.Authentication;
import com.mebelkart.api.util.helpers.Helper;

/**
 * @author Tinku
 *
 */
@Path("/v1.0")
@Produces({MediaType.APPLICATION_JSON})
public class ProductResource {

	ProductDao productDao;

	public ProductResource(ProductDao productDao) {
		this.productDao = productDao;
	}
	/**
	 * Getting products elastic client connection
	 */
	Client client = ElasticFactory.getProductsElasticClient();
	/**
	 * Get actual class name to be printed on log files
	 */
	static Logger log = LoggerFactory.getLogger(ProductResource.class);
	/**
	 * InvalidInputReplyClass class
	 */
	InvalidInputReplyClass invalidRequestReply = null;	
	/**
	 * Getting client to authenticate
	 */
	Authentication authenticate = new Authentication();

	/**
	 * Helper class from utils
	 */
	Helper helper = new Helper();
	
	/**
	 * This will return json of all live products in pagination format
	 * @return will return json of all live products in pagination format
	 */
	@SuppressWarnings("unchecked")
	@GET
	@Path("/products")
	public Object getAllProducts(@HeaderParam("accessParam") String accessParam){		
		try{
			if(isValidJson(accessParam)){
				if(isHavingValidGetAllProductDetailKeys(accessParam)){
					JSONObject jsonData = helper.jsonParser(accessParam);
					String userName = (String) jsonData.get("userName");
					String accessToken = (String) jsonData.get("accessToken");
					try {
						authenticate.validate(userName,accessToken, "products", "get", "getAllProducts");
					} catch (Exception e) {
						log.info("Unautherized user "+userName+" tried to access getProductDetail function");
						invalidRequestReply = new InvalidInputReplyClass(Response.Status.UNAUTHORIZED.getStatusCode(), Response.Status.UNAUTHORIZED.getReasonPhrase(), e.getMessage());
						return invalidRequestReply;
					}
					int page  = Integer.parseInt((String) jsonData.get("page"));
					int perPage = Integer.parseInt((String) jsonData.get("limit"));
					int start = ((page - 1) * perPage) + 1;
					int end = page * perPage;
					String[] includes = new String[]
							{"info.id_product","info.name","info.id_category_default","info.name_category_default"
							,"categoryVars.price_without_reduction","categoryVars.price_tax_exc"
							,"all_locations","info.additional_shipping_cost","info.available_now"};
					List<Map<String, String>> productsList = new ArrayList<Map<String,String>>();
					SearchResponse response = client.prepareSearch("mkproducts")
							.setTypes("product")
					        .setFetchSource(includes, null)
					        .setFrom(start).setSize(perPage).setExplain(true)
					        .execute().actionGet();
					SearchHit[] searchHits = response.getHits().getHits();
					long totalProducts = response.getHits().getTotalHits();
					long totalPages = totalProducts/perPage;
					String currentShowing = start+"-"+end;
					for(int i=0;i<searchHits.length;i++){
						Map<String,String> productsDetails = new HashMap<String,String>();
						Map<String,Object> info = (Map<String, Object>) searchHits[i].getSource().get("info");
						Map<String,Object> categoryVars = (Map<String, Object>) searchHits[i].getSource().get("categoryVars");
						productsDetails.put("productId",(String)info.get("id_product"));
						productsDetails.put("productName",(String)info.get("name"));
						productsDetails.put("categoryId",(String)info.get("id_category_default"));
						productsDetails.put("categoryName",(String)info.get("name_category_default"));
						productsDetails.put("mktPrice",(Integer)categoryVars.get("price_without_reduction")+"");
						productsDetails.put("ourPrice",(Integer)categoryVars.get("price_tax_exc")+"");
						productsDetails.put("emiPrice","https://www.mebelkart.com/getEMIForProduct/"+(String)info.get("id_product")+"?mode=json");
						productsDetails.put("availLocation",(String)searchHits[i].getSource().get("all_locations"));
						productsDetails.put("shippingCost",(String)info.get("additional_shipping_cost"));
						productsDetails.put("shippingAvailable",(String)info.get("available_now"));
						productsList.add(productsDetails);
					}
					System.out.println("Number of hits "+searchHits.length);
					return new PaginationReply(Response.Status.OK.getStatusCode(), Response.Status.OK.getReasonPhrase(),totalProducts,totalPages,page,currentShowing,productsList);
				}else{
					log.info("Invalid header keys provided to access getProductDetail function");
					invalidRequestReply = new InvalidInputReplyClass(Response.Status.BAD_REQUEST.getStatusCode(), Response.Status.BAD_REQUEST.getReasonPhrase(), "Invalid keys provided");
					return invalidRequestReply;
				}
			}else{
				log.info("Invalid header json provided to access getAllProducts function");
				invalidRequestReply = new InvalidInputReplyClass(Response.Status.BAD_REQUEST.getStatusCode(), Response.Status.BAD_REQUEST.getReasonPhrase(), "Header data is invalid json");
				return invalidRequestReply;
			}
		}catch(NumberFormatException e){
			e.printStackTrace();
			log.info("Invalid pageNumber/limit given in getAllProducts functions");
			invalidRequestReply = new InvalidInputReplyClass(Response.Status.BAD_REQUEST.getStatusCode(), Response.Status.BAD_REQUEST.getReasonPhrase(), "Invalid pageNumber/limit");
			return invalidRequestReply;
		}
		catch(Exception e){
			e.printStackTrace();
			log.warn("Internal error occured in getAllProducts function");
			invalidRequestReply = new InvalidInputReplyClass(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), Response.Status.INTERNAL_SERVER_ERROR.getReasonPhrase(), "Unknown exception caused");
			return invalidRequestReply;
		}
	}
	
	private boolean isHavingValidGetAllProductDetailKeys(String accessParam) {
		JSONObject jsonData = helper.jsonParser(accessParam);
		if(jsonData.containsKey("userName") && jsonData.containsKey("accessToken") && jsonData.containsKey("page") && jsonData.containsKey("limit"))
			return true;
		else
			return false;
	}

	/**
	 * This will return json of all live products in pagination format corresponding to their category Id
	 * @param accessParam
	 * @return
	 */
	@SuppressWarnings({ "unchecked" })
	@GET
	@Path("/products/category/{categoryId}")
	public Object getProductsListByCategory(@HeaderParam("accessParam") String accessParam,@PathParam("categoryId") String categoryId){		
		try{
			if(isValidJson(accessParam)){
				if(isHavingValidGetProductDetailByCategoryKeys(accessParam)){
					JSONObject jsonData = helper.jsonParser(accessParam);
					String userName = (String) jsonData.get("userName");
					String accessToken = (String) jsonData.get("accessToken");
					try {
						authenticate.validate(userName,accessToken, "products", "get", "getProductsListByCategory");
					} catch (Exception e) {
						log.info("Unautherized user "+userName+" tried to access getProductsListByCategory function");
						invalidRequestReply = new InvalidInputReplyClass(Response.Status.UNAUTHORIZED.getStatusCode(), Response.Status.UNAUTHORIZED.getReasonPhrase(), e.getMessage());
						return invalidRequestReply;
					}
					String stock = "";
					int manufacturerId = 0;
					int locationId = 0;
					BoolQueryBuilder categoryQuery = QueryBuilders.boolQuery();
					try{
						int category = Integer.parseInt(categoryId);
						categoryQuery.must(QueryBuilders.matchQuery("all_category_ids",category));
						if(jsonData.containsKey("stock")){
							stock = (String) jsonData.get("stock");
							if(stock.equalsIgnoreCase("oos")||stock.equalsIgnoreCase("outofstock")||stock.equalsIgnoreCase("out of stock"))
								categoryQuery.must(QueryBuilders.rangeQuery("info.quantity").lte(0));
							else if(stock.equalsIgnoreCase("instock")||stock.equalsIgnoreCase("is")||stock.equalsIgnoreCase("in stock"))
								categoryQuery.must(QueryBuilders.rangeQuery("info.quantity").gt(0));
						}
						if(jsonData.containsKey("sellerId")){
							manufacturerId = Integer.parseInt((String)jsonData.get("sellerId"));
							categoryQuery.must(QueryBuilders.matchQuery("info.id_manufacturer",manufacturerId));
						}
						if(jsonData.containsKey("cityId")){
							locationId = Integer.parseInt((String)jsonData.get("cityId"));
							categoryQuery.must(QueryBuilders.matchQuery("all_location_ids",locationId));
						}
					}catch(Exception e){
						invalidRequestReply = new InvalidInputReplyClass(Response.Status.BAD_REQUEST.getStatusCode(), Response.Status.BAD_REQUEST.getReasonPhrase(), "Give valid values corresponding to keys (categoryId/stock/sellerId/cityId)");
						return invalidRequestReply;
					}
					int page  = Integer.parseInt((String) jsonData.get("page"));
					int perPage = Integer.parseInt((String) jsonData.get("limit"));
					int start = ((page - 1) * perPage) + 1;
					int end = page * perPage;
					String[] includes = new String[]
							{"info.id_product","info.name","info.id_category_default","info.name_category_default"
							,"categoryVars.price_without_reduction","categoryVars.price_tax_exc"
							,"all_locations","info.additional_shipping_cost","info.available_now"};
					List<Map<String, String>> productsList = new ArrayList<Map<String,String>>();
					SearchResponse response = client.prepareSearch("mkproducts")
							.setTypes("product")
					        .setFetchSource(includes, null)
					        .setQuery(categoryQuery)
					        .setFrom(start).setSize(perPage).setExplain(true)
					        .execute().actionGet();
					SearchHit[] searchHits = response.getHits().getHits();
					long totalProducts = response.getHits().getTotalHits();
					long totalPages = totalProducts/perPage;
					String currentShowing = start+"-"+end;
					for(int i=0;i<searchHits.length;i++){
						Map<String,String> productsDetails = new HashMap<String,String>();
						Map<String,Object> info = (Map<String, Object>) searchHits[i].getSource().get("info");
						Map<String,Object> categoryVars = (Map<String, Object>) searchHits[i].getSource().get("categoryVars");
						productsDetails.put("productId",(String)info.get("id_product"));
						productsDetails.put("productName",(String)info.get("name"));
						productsDetails.put("categoryId",(String)info.get("id_category_default"));
						productsDetails.put("categoryName",(String)info.get("name_category_default"));
						productsDetails.put("mktPrice",(Integer)categoryVars.get("price_without_reduction")+"");
						productsDetails.put("ourPrice",(Integer)categoryVars.get("price_tax_exc")+"");
						productsDetails.put("emiPrice","https://www.mebelkart.com/getEMIForProduct/"+(String)info.get("id_product")+"?mode=json");
						productsDetails.put("availLocation",(String)searchHits[i].getSource().get("all_locations"));
						productsDetails.put("shippingCost",(String)info.get("additional_shipping_cost"));
						productsDetails.put("shippingAvailable",(String)info.get("available_now"));
						productsList.add(productsDetails);
					}
					System.out.println("Number of hits "+searchHits.length);
					return new PaginationReply(Response.Status.OK.getStatusCode(), Response.Status.OK.getReasonPhrase(),totalProducts,totalPages,page,currentShowing,productsList);
					//return new ProductsPaginationReply(Response.Status.OK.getStatusCode(), Response.Status.OK.getReasonPhrase(),totalProducts,totalPages,page,currentShowing,productsList);
				}else{
					log.info("Invalid header keys provided to access getProductsListByCategory function");
					invalidRequestReply = new InvalidInputReplyClass(Response.Status.BAD_REQUEST.getStatusCode(), Response.Status.BAD_REQUEST.getReasonPhrase(), "Invalid keys provided");
					return invalidRequestReply;
				}
			}else{
				log.info("Invalid header json provided to access getProductsListByCategory function");
				invalidRequestReply = new InvalidInputReplyClass(Response.Status.BAD_REQUEST.getStatusCode(), Response.Status.BAD_REQUEST.getReasonPhrase(), "Header data is invalid json");
				return invalidRequestReply;
			}
		}catch(NumberFormatException e){
			e.printStackTrace();
			log.info("Invalid pageNumber/limit given in getProductsListByCategory functions");
			invalidRequestReply = new InvalidInputReplyClass(Response.Status.BAD_REQUEST.getStatusCode(), Response.Status.BAD_REQUEST.getReasonPhrase(), "Invalid pageNumber/limit");
			return invalidRequestReply;
		}
		catch(Exception e){
			e.printStackTrace();
			log.warn("Internal error occured in getProductsListByCategory function");
			invalidRequestReply = new InvalidInputReplyClass(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), Response.Status.INTERNAL_SERVER_ERROR.getReasonPhrase(), "Unknown exception caused");
			return invalidRequestReply;
		}
	}
	
	private boolean isHavingValidGetProductDetailByCategoryKeys(String accessParam) {
		JSONObject jsonData = helper.jsonParser(accessParam);
		if(jsonData.containsKey("userName") && jsonData.containsKey("accessToken") && jsonData.containsKey("page") && jsonData.containsKey("limit"))
			return true;
		else
			return false;
	}

	/**
	 * This function returns the product details of single product based on their id 
	 * @param id
	 * @return will return all details of single product
	 */
	@SuppressWarnings({ "unchecked", "unused" })
	@GET
	@Path("/product/{productId}")
	public Object getProductDetail(@HeaderParam("accessParam") String accessParam,@PathParam("productId") String id){
		try{
			if(isValidJson(accessParam)){
				if(ishavingValidGetProductDetailKeys(accessParam)){
					int productId = Integer.parseInt(id);
					JSONObject jsonData = helper.jsonParser(accessParam);
					String userName = (String) jsonData.get("userName");
					String accessToken = (String) jsonData.get("accessToken");
					try {
						authenticate.validate(userName,accessToken, "products", "get", "getProductDetail");
					} catch (Exception e) {
						log.info("Unautherized user "+userName+" tried to access getProductDetail function");
						invalidRequestReply = new InvalidInputReplyClass(Response.Status.UNAUTHORIZED.getStatusCode(), Response.Status.UNAUTHORIZED.getReasonPhrase(), e.getMessage());
						return invalidRequestReply;
					}
					GetResponse response = client.prepareGet("mkproducts", "product", id).get();
					ProductDetailsResponse prodDetails = new ProductDetailsResponse();
					Map<String,Object> prodFilteredDetails = new HashMap<String,Object>();
					Map<String,Object> source = response.getSource();
					Map<String,Object> info = (Map<String, Object>) source.get("info");
					Map<String,Object> categoryVars = (Map<String, Object>) source.get("categoryVars");
					if(jsonData.containsKey("filters")){
						int i;
						JSONArray required = (JSONArray) jsonData.get("filters");
						for(i = 0 ; i < required.size(); i++){
							if(((String)required.get(i)).equalsIgnoreCase("price")){
								prodFilteredDetails.put("mktPrice",(Integer)categoryVars.get("price_without_reduction")+"");
								prodFilteredDetails.put("ourPrice",(Integer)categoryVars.get("price_tax_exc")+"");
								prodFilteredDetails.put("emiPrice","https://www.mebelkart.com/getEMIForProduct/"+(String)categoryVars.get("id_product")+"?mode=json");
							} else if(((String)required.get(i)).equalsIgnoreCase("desc") || ((String)required.get(i)).equalsIgnoreCase("description")){
								prodFilteredDetails.put("productName",(String)categoryVars.get("name"));
								prodFilteredDetails.put("productDesc",(String)categoryVars.get("description"));
							} else if(((String)required.get(i)).equalsIgnoreCase("attr") || ((String)required.get(i)).equalsIgnoreCase("attributes")){
								prodFilteredDetails.put("attributes",(Object)source.get("attributes"));
								prodFilteredDetails.put("attributeGroups",(Object) source.get("attribute_groups"));
							} else if(((String)required.get(i)).equalsIgnoreCase("feature") ){
								prodFilteredDetails.put("features",getProductFeatures(source));
							}								
						}
						// here 4 is 4 filters which we have assigned
						// so if the user applied all the 4 filters then we are going to show all the product details
						if(required.size() != 4 && required.size() != 0){
							return new Reply(Response.Status.OK.getStatusCode(), Response.Status.OK.getReasonPhrase(), prodFilteredDetails);
						}
					}
					prodDetails = new ProductDetailsResponse();
					prodDetails.setProductId((String)info.get("id_product"));
					prodDetails.setCategoryId((String)info.get("id_category_default"));
					prodDetails.setCategoryName((String)info.get("name_category_default"));
					prodDetails.setProductName((String)info.get("name"));
					prodDetails.setProductDesc((String)info.get("description"));
					prodDetails.setAvailLocation((String) source.get("all_locations"));
					prodDetails.setBrandId(null);
					prodDetails.setBrandName(null);
					prodDetails.setTotalViews((String)source.get("views_count"));
					prodDetails.setShippingCost((String)info.get("additional_shipping_cost"));
					prodDetails.setShippingAvailable((String)info.get("available_now"));
					prodDetails.setGallery(getGallery(source));
					prodDetails.setOfferText(null);
					prodDetails.setMktPrice((Integer)categoryVars.get("price_without_reduction"));
					prodDetails.setOurPrice((Integer)categoryVars.get("price_tax_exc"));
					prodDetails.setEmiPrice("https://www.mebelkart.com/getEMIForProduct/"+(String)info.get("id_product")+"?mode=json");
					prodDetails.setRating((Integer) source.get("product_rating"));
					prodDetails.setAttributes((String) source.get("attributes"));
					prodDetails.setProductFeatures(getProductFeatures(source));
					prodDetails.setIsSoldOut(0);
					prodDetails.setAttributeGroups((Object) source.get("attribute_groups"));
					prodDetails.setReviews(null);
					prodDetails.setTotalReviews(0);
					prodDetails.setTotalReviewsCount(0);
					return new Reply(Response.Status.OK.getStatusCode(), Response.Status.OK.getReasonPhrase(), prodDetails);
				}else{
					log.info("Invalid header keys provided to access getProductDetail function");
					invalidRequestReply = new InvalidInputReplyClass(Response.Status.BAD_REQUEST.getStatusCode(), Response.Status.BAD_REQUEST.getReasonPhrase(), "Invalid keys provided");
					return invalidRequestReply;
				}
			}else{
				log.info("Invalid header json provided to access getProductDetail function");
				invalidRequestReply = new InvalidInputReplyClass(Response.Status.BAD_REQUEST.getStatusCode(), Response.Status.BAD_REQUEST.getReasonPhrase(), "Header data is invalid json");
				return invalidRequestReply;
			}
		}catch(NumberFormatException e){
			e.printStackTrace();
			log.info("Invalid product id given in getProductDetail functions");
			invalidRequestReply = new InvalidInputReplyClass(Response.Status.BAD_REQUEST.getStatusCode(), Response.Status.BAD_REQUEST.getReasonPhrase(), "Invalid product id provided");
			return invalidRequestReply;
		}
		catch(Exception e){
			e.printStackTrace();
			log.warn("Internal error occured in getProductDetail function");
			invalidRequestReply = new InvalidInputReplyClass(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), Response.Status.INTERNAL_SERVER_ERROR.getReasonPhrase(), "Unknown exception caused");
			return invalidRequestReply;
		}
	}

	private boolean isValidJson(String accessParam) {
		if(helper.isValidJson(accessParam)){
			return true;
		}else
			return false;
	}

	private boolean ishavingValidGetProductDetailKeys(String accessParam) {
		JSONObject jsonData = helper.jsonParser(accessParam);
		if(jsonData.containsKey("userName") && jsonData.containsKey("accessToken")){
			return true;
		}else
			return false;
	}

	@SuppressWarnings("unchecked")
	private Object getProductFeatures(Map<String, Object> source) {
		List<Map<String,Object>> featuresInfo = (List<Map<String, Object>>) source.get("features");
		Map<Object,Object> features = new HashMap<Object,Object>();
		for(int i = 0; i < featuresInfo.size(); i++){
			features.put(featuresInfo.get(i).get("name").toString(), featuresInfo.get(i).get("value").toString());
		}
		return new JSONObject(features);
	}

	@SuppressWarnings("unchecked")
	private Object[] getGallery(Map<String, Object> source) {
		List<Map<String,Object>> imagesInfo = (List<Map<String, Object>>) source.get("images");
		List<JSONObject> gallery = new ArrayList<JSONObject>();
		String imageName = (String)((Map<String, Object>)source.get("info")).get("link_rewrite");
		for(int i = 0; i < imagesInfo.size(); i++){
			Map<Object,Object> images = new HashMap<Object,Object>();
			String imageId = imagesInfo.get(i).get("id_image").toString();
			images.put("homeImage", "https://cdn1.mebelkart.com/"+imageId+"-home/"+imageName+".jpg");
			images.put("largeImage", "https://cdn1.mebelkart.com/"+imageId+"-large/"+imageName+".jpg");
			images.put("largerImage", "https://cdn1.mebelkart.com/"+imageId+"-larger/"+imageName+".jpg");
			gallery.add(new JSONObject(images));
		}
		return gallery.toArray();
	}

	/**
	 * This function returns the price details of the products based on their id 
	 * @param id
	 * @return
	 */
	@SuppressWarnings({ "unchecked", "unused" })
	@GET
	@Path("/product/{productId}/price")
	public Object getSellingPrice(@HeaderParam("accessParam") String accessParam,@PathParam("productId") String id){
		try{
			if(isValidJson(accessParam)){
				if(ishavingValidGetProductDetailKeys(accessParam)){
					int productId = Integer.parseInt(id);
					JSONObject jsonData = helper.jsonParser(accessParam);
					String userName = (String) jsonData.get("userName");
					String accessToken = (String) jsonData.get("accessToken");
					try {
						authenticate.validate(userName,accessToken, "products", "get", "getSellingPrice");
					} catch (Exception e) {
						log.info("Unautherized user "+userName+" tried to access getSellingPrice function");
						invalidRequestReply = new InvalidInputReplyClass(Response.Status.UNAUTHORIZED.getStatusCode(), Response.Status.UNAUTHORIZED.getReasonPhrase(), e.getMessage());
						return invalidRequestReply;
					}
					GetResponse response = client.prepareGet("mkproducts", "product", id).get();
					Map<String,String> productsDetails = new HashMap<String,String>();
					Map<String,Object> source = response.getSource();
					Map<String,Object> categoryVars = (Map<String, Object>) source.get("categoryVars");
					productsDetails.put("mktPrice",(Integer)categoryVars.get("price_without_reduction")+"");
					productsDetails.put("ourPrice",(Integer)categoryVars.get("price_tax_exc")+"");
					productsDetails.put("emiPrice","https://www.mebelkart.com/getEMIForProduct/"+(String)categoryVars.get("id_product")+"?mode=json");
					return new Reply(Response.Status.OK.getStatusCode(), Response.Status.OK.getReasonPhrase(), productsDetails);
				}else{
					log.info("Invalid header keys provided to access getSellingPrice function");
					invalidRequestReply = new InvalidInputReplyClass(Response.Status.BAD_REQUEST.getStatusCode(), Response.Status.BAD_REQUEST.getReasonPhrase(), "Invalid keys provided");
					return invalidRequestReply;
				}
			}else{
				log.info("Invalid header json provided to access getSellingPrice function");
				invalidRequestReply = new InvalidInputReplyClass(Response.Status.BAD_REQUEST.getStatusCode(), Response.Status.BAD_REQUEST.getReasonPhrase(), "Header data is invalid json");
				return invalidRequestReply;
			}
		}catch(NumberFormatException e){
			e.printStackTrace();
			log.info("Invalid product id given in getSellingPrice functions");
			invalidRequestReply = new InvalidInputReplyClass(Response.Status.BAD_REQUEST.getStatusCode(), Response.Status.BAD_REQUEST.getReasonPhrase(), "Invalid product id provided");
			return invalidRequestReply;
		}
		catch(Exception e){
			e.printStackTrace();
			log.warn("Internal error occured in getSellingPrice function");
			invalidRequestReply = new InvalidInputReplyClass(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), Response.Status.INTERNAL_SERVER_ERROR.getReasonPhrase(), "Unknown exception caused");
			return invalidRequestReply;
		}
	}
	
	/**
	 * This function returns the description of the products based on their id 
	 * @param id
	 * @return
	 */
	@SuppressWarnings({ "unchecked", "unused" })
	@GET
	@Path("/product/{productId}/description")
	public Object getProdDesc(@HeaderParam("accessParam") String accessParam,@PathParam("productId") String id){
		try{
			if(isValidJson(accessParam)){
				if(ishavingValidGetProductDetailKeys(accessParam)){
					int productId = Integer.parseInt(id);
					JSONObject jsonData = helper.jsonParser(accessParam);
					String userName = (String) jsonData.get("userName");
					String accessToken = (String) jsonData.get("accessToken");
					try {
						authenticate.validate(userName,accessToken, "products", "get", "getProdDesc");
					} catch (Exception e) {
						log.info("Unautherized user "+userName+" tried to access getProdDesc function");
						invalidRequestReply = new InvalidInputReplyClass(Response.Status.UNAUTHORIZED.getStatusCode(), Response.Status.UNAUTHORIZED.getReasonPhrase(), e.getMessage());
						return invalidRequestReply;
					}
					GetResponse response = client.prepareGet("mkproducts", "product", id).get();
					Map<String,String> productsDetails = new HashMap<String,String>();
					Map<String,Object> source = response.getSource();
					Map<String,Object> categoryVars = (Map<String, Object>) source.get("categoryVars");
					productsDetails.put("productName",(String)categoryVars.get("name"));
					productsDetails.put("productDesc",(String)categoryVars.get("description"));
					return new Reply(Response.Status.OK.getStatusCode(), Response.Status.OK.getReasonPhrase(), productsDetails);
				}else{
					log.info("Invalid header keys provided to access getProdDesc function");
					invalidRequestReply = new InvalidInputReplyClass(Response.Status.BAD_REQUEST.getStatusCode(), Response.Status.BAD_REQUEST.getReasonPhrase(), "Invalid keys provided");
					return invalidRequestReply;
				}
			}else{
				log.info("Invalid header json provided to access getProdDesc function");
				invalidRequestReply = new InvalidInputReplyClass(Response.Status.BAD_REQUEST.getStatusCode(), Response.Status.BAD_REQUEST.getReasonPhrase(), "Header data is invalid json");
				return invalidRequestReply;
			}
		}catch(NumberFormatException e){
			log.info("Invalid product id given in getProdDesc functions");
			invalidRequestReply = new InvalidInputReplyClass(Response.Status.BAD_REQUEST.getStatusCode(), Response.Status.BAD_REQUEST.getReasonPhrase(), "Invalid product id provided");
			return invalidRequestReply;
		}
		catch(Exception e){
			e.printStackTrace();
			log.warn("Internal error occured in getProdDesc function");
			invalidRequestReply = new InvalidInputReplyClass(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), Response.Status.INTERNAL_SERVER_ERROR.getReasonPhrase(), "Unknown exception caused");
			return invalidRequestReply;
		}
	}
	
	/**
	 * This function returns the attributes of the products based on their id 
	 * @param id
	 * @return
	 */
	@SuppressWarnings({ "unused" })
	@GET
	@Path("/product/{productId}/attributes")
	public Object getProdAttr(@HeaderParam("accessParam") String accessParam,@PathParam("productId") String id){
		try{
			if(isValidJson(accessParam)){
				if(ishavingValidGetProductDetailKeys(accessParam)){
					int productId = Integer.parseInt(id);
					JSONObject jsonData = helper.jsonParser(accessParam);
					String userName = (String) jsonData.get("userName");
					String accessToken = (String) jsonData.get("accessToken");
					try {
						authenticate.validate(userName,accessToken, "products", "get", "getProdAttr");
					} catch (Exception e) {
						log.info("Unautherized user "+userName+" tried to access getProdAttr function");
						invalidRequestReply = new InvalidInputReplyClass(Response.Status.UNAUTHORIZED.getStatusCode(), Response.Status.UNAUTHORIZED.getReasonPhrase(), e.getMessage());
						return invalidRequestReply;
					}
					GetResponse response = client.prepareGet("mkproducts", "product", id).get();
					Map<String,Object> productsDetails = new HashMap<String,Object>();
					Map<String,Object> source = response.getSource();
					productsDetails.put("attributes",(Object)source.get("attributes"));
					productsDetails.put("attributeGroups",(Object) source.get("attribute_groups"));
					return new Reply(Response.Status.OK.getStatusCode(), Response.Status.OK.getReasonPhrase(), productsDetails);
				}else{
					log.info("Invalid header keys provided to access getProdAttr function");
					invalidRequestReply = new InvalidInputReplyClass(Response.Status.BAD_REQUEST.getStatusCode(), Response.Status.BAD_REQUEST.getReasonPhrase(), "Invalid keys provided");
					return invalidRequestReply;
				}
			}else{
				log.info("Invalid header json provided to access getProdAttr function");
				invalidRequestReply = new InvalidInputReplyClass(Response.Status.BAD_REQUEST.getStatusCode(), Response.Status.BAD_REQUEST.getReasonPhrase(), "Header data is invalid json");
				return invalidRequestReply;
			}
		}catch(NumberFormatException e){
			log.info("Invalid product id given in getProdAttr functions");
			invalidRequestReply = new InvalidInputReplyClass(Response.Status.BAD_REQUEST.getStatusCode(), Response.Status.BAD_REQUEST.getReasonPhrase(), "Invalid product id provided");
			return invalidRequestReply;
		}
		catch(Exception e){
			e.printStackTrace();
			log.warn("Internal error occured in getProdAttr function");
			invalidRequestReply = new InvalidInputReplyClass(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), Response.Status.INTERNAL_SERVER_ERROR.getReasonPhrase(), "Unknown exception caused");
			return invalidRequestReply;
		}
	}
	
	/**
	 * This function returns the features of the products based on their id 
	 * @param id
	 * @return
	 */
	@SuppressWarnings("unused")
	@GET
	@Path("/product/{productId}/feature")
	public Object getProdFeature(@HeaderParam("accessParam") String accessParam,@PathParam("productId") String id){
		try{
			if(isValidJson(accessParam)){
				if(ishavingValidGetProductDetailKeys(accessParam)){
					int productId = Integer.parseInt(id);
					JSONObject jsonData = helper.jsonParser(accessParam);
					String userName = (String) jsonData.get("userName");
					String accessToken = (String) jsonData.get("accessToken");
					try {
						authenticate.validate(userName,accessToken, "products", "get", "getProdFeature");
					} catch (Exception e) {
						log.info("Unautherized user "+userName+" tried to access getProdFeature function");
						invalidRequestReply = new InvalidInputReplyClass(Response.Status.UNAUTHORIZED.getStatusCode(), Response.Status.UNAUTHORIZED.getReasonPhrase(), e.getMessage());
						return invalidRequestReply;
					}
					GetResponse response = client.prepareGet("mkproducts", "product", id).get();
					Map<String,Object> productsDetails = new HashMap<String,Object>();
					Map<String,Object> source = response.getSource();
					productsDetails.put("features",getProductFeatures(source));
					return new Reply(Response.Status.OK.getStatusCode(), Response.Status.OK.getReasonPhrase(), productsDetails);
				}else{
					log.info("Invalid header keys provided to access getProdFeature function");
					invalidRequestReply = new InvalidInputReplyClass(Response.Status.BAD_REQUEST.getStatusCode(), Response.Status.BAD_REQUEST.getReasonPhrase(), "Invalid keys provided");
					return invalidRequestReply;
				}
			}else{
				log.info("Invalid header json provided to access getProdFeature function");
				invalidRequestReply = new InvalidInputReplyClass(Response.Status.BAD_REQUEST.getStatusCode(), Response.Status.BAD_REQUEST.getReasonPhrase(), "Header data is invalid json");
				return invalidRequestReply;
			}
		}catch(NumberFormatException e){
			log.info("Invalid product id given in getProdFeature functions");
			invalidRequestReply = new InvalidInputReplyClass(Response.Status.BAD_REQUEST.getStatusCode(), Response.Status.BAD_REQUEST.getReasonPhrase(), "Invalid product id provided");
			return invalidRequestReply;
		}
		catch(Exception e){
			e.printStackTrace();
			log.warn("Internal error occured in getProdFeature function");
			invalidRequestReply = new InvalidInputReplyClass(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), Response.Status.INTERNAL_SERVER_ERROR.getReasonPhrase(), "Unknown exception caused");
			return invalidRequestReply;
		}
	}
	
	/**
	 * This function returns the featured products 
	 * @param accessParam
	 * @return
	 */
	@SuppressWarnings({ "unchecked", "unused" })
	@GET
	@Path("/products/featured")
	@Produces(MediaType.APPLICATION_JSON)
	public Object getFeaturedProduct(@HeaderParam("accessParam") String accessParam) {
		try {
			if(isValidJson(accessParam)){
				if(ishavingValidGetProductDetailKeys(accessParam)){
					JSONObject jsonData = helper.jsonParser(accessParam);
					String userName = (String) jsonData.get("userName");
					String accessToken = (String) jsonData.get("accessToken");
					try{
						authenticate.validate(userName,accessToken, "products", "get", "featured");
					}catch(Exception e){
						log.info("Unautherized user "+userName+" tried to access featured function");
						invalidRequestReply = new InvalidInputReplyClass(Response.Status.UNAUTHORIZED.getStatusCode(), Response.Status.UNAUTHORIZED.getReasonPhrase(), e.getMessage());
						return invalidRequestReply;
					}
					String configVarName = "HOME_SUB_CATEGORY_IDS";
					String categoryIds = productDao.getConfigVarValue(configVarName);
					List<CategoryFeatured> categoryList=new ArrayList<>();
					for (String catId : categoryIds.split(",")) {
						GetResponse response = client.prepareGet("mkcategories", "categoryPopularProducts", catId)
								.execute()
								.actionGet();
						String catName=productDao.getNameOfCategory(catId);

						Map<String,Object> source = response.getSource();

						List<Object> listOfProducts=(List<Object>) source.get("products");
						for(Object product:listOfProducts){
							HashMap<String,Object> product1=(HashMap<String,Object>) product;
							String imageUrl="https://cdn1.mebelkart.com/"+product1.get("id_image")+"-home/"+product1.get("link_rewrite")+".jpg";

							HashMap<String,String> image=new HashMap<>();
							image.put("appImageUrl","https://cdn1.mebelkart.com/"+product1.get("id_image")+"-home/"+product1.get("link_rewrite")+".jpg");
							image.put("webImageUrl","https://cdn1.mebelkart.com/"+product1.get("id_image")+"-large/"+product1.get("link_rewrite")+".jpg");
							product1.put("image",image);
							product1.put("type","product");
							product=product1;
						}

						CategoryFeatured categoryReply=new CategoryFeatured("category","",Integer.valueOf(catId),catName,listOfProducts);
						categoryList.add(categoryReply);

					}
					Reply featuredReply=new Reply(Response.Status.OK.getStatusCode(),Response.Status.OK.getReasonPhrase(),categoryList);
					return featuredReply;
				}else{
					log.info("Invalid header keys provided to access featured function in products resource");
					invalidRequestReply = new InvalidInputReplyClass(Response.Status.BAD_REQUEST.getStatusCode(), Response.Status.BAD_REQUEST.getReasonPhrase(), "Invalid keys provided");
					return invalidRequestReply;
				}
			}else{
				log.info("Invalid header json provided to access featured function in products resource");
				invalidRequestReply = new InvalidInputReplyClass(Response.Status.BAD_REQUEST.getStatusCode(), Response.Status.BAD_REQUEST.getReasonPhrase(), "Header data is invalid json");
				return invalidRequestReply;
			}
		} catch (Exception e) {
			InvalidInputReplyClass errorOccured=new InvalidInputReplyClass(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(),Response.Status.INTERNAL_SERVER_ERROR.getReasonPhrase(),"Some error occured while serving request");
			e.printStackTrace();
			return errorOccured;

		}

	}
	
	/**
	 * This will return json of all live products in pagination format corresponding to their category Id
	 * @param accessParam
	 * @return
	 */
	@SuppressWarnings({ "unchecked" })
	@GET
	@Path("/products/seller/{manufacturerId}")
	public Object getProductsListBySeller(@HeaderParam("accessParam") String accessParam,@PathParam("manufacturerId") String manufacturerId){		
		try{
			if(isValidJson(accessParam)){
				if(isHavingValidGetProductDetailBySellerKeys(accessParam)){
					JSONObject jsonData = helper.jsonParser(accessParam);
					String userName = (String) jsonData.get("userName");
					String accessToken = (String) jsonData.get("accessToken");
					try {
						authenticate.validate(userName,accessToken, "products", "get", "getProductsListBySeller");
					} catch (Exception e) {
						log.info("Unautherized user "+userName+" tried to access getProductsListBySeller function");
						invalidRequestReply = new InvalidInputReplyClass(Response.Status.UNAUTHORIZED.getStatusCode(), Response.Status.UNAUTHORIZED.getReasonPhrase(), e.getMessage());
						return invalidRequestReply;
					}
					int manufId = Integer.parseInt(manufacturerId);
					int page  = Integer.parseInt((String) jsonData.get("page"));
					int perPage = Integer.parseInt((String) jsonData.get("limit"));
					int start = ((page - 1) * perPage) + 1;
					int end = page * perPage;
					String[] includes = new String[]
							{"info.id_product","info.name","info.id_category_default","info.name_category_default"
							,"categoryVars.price_without_reduction","categoryVars.price_tax_exc"
							,"all_locations","info.additional_shipping_cost","info.available_now"};
					List<Map<String, String>> productsList = new ArrayList<Map<String,String>>();
					BoolQueryBuilder categoryQuery = QueryBuilders.boolQuery()
							.must(QueryBuilders.matchQuery("info.id_manufacturer",manufId));
					SearchResponse response = client.prepareSearch("mkproducts")
							.setTypes("product")
					        .setFetchSource(includes, null)
					        .setQuery(categoryQuery)
					        .setFrom(start).setSize(perPage).setExplain(true)
					        .execute().actionGet();
					SearchHit[] searchHits = response.getHits().getHits();
					long totalProducts = response.getHits().getTotalHits();
					long totalPages = totalProducts/perPage;
					String currentShowing = start+"-"+end;
					for(int i=0;i<searchHits.length;i++){
						Map<String,String> productsDetails = new HashMap<String,String>();
						Map<String,Object> info = (Map<String, Object>) searchHits[i].getSource().get("info");
						Map<String,Object> categoryVars = (Map<String, Object>) searchHits[i].getSource().get("categoryVars");
						productsDetails.put("productId",(String)info.get("id_product"));
						productsDetails.put("productName",(String)info.get("name"));
						productsDetails.put("categoryId",(String)info.get("id_category_default"));
						productsDetails.put("categoryName",(String)info.get("name_category_default"));
						productsDetails.put("mktPrice",(Integer)categoryVars.get("price_without_reduction")+"");
						productsDetails.put("ourPrice",(Integer)categoryVars.get("price_tax_exc")+"");
						productsDetails.put("emiPrice","https://www.mebelkart.com/getEMIForProduct/"+(String)info.get("id_product")+"?mode=json");
						productsDetails.put("availLocation",(String)searchHits[i].getSource().get("all_locations"));
						productsDetails.put("shippingCost",(String)info.get("additional_shipping_cost"));
						productsDetails.put("shippingAvailable",(String)info.get("available_now"));
						productsList.add(productsDetails);
					}
					System.out.println("Number of hits "+searchHits.length);
					return new PaginationReply(Response.Status.OK.getStatusCode(), Response.Status.OK.getReasonPhrase(),totalProducts,totalPages,page,currentShowing,productsList);
					//return new ProductsPaginationReply(Response.Status.OK.getStatusCode(), Response.Status.OK.getReasonPhrase(),totalProducts,totalPages,page,currentShowing,productsList);
				}else{
					log.info("Invalid header keys provided to access getProductsListBySeller function");
					invalidRequestReply = new InvalidInputReplyClass(Response.Status.BAD_REQUEST.getStatusCode(), Response.Status.BAD_REQUEST.getReasonPhrase(), "Invalid keys provided");
					return invalidRequestReply;
				}
			}else{
				log.info("Invalid header json provided to access getProductsListBySeller function");
				invalidRequestReply = new InvalidInputReplyClass(Response.Status.BAD_REQUEST.getStatusCode(), Response.Status.BAD_REQUEST.getReasonPhrase(), "Header data is invalid json");
				return invalidRequestReply;
			}
		}catch(NumberFormatException e){
			e.printStackTrace();
			log.info("Invalid pageNumber/limit given in getProductsListBySeller functions");
			invalidRequestReply = new InvalidInputReplyClass(Response.Status.BAD_REQUEST.getStatusCode(), Response.Status.BAD_REQUEST.getReasonPhrase(), "Invalid pageNumber/limit");
			return invalidRequestReply;
		}
		catch(Exception e){
			e.printStackTrace();
			log.warn("Internal error occured in getProductsListBySeller function");
			invalidRequestReply = new InvalidInputReplyClass(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), Response.Status.INTERNAL_SERVER_ERROR.getReasonPhrase(), "Unknown exception caused");
			return invalidRequestReply;
		}
	}

	private boolean isHavingValidGetProductDetailBySellerKeys(String accessParam) {
		JSONObject jsonData = helper.jsonParser(accessParam);
		if(jsonData.containsKey("userName") && jsonData.containsKey("accessToken") && jsonData.containsKey("page") && jsonData.containsKey("limit"))
			return true;
		else
			return false;
	}
	
	/**
	 * This will return json of all live products in pagination format corresponding to their category Id
	 * @param accessParam
	 * @return
	 */
	@SuppressWarnings({ "unchecked" })
	@GET
	@Path("/products/outofstock")
	public Object getAllOutOfStock(@HeaderParam("accessParam") String accessParam){		
		try{
			if(isValidJson(accessParam)){
				if(isHavingValidGetProductDetailBySellerKeys(accessParam)){
					JSONObject jsonData = helper.jsonParser(accessParam);
					String userName = (String) jsonData.get("userName");
					String accessToken = (String) jsonData.get("accessToken");
					try {
						authenticate.validate(userName,accessToken, "products", "get", "getAllOutOfStock");
					} catch (Exception e) {
						log.info("Unautherized user "+userName+" tried to access getAllOutOfStock function");
						invalidRequestReply = new InvalidInputReplyClass(Response.Status.UNAUTHORIZED.getStatusCode(), Response.Status.UNAUTHORIZED.getReasonPhrase(), e.getMessage());
						return invalidRequestReply;
					}
					int page  = Integer.parseInt((String) jsonData.get("page"));
					int perPage = Integer.parseInt((String) jsonData.get("limit"));
					int start = ((page - 1) * perPage) + 1;
					int end = page * perPage;
					String[] includes = new String[]
							{"info.id_product","info.name","info.id_category_default","info.name_category_default"
							,"categoryVars.price_without_reduction","categoryVars.price_tax_exc"
							,"all_locations","info.additional_shipping_cost","info.available_now"};
					List<Map<String, String>> productsList = new ArrayList<Map<String,String>>();
					BoolQueryBuilder categoryQuery = QueryBuilders.boolQuery()
							.must(QueryBuilders.rangeQuery("info.quantity").lte(0));
					SearchResponse response = client.prepareSearch("mkproducts")
							.setTypes("product")
					        .setFetchSource(includes, null)
					        .setQuery(categoryQuery)
					        .setFrom(start).setSize(perPage).setExplain(true)
					        .execute().actionGet();
					SearchHit[] searchHits = response.getHits().getHits();
					long totalProducts = response.getHits().getTotalHits();
					long totalPages = totalProducts/perPage;
					String currentShowing = start+"-"+end;
					for(int i=0;i<searchHits.length;i++){
						Map<String,String> productsDetails = new HashMap<String,String>();
						Map<String,Object> info = (Map<String, Object>) searchHits[i].getSource().get("info");
						Map<String,Object> categoryVars = (Map<String, Object>) searchHits[i].getSource().get("categoryVars");
						productsDetails.put("productId",(String)info.get("id_product"));
						productsDetails.put("productName",(String)info.get("name"));
						productsDetails.put("categoryId",(String)info.get("id_category_default"));
						productsDetails.put("categoryName",(String)info.get("name_category_default"));
						productsDetails.put("mktPrice",(Integer)categoryVars.get("price_without_reduction")+"");
						productsDetails.put("ourPrice",(Integer)categoryVars.get("price_tax_exc")+"");
						productsDetails.put("emiPrice","https://www.mebelkart.com/getEMIForProduct/"+(String)info.get("id_product")+"?mode=json");
						productsDetails.put("availLocation",(String)searchHits[i].getSource().get("all_locations"));
						productsDetails.put("shippingCost",(String)info.get("additional_shipping_cost"));
						productsDetails.put("shippingAvailable",(String)info.get("available_now"));
						productsList.add(productsDetails);
					}
					System.out.println("Number of hits "+searchHits.length);
					return new PaginationReply(Response.Status.OK.getStatusCode(), Response.Status.OK.getReasonPhrase(),totalProducts,totalPages,page,currentShowing,productsList);
//					return new ProductsPaginationReply(Response.Status.OK.getStatusCode(), Response.Status.OK.getReasonPhrase(),totalProducts,totalPages,page,currentShowing,productsList);
				}else{
					log.info("Invalid header keys provided to access getAllOutOfStock function");
					invalidRequestReply = new InvalidInputReplyClass(Response.Status.BAD_REQUEST.getStatusCode(), Response.Status.BAD_REQUEST.getReasonPhrase(), "Invalid keys provided");
					return invalidRequestReply;
				}
			}else{
				log.info("Invalid header json provided to access getAllOutOfStock function");
				invalidRequestReply = new InvalidInputReplyClass(Response.Status.BAD_REQUEST.getStatusCode(), Response.Status.BAD_REQUEST.getReasonPhrase(), "Header data is invalid json");
				return invalidRequestReply;
			}
		}catch(NumberFormatException e){
			e.printStackTrace();
			log.info("Invalid pageNumber/limit given in getAllOutOfStock functions");
			invalidRequestReply = new InvalidInputReplyClass(Response.Status.BAD_REQUEST.getStatusCode(), Response.Status.BAD_REQUEST.getReasonPhrase(), "Invalid pageNumber/limit");
			return invalidRequestReply;
		}
		catch(Exception e){
			e.printStackTrace();
			log.warn("Internal error occured in getAllOutOfStock function");
			invalidRequestReply = new InvalidInputReplyClass(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), Response.Status.INTERNAL_SERVER_ERROR.getReasonPhrase(), "Unknown exception caused");
			return invalidRequestReply;
		}
	}
	
	/**
	 * This function returns the top products 
	 * @param accessParam
	 * @return
	 */
	@GET
	@Path("/products/top")
	@Produces(MediaType.APPLICATION_JSON)
	public Object getTopProducts(@HeaderParam("accessParam") String accessParam) {
		try {
			if(isValidJson(accessParam)){
				if(ishavingValidGetTopProductKeys(accessParam)){
					JSONObject jsonData = helper.jsonParser(accessParam);
					String userName = (String) jsonData.get("userName");
					String accessToken = (String) jsonData.get("accessToken");
					try{
						authenticate.validate(userName,accessToken, "products", "get", "getTopProducts");
					}catch(Exception e){
						log.info("Unautherized user "+userName+" tried to access getTopProducts function");
						invalidRequestReply = new InvalidInputReplyClass(Response.Status.UNAUTHORIZED.getStatusCode(), Response.Status.UNAUTHORIZED.getReasonPhrase(), e.getMessage());
						return invalidRequestReply;
					}
					int numberOfProducts  = Integer.parseInt((String) jsonData.get("prodCount"));
					String startDate = (String) jsonData.get("startDate");
					String endDate = (String) jsonData.get("endDate");
					if(helper.isDateValid(startDate) && helper.isDateValid(endDate)){
						if(numberOfProducts < 100){
							List<TopProductsWrapper> topProductsList =  this.productDao.getTopProducts(numberOfProducts,"\""+startDate+"\"","\""+endDate+"\"");
							return new Reply(Response.Status.OK.getStatusCode(), Response.Status.OK.getReasonPhrase(),topProductsList);
						} else{
							log.info("More than 100 Top products requested in getTopProducts function");
							invalidRequestReply = new InvalidInputReplyClass(Response.Status.BAD_REQUEST.getStatusCode(), Response.Status.BAD_REQUEST.getReasonPhrase(), "More than 100 Top products requested, limit is below 100");
							return invalidRequestReply;
						}
					}else{
						log.info("Invalid Date formats provided to access getTopProducts function in products resource");
						invalidRequestReply = new InvalidInputReplyClass(Response.Status.BAD_REQUEST.getStatusCode(), Response.Status.BAD_REQUEST.getReasonPhrase(), "Invalid Date formats, valid formats Ex: yyyy/MM/dd, yyyy-MM-dd, yyyy/MM/dd hh:mm:ss, yyyy-MM-dd hh:mm:ss");
						return invalidRequestReply;
					}
				}else{
					log.info("Invalid header keys provided to access getTopProducts function in products resource");
					invalidRequestReply = new InvalidInputReplyClass(Response.Status.BAD_REQUEST.getStatusCode(), Response.Status.BAD_REQUEST.getReasonPhrase(), "Invalid keys provided");
					return invalidRequestReply;
				}
			}else{
				log.info("Invalid header json provided to access getTopProducts function in products resource");
				invalidRequestReply = new InvalidInputReplyClass(Response.Status.BAD_REQUEST.getStatusCode(), Response.Status.BAD_REQUEST.getReasonPhrase(), "Header data is invalid json");
				return invalidRequestReply;
			}
		} catch(NumberFormatException e){
			e.printStackTrace();
			log.info("Invalid pageNumber/numberOfProducts/startDate/endDate given in getTopProducts function");
			invalidRequestReply = new InvalidInputReplyClass(Response.Status.BAD_REQUEST.getStatusCode(), Response.Status.BAD_REQUEST.getReasonPhrase(), "Invalid pageNumber/limit");
			return invalidRequestReply;
		} catch (Exception e) {
			InvalidInputReplyClass errorOccured=new InvalidInputReplyClass(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(),Response.Status.INTERNAL_SERVER_ERROR.getReasonPhrase(),"Some error occured while serving request");
			e.printStackTrace();
			return errorOccured;
		}
	}

	private boolean ishavingValidGetTopProductKeys(String accessParam) {
		JSONObject jsonData = helper.jsonParser(accessParam);
		if(jsonData.containsKey("userName") && jsonData.containsKey("accessToken") && jsonData.containsKey("prodCount") && jsonData.containsKey("endDate") && jsonData.containsKey("startDate"))
			return true;
		else
			return false;
	}
	
	
	/**
	 * This function returns the FAQ's of the product id 
	 * @param accessParam
	 * @param id
	 * @return
	 */
	@GET
	@Path("/product/{productId}/faq")
	public Object getProdFaq(@HeaderParam("accessParam") String accessParam,@PathParam("productId") String id){
		try{
			if(isValidJson(accessParam)){
				if(ishavingValidGetProductDetailKeys(accessParam)){
					int productId = Integer.parseInt(id);
					JSONObject jsonData = helper.jsonParser(accessParam);
					String userName = (String) jsonData.get("userName");
					String accessToken = (String) jsonData.get("accessToken");
					try {
						authenticate.validate(userName,accessToken, "products", "get", "getProdFaq");
					} catch (Exception e) {
						log.info("Unautherized user "+userName+" tried to access getProdFaq function");
						invalidRequestReply = new InvalidInputReplyClass(Response.Status.UNAUTHORIZED.getStatusCode(), Response.Status.UNAUTHORIZED.getReasonPhrase(), e.getMessage());
						return invalidRequestReply;
					}
					Client localClient = ElasticFactory.getElasticClient();
					BoolQueryBuilder categoryQuery = QueryBuilders.boolQuery()
							.must(QueryBuilders.matchQuery("idProduct",productId));
					SearchResponse response = localClient.prepareSearch("product")
							.setTypes("faq")
					        .setQuery(categoryQuery)
					        .execute().actionGet();
					SearchHit[] searchHits = response.getHits().getHits();
					if(searchHits.length > 0)
						return new Reply(Response.Status.OK.getStatusCode(), Response.Status.OK.getReasonPhrase(), searchHits[0].getSource());
					else
						return new Reply(Response.Status.OK.getStatusCode(), Response.Status.OK.getReasonPhrase(), "No Results");
				}else{
					log.info("Invalid header keys provided to access getProdFaq function");
					invalidRequestReply = new InvalidInputReplyClass(Response.Status.BAD_REQUEST.getStatusCode(), Response.Status.BAD_REQUEST.getReasonPhrase(), "Invalid keys provided");
					return invalidRequestReply;
				}
			}else{
				log.info("Invalid header json provided to access getProdFaq function");
				invalidRequestReply = new InvalidInputReplyClass(Response.Status.BAD_REQUEST.getStatusCode(), Response.Status.BAD_REQUEST.getReasonPhrase(), "Header data is invalid json");
				return invalidRequestReply;
			}
		}catch(NumberFormatException e){
			e.printStackTrace();
			log.info("Invalid product id given in getProdFaq functions");
			invalidRequestReply = new InvalidInputReplyClass(Response.Status.BAD_REQUEST.getStatusCode(), Response.Status.BAD_REQUEST.getReasonPhrase(), "Invalid product id provided");
			return invalidRequestReply;
		}
		catch(Exception e){
			e.printStackTrace();
			log.warn("Internal error occured in getProdFaq function");
			invalidRequestReply = new InvalidInputReplyClass(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), Response.Status.INTERNAL_SERVER_ERROR.getReasonPhrase(), "Unknown exception caused");
			return invalidRequestReply;
		}
	}
}
