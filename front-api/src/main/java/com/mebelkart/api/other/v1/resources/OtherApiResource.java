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
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.codahale.metrics.annotation.Timed;
import com.mebelkart.api.other.v1.core.DealsWrapper;
import com.mebelkart.api.other.v1.dao.OtherApiDao;
import com.mebelkart.api.util.classes.InvalidInputReplyClass;
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

	/**
	 * Helper class from utils
	 */
	Helper helper = new Helper();
	
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
	@Path("deals")
	@Timed
	public Object getBestDeals(@HeaderParam("accessParam") String accessParam,@QueryParam("catId") int categoryId,@QueryParam("cusId") int customerId){
		try{
			if(helper.isValidJson(accessParam)){
				if(isHavingValidAccessParamKeys(accessParam)){
					JSONObject jsonData = helper.jsonParser(accessParam);
					String userName = (String) jsonData.get("userName");
					String accessToken = (String) jsonData.get("accessToken");
					try {
						authenticate.validate(userName,accessToken, "other", "get", "getBestDeals");
					} catch (Exception e) {
						log.info("Unautherized user "+userName+" tried to access getMenu function");
						invalidRequestReply = new InvalidInputReplyClass(Response.Status.UNAUTHORIZED.getStatusCode(), Response.Status.UNAUTHORIZED.getReasonPhrase(), e.getMessage());
						return invalidRequestReply;
					}
					
					if(categoryId == 0 ){
						return new Reply(Response.Status.OK.getStatusCode(), Response.Status.OK.getReasonPhrase(), deals(customerId));
					}else{
						return new Reply(Response.Status.OK.getStatusCode(), Response.Status.OK.getReasonPhrase(), bestdeals(categoryId));
					}
				}
			}
			return null;
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
	private Object bestdeals(int categoryId) {
		BoolQueryBuilder filterQuery = QueryBuilders.boolQuery();
		filterQuery.must(QueryBuilders.matchQuery("all_category_ids",categoryId))
		.must(QueryBuilders.matchQuery("deals.isFlashSaleEnabled",1))
		.must(QueryBuilders.rangeQuery("deals.isFlashSaleValidForProductInt").gte(1))
		.must(QueryBuilders.rangeQuery("deals.flash_sale_product_active_int").gte(1))
		.must(QueryBuilders.rangeQuery("deals.is_fs_product_int").gte(1))
		.must(QueryBuilders.rangeQuery("info.quantity").gt(0))
		.must(QueryBuilders.rangeQuery("info.available_for_order").gt(0))
		.must(QueryBuilders.rangeQuery("info.show_price").gt(0));
		String[] includes = new String[]
				{"info.id_product","info.name","info.id_category_default","info.name_category_default"
				,"categoryVars.price_without_reduction","categoryVars.price_tax_exc"
				,"product_rating","deals.flashSaleDateEnd","info.id_image","images","info.link_rewrite"};
		List<Map<String, Object>> productsList = new ArrayList<Map<String,Object>>();
		SearchResponse response = client.prepareSearch("mkproducts")
				.setTypes("product")
		        .setFetchSource(includes, null)
		        .setQuery(filterQuery)
		        .setFrom(0).setSize(20).setExplain(true)
		        //.addSort(field, order)
		        .execute().actionGet();
		SearchHit[] searchHits = response.getHits().getHits();
		for(int i=0;i<searchHits.length;i++){
			Map<String,Object> productsDetails = new HashMap<String,Object>();
			Map<String,Object> info = (Map<String, Object>) searchHits[i].getSource().get("info");
			Map<String,Object> categoryVars = (Map<String, Object>) searchHits[i].getSource().get("categoryVars");
			Map<String,Object> deals = (Map<String,Object>) searchHits[i].getSource().get("deals");
			productsDetails.put("productId",(String)info.get("id_product"));
			productsDetails.put("productName",(String)info.get("name"));
			System.out.println("Product name: "+(String)info.get("name"));
			productsDetails.put("categoryId",(String)info.get("id_category_default"));
			productsDetails.put("categoryName",(String)info.get("name_category_default"));
			productsDetails.put("mktPrice",(Integer)categoryVars.get("price_without_reduction")+"");
			productsDetails.put("ourPrice",(Integer)categoryVars.get("price_tax_exc")+"");
			productsDetails.put("rating",(Integer)searchHits[i].getSource().get("product_rating"));
			productsDetails.put("gallery",getGallery((String)info.get("id_image"),(String)info.get("link_rewrite")));
			productsDetails.put("flashSaleEndDate",(String)deals.get("flashSaleDateEnd"));
			productsList.add(productsDetails);
		}
		System.out.println("Number of products in best deals: "+searchHits.length);
		return productsList;
	}

	/**
	 * @return
	 */
	private Object deals(int custId) {
//		int requiredDepth = 0;
//		int customerId = 0; // Here zero means null
//		String groups = "";
//		if(customerId != 0){
//			groups = String.join(",", this.dao.getGroupsStatic(customerId));
//		}else{
//			// This is the default customer group id
//			groups= "1";
//		}
//		//System.out.println("Customer group statics are "+groups);
//		int isFlashSalesEnabled = this.dao.getDataFromConfiguration("PS_FLASH_SALES_ENABLED"); // pick up value from db table name is ps_configuration
		int flashSaleCatId = this.dao.getDataFromConfiguration("FLASHSALE_CATEGORY_ID");
		List<String> catIds = this.dao.getCategoryIds(flashSaleCatId);
		int nb = this.dao.getDataFromConfiguration("FLASHSALE_CATEGORIES_NBR");
		int lang = this.dao.getDataFromConfiguration("PS_LANG_DEFAULT"); // pick up value from db table name is ps_configuration
		List<Object> results = new ArrayList<Object>();
		for(int i = 0; i < catIds.size(); i++){
			System.out.println("CategoryID is: "+catIds.get(i));
			List<DealsWrapper> result = getFlashSalesProductsByCategory(Integer.parseInt(catIds.get(i)),lang,1,(nb > 0 ? nb : 4),"","",custId);
			results.add(result);
		}
//		List<String> data = new ArrayList<String>();
//		int cityId = 0; // Here zero means null, made it null to exclude based on cityId
//		int idCityAllCities = this.dao.getDataFromConfiguration("ALL_CITIES_ID");
//		
//		int maxDepth = 0; // here zero means null
//		if(requiredDepth == 0)
//			maxDepth = this.dao.getDataFromConfiguration("BLOCK_CATEG_MAX_DEPTH") + 3;
//		else
//			maxDepth = 1;
		return results;
	}

	/**
	 * @param string
	 * @param lang
	 * @param i
	 * @param j
	 */
	private List<DealsWrapper> getFlashSalesProductsByCategory(int catId, int langId,int pageNumber, int nbProducts,String orderBy, String orderWay,int custId) {
		String currentTimeStamp = helper.getCurrentDateString();
		int cityId = this.dao.getDataFromConfiguration("ID_OTHER_CITIES");
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
		
		for(int i = 0; i < result.size(); i++){
			System.out.println("The product ID is: "+result.get(i).getFsAvailability()+" for catId "+catId);
		}
		return result;
		
	} 

	private Object getGallery(String imageId,String imageName) {
		Map<Object,Object> images = new HashMap<Object,Object>();
		images.put("homeImage", "https://cdn1.mebelkart.com/"+imageId+"-home/"+imageName+".jpg");
		images.put("largeImage", "https://cdn1.mebelkart.com/"+imageId+"-large/"+imageName+".jpg");
		images.put("largerImage", "https://cdn1.mebelkart.com/"+imageId+"-larger/"+imageName+".jpg");			
		return images;
	}
}
