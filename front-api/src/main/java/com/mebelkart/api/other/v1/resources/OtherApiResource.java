/**
 * 
 */
package com.mebelkart.api.other.v1.resources;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import redis.clients.jedis.Jedis;

import com.codahale.metrics.annotation.Timed;
import com.mebelkart.api.other.v1.core.CategoryWrapper;
import com.mebelkart.api.other.v1.core.DealsWrapper;
import com.mebelkart.api.other.v1.dao.OtherApiDao;
import com.mebelkart.api.other.v1.helper.HelperMethods;
import com.mebelkart.api.util.classes.InvalidInputReplyClass;
import com.mebelkart.api.util.classes.Reply;
import com.mebelkart.api.util.factories.ElasticFactory;
import com.mebelkart.api.util.factories.JedisFactory;
import com.mebelkart.api.util.helpers.Authentication;
import com.mebelkart.api.util.helpers.Helper;

/**
 * @author Tinku
 *
 */
@Path("/v1.0")
@Produces({MediaType.APPLICATION_JSON})
public class OtherApiResource {
	
	OtherApiDao dao;

	/**
	 * Constructer
	 */
	public OtherApiResource(OtherApiDao dao) {
		this.dao = dao;
	}
		
	/**
	 * Get actual class name to be printed on log files
	 */
	static Logger log = LoggerFactory.getLogger(OtherApiResource.class);
	/**
	 * InvalidInputReplyClass class
	 */
	InvalidInputReplyClass invalidRequestReply = null;	
	/**
	 * Getting client to authenticate
	 */
	Authentication authenticate = new Authentication();
	
	JedisFactory jedisFactory = new JedisFactory();

	/**
	 * Helper class from utils
	 */
	Helper helper = new Helper();
	
	HelperMethods internalHelper = new HelperMethods(); 
	
	/**
	 * Getting products elastic client connection
	 */
	Client client = ElasticFactory.getProductsElasticClient();
	
//	/**
//	 * This will return json of menu
//	 * @return will return json of menu
//	 */
//	@GET
//	@Path("/menu")
//	@Timed
//	public Object getMenu(@HeaderParam("accessParam") String accessParam){
//		try{
//			if(helper.isValidJson(accessParam)){
//				if(isHavingValidAccessParamKeys(accessParam)){
//					JSONObject jsonData = helper.jsonParser(accessParam);
//					String userName = (String) jsonData.get("userName");
//					String accessToken = (String) jsonData.get("accessToken");
//					try {
//						authenticate.validate(userName,accessToken, "other", "get", "getMenu");
//					} catch (Exception e) {
//						log.info("Unautherized user "+userName+" tried to access getMenu function");
//						invalidRequestReply = new InvalidInputReplyClass(Response.Status.UNAUTHORIZED.getStatusCode(), Response.Status.UNAUTHORIZED.getReasonPhrase(), e.getMessage());
//						return invalidRequestReply;
//					}
//				}
//			}
//			return null;
//		}catch(Exception e){
//			e.printStackTrace();
//			log.warn("Internal error occured in getMenu function");
//			invalidRequestReply = new InvalidInputReplyClass(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), Response.Status.INTERNAL_SERVER_ERROR.getReasonPhrase(), "Unknown exception caused");
//			return invalidRequestReply;
//		}
//	}
		
	private boolean isHavingValidAccessParamKeys(String accessParam) {
		JSONObject jsonData = helper.jsonParser(accessParam);
		if(jsonData.containsKey("userName") && jsonData.containsKey("accessToken")){
			return true;
		}else
			return false;
	}
	
	/**
	 * This will return json of best deals
	 * @return will return json of best deals
	 */
	@GET
	@Path("{deals}")
	@Timed
	public Object getBestDeals(@HeaderParam("accessParam") String accessParam,@PathParam("deals") String deals,@QueryParam("cusId") int customerId,@QueryParam("cityId") int cityId,@QueryParam("nbr") int nbr,@QueryParam("refresh") String refresh){
		try{
			if(helper.isValidJson(accessParam)){
				if(isHavingValidAccessParamKeys(accessParam)){
					JSONObject jsonData = helper.jsonParser(accessParam);
					String userName = (String) jsonData.get("userName");
					String accessToken = (String) jsonData.get("accessToken");
					try {
						authenticate.validate(userName,accessToken, "other", "get", "getBestDeals");
					} catch (Exception e) {
						log.info("Unautherized user "+userName+" tried to access getBestDeals function");
						invalidRequestReply = new InvalidInputReplyClass(Response.Status.UNAUTHORIZED.getStatusCode(), Response.Status.UNAUTHORIZED.getReasonPhrase(), e.getMessage());
						return invalidRequestReply;
					}
					
					if(deals.equalsIgnoreCase("deals")){
						if(customerId > 0 && cityId > 0 && nbr > 0)
							if("yes".equalsIgnoreCase(refresh) || "no".equalsIgnoreCase(refresh))
								return new Reply(Response.Status.OK.getStatusCode(), Response.Status.OK.getReasonPhrase(), deals(customerId,cityId,refresh,nbr));
							else{
								invalidRequestReply = new InvalidInputReplyClass(Response.Status.BAD_REQUEST.getStatusCode(), Response.Status.BAD_REQUEST.getReasonPhrase(), "Please provide valid refresh param, i.e, yes or no");
								return invalidRequestReply;
							}
						else{
							invalidRequestReply = new InvalidInputReplyClass(Response.Status.BAD_REQUEST.getStatusCode(), Response.Status.BAD_REQUEST.getReasonPhrase(), "Please provide valid Customer Id and City Id and nbr");
							return invalidRequestReply;
						}							
					}else if(deals.equalsIgnoreCase("dealsoftheday")){
						if(nbr > 0)
							if("yes".equalsIgnoreCase(refresh) || "no".equalsIgnoreCase(refresh))
								return new Reply(Response.Status.OK.getStatusCode(), Response.Status.OK.getReasonPhrase(), bestdeals("web",nbr,refresh));
							else{
								invalidRequestReply = new InvalidInputReplyClass(Response.Status.BAD_REQUEST.getStatusCode(), Response.Status.BAD_REQUEST.getReasonPhrase(), "Please provide valid refresh param, i.e, yes or no");
								return invalidRequestReply;
							}	
						else{
							invalidRequestReply = new InvalidInputReplyClass(Response.Status.BAD_REQUEST.getStatusCode(), Response.Status.BAD_REQUEST.getReasonPhrase(), "Please provide valid nbr (number of products per row)");
							return invalidRequestReply;
						}							
					}else{
						invalidRequestReply = new InvalidInputReplyClass(Response.Status.BAD_REQUEST.getStatusCode(), Response.Status.BAD_REQUEST.getReasonPhrase(), "URL doesn't exist, Please provide valid URL");
						return invalidRequestReply;
					}
				}else{
					log.info("Invalid header keys provided to access getBestDeals function");
					invalidRequestReply = new InvalidInputReplyClass(Response.Status.BAD_REQUEST.getStatusCode(), Response.Status.BAD_REQUEST.getReasonPhrase(), "Invalid keys provided");
					return invalidRequestReply;
				}
			}else{
				log.info("Invalid header json provided to access getBestDeals function");
				invalidRequestReply = new InvalidInputReplyClass(Response.Status.BAD_REQUEST.getStatusCode(), Response.Status.BAD_REQUEST.getReasonPhrase(), "Header data is invalid json/You may have not passed header details");
				return invalidRequestReply;
			}
		}catch(Exception e){
			e.printStackTrace();
			log.warn("Internal error occured in getBestDeals function");
			invalidRequestReply = new InvalidInputReplyClass(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), Response.Status.INTERNAL_SERVER_ERROR.getReasonPhrase(), "Unknown exception caused");
			return invalidRequestReply;
		}
	}

	/**
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Object bestdeals(String callType, int nbr, String refresh) {
		Jedis jedis = jedisFactory.getJedisConnection();
		if(jedis.exists("dealsOfTheDay") && !refresh.equalsIgnoreCase("yes")){
			Map<String,String> categoriesWithProductDetails = jedis.hgetAll("dealsOfTheDay");
			if(!categoriesWithProductDetails.isEmpty()){
				return internalHelper.getDealsPage(categoriesWithProductDetails);
			}
		}
		int categoryId = this.dao.getDataFromConfiguration("HOME_DOTD_CATEGORY");
		
		BoolQueryBuilder filterQuery = QueryBuilders.boolQuery();
		filterQuery.must(QueryBuilders.matchQuery("all_category_ids",categoryId))
		.must(QueryBuilders.matchQuery("deals.isFlashSaleEnabled",1))
		.must(QueryBuilders.rangeQuery("deals.isFlashSaleValidForProductInt").gte(1))
		.must(QueryBuilders.rangeQuery("deals.flash_sale_product_active_int").gte(0))
		.must(QueryBuilders.rangeQuery("deals.is_fs_product_int").gte(1))
		.must(QueryBuilders.rangeQuery("info.quantity").gt(0))
		.must(QueryBuilders.rangeQuery("info.available_for_order").gt(0))
		.must(QueryBuilders.rangeQuery("info.show_price").gt(0));
		
		
		String[] includes = new String[]
				{"info.id_product","info.name","info.id_category_default","info.name_category_default"
				,"categoryVars.price_without_reduction","categoryVars.price_tax_exc"
				,"product_rating","deals.flashSaleDateEnd","info.id_image","images","info.link_rewrite","param3.popularityNetScore"};
		
		List<DealsWrapper> productsList = new ArrayList<DealsWrapper>();
		SearchResponse response = client.prepareSearch("mkproducts")
				.setTypes("product")
		        .setFetchSource(includes, null)
		        .setQuery(filterQuery)
		        //.addSort(SortBuilders.fieldSort("param3.popularityNetScore").order(SortOrder.DESC))
		        .addSort(SortBuilders.fieldSort("deals.flash_sale_date_end_analyzed").order(SortOrder.DESC))
		        .setFrom(0).setSize(nbr).setExplain(true)
		        .execute().actionGet();
		
		SearchHit[] searchHits = response.getHits().getHits();
		for(int i=0;i<searchHits.length;i++){
			DealsWrapper productsDetails = new DealsWrapper();
			Map<String,Object> info = (Map<String, Object>) searchHits[i].getSource().get("info");
			Map<String,Object> categoryVars = (Map<String, Object>) searchHits[i].getSource().get("categoryVars");
			Map<String,Object> deals = (Map<String,Object>) searchHits[i].getSource().get("deals");
			productsDetails.setProductId(Integer.parseInt((String)info.get("id_product")));
			productsDetails.setProductName((String)info.get("name"));
			if(callType.equalsIgnoreCase("mobile"))
				productsDetails.setProductImage("https://cdn1.mebelkart.com/"+(String)info.get("id_image")+"-home/"+(String)info.get("link_rewrite")+".jpg");
			else if(callType.equalsIgnoreCase("web"))
				productsDetails.setProductImage("https://cdn1.mebelkart.com/"+(String)info.get("id_image")+"-home/"+(String)info.get("link_rewrite")+".jpg");
			productsDetails.setCatId(Integer.parseInt((String)info.get("id_category_default")));
			productsDetails.setMktPrice((Integer)categoryVars.get("price_without_reduction"));
			productsDetails.setOurPrice((Integer)categoryVars.get("price_tax_exc"));			
			productsDetails.setFlashSaleEndDate((String)deals.get("flashSaleDateEnd"));
			productsDetails.setFsAvailability("1");
			productsList.add(productsDetails);
		}
		List<Object> prodDet = new ArrayList<Object>();
		Map<String,Object> prod = new HashMap<String,Object>();
		prod.put("categoryId", 0);
		prod.put("catName", "Best Deals");
		prod.put("products", productsList);
		prodDet.add(prod);
		jedis.del("dealsOfTheDay");
		jedis.hmset("dealsOfTheDay", internalHelper.convertDealsPageDataIntoMap(prodDet));
		return prodDet;
	}

	/**
	 * @return
	 */
	public Object deals(int custId,int cityId, String refresh, int nbr) {
		Jedis jedis = jedisFactory.getJedisConnection();
		if(jedis.exists("dealsPage") && !refresh.equalsIgnoreCase("yes")){
			Map<String,String> categoriesWithProductDetails = jedis.hgetAll("dealsPage");
			if(!categoriesWithProductDetails.isEmpty()){
				return internalHelper.getDealsPage(categoriesWithProductDetails);
			}
		}
		int flashSaleCatId = this.dao.getDataFromConfiguration("FLASHSALE_CATEGORY_ID");
		List<CategoryWrapper> cat = this.dao.getCategoryIds(flashSaleCatId);
		int nb = this.dao.getDataFromConfiguration("FLASHSALE_CATEGORIES_NBR");
		if(nb > nbr){
			nb = nbr;
		}
		int lang = this.dao.getDataFromConfiguration("PS_LANG_DEFAULT"); // pick up value from db table name is ps_configuration
		List<Object> results = new ArrayList<Object>();
		for(int i = 0; i < cat.size(); i++){
			Map<String,Object> temp = new HashMap<String,Object>();
			List<DealsWrapper> result = getFlashSalesProductsByCategory(cat.get(i).getCatId(),lang,1,(nb > 0 ? nb : 4),"","",custId,cityId);
			if(result.size() > 0){
				temp.put("categoryId",cat.get(i).getCatId());
				temp.put("catName", cat.get(i).getCatName());
				temp.put("products",result);
				results.add(temp);
			}
		}
		jedis.del("dealsPage");
		jedis.hmset("dealsPage", internalHelper.convertDealsPageDataIntoMap(results));
		return results;
	}

	/**
	 * @param string
	 * @param lang
	 * @param i
	 * @param j
	 */
	private List<DealsWrapper> getFlashSalesProductsByCategory(int catId, int langId,int pageNumber, int nbProducts,String orderBy, String orderWay,int custId,int cityId) {
		String currentTimeStamp = helper.getCurrentDateString();
		int cityIdAllCities = this.dao.getDataFromConfiguration("ALL_CITIES_ID");
		int startLimit = (pageNumber - 1) * nbProducts;
		if(pageNumber < 0) pageNumber = 0;
		if(nbProducts < 1) nbProducts = 10;
		if(orderBy.isEmpty() || orderBy == null || orderBy.equalsIgnoreCase("position")) orderBy = "flash_sale_date_end";
		if(orderWay.isEmpty() || orderWay == null) orderWay = "ASC";
		List<String> groups = this.dao.getCurrentCustomerGroups(custId);
		String sqlGroups = (groups.size() > 0 ? "IN ("+String.join(",", groups)+")": "= 1");
		String joinQuery = (cityId > 0 ? " LEFT JOIN ps_product_location prl ON (p.id_product = prl.id_product)" : " ");
		String catQuery = (catId > 0 ? " AND cp.id_category = "+catId : " ");
		String cityQuery = (cityId > 0 ? " AND prl.active=1 AND prl.id_city IN (\'"+cityId+"\',\'"+cityIdAllCities+"\')" : " ");
		String lte = "<=";
		List<DealsWrapper> result = this.dao.getFlashSaleProductsByCategorySQL(currentTimeStamp,langId,sqlGroups,orderBy,orderWay,startLimit,nbProducts,joinQuery,catQuery,cityQuery,lte);
		result = getProductDetailsFromElastic(result);
		return result;		
	} 

	/**
	 * @param result
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private List<DealsWrapper> getProductDetailsFromElastic(List<DealsWrapper> result) {
		for(int i = 0; i < result.size(); i++){
			GetResponse response = client.prepareGet("mkproducts", "product", result.get(i).getProductId()+"").get();
			Map<String,Object> source = response.getSource();
			Map<String,Object> categoryVars = (Map<String, Object>) source.get("categoryVars");
			result.get(i).setProductName((String)categoryVars.get("name"));
			result.get(i).setProductImage((String)((Map<String, Object>)((Map<String, Object>)source.get("product_name_suggest")).get("payload")).get("product_image_link"));
			result.get(i).setMktPrice((Integer)categoryVars.get("price_without_reduction"));
			result.get(i).setOurPrice((Integer)categoryVars.get("price_tax_exc"));
			result.get(i).setCatId(Integer.parseInt((String)((Map<String, Object>) source.get("info")).get("id_category_default")));
		}
		return result;
	}

//	private Object getGallery(String imageId,String imageName) {
//		Map<Object,Object> images = new HashMap<Object,Object>();
//		images.put("homeImage", "https://cdn1.mebelkart.com/"+imageId+"-home/"+imageName+".jpg");
//		images.put("largeImage", "https://cdn1.mebelkart.com/"+imageId+"-large/"+imageName+".jpg");
//		images.put("largerImage", "https://cdn1.mebelkart.com/"+imageId+"-larger/"+imageName+".jpg");			
//		return images;
//	}
}
