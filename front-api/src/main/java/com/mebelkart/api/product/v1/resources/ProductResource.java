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
import com.mebelkart.api.product.v1.dao.ProductDao;
import com.mebelkart.api.util.classes.InvalidInputReplyClass;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.client.Client;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mebelkart.api.product.v1.api.ProductDetailsResponse;
import com.mebelkart.api.util.classes.Reply;
import com.mebelkart.api.util.exceptions.HandleException;
import com.mebelkart.api.util.factories.ElasticFactory;
import com.mebelkart.api.util.factories.JedisFactory;

/**
 * @author Tinku
 *
 */
@Path("/v1.0/products")
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
	 * Get actual class name to be printed on
	 */
	static Logger log = LoggerFactory.getLogger(ProductResource.class);
	/**
	 * Exception class
	 */
	HandleException exception = new HandleException();	
	/**
	 * Getting 
	 */
	JedisFactory jedisAuthentication = new JedisFactory();
	
	/**
	 * @return will return json of all live products in pagination format
	 */
	@GET
	@Path("/getAllProducts")
	public Object getAllProducts(){		
		return null;
	}
	
	/**
	 * @return will return json of all live products in pagination format
	 */
	@GET
	@Path("/getProductsListByCategory")
	public Object getProductsByCategory(){		
		return null;
	}
	
	/**
	 * @param id
	 * @return will return all details of single product
	 */
	@SuppressWarnings({ "unchecked", "unused" })
	@GET
	@Path("/product/getProductDetail/{id}")
	public Object getProductDetail(@HeaderParam("accessParam") String accessParam,@PathParam("id") String id){
		try{
			if(isValidJson(accessParam)){
				try{
					int productId = Integer.parseInt(id);
				}catch(NumberFormatException e){
					log.info("Invalid product id given in getProductDetail functions");
					return null;
				}
				String userName = "";
				String accessToken = "";
				int isUserAuthorized = jedisAuthentication.validate(userName,accessToken, "products", "get", "getProductDetail");
				if(isUserAuthorized == 1){
					GetResponse response = client.prepareGet("mkproducts", "product", id).get();
					ProductDetailsResponse prodDetails = new ProductDetailsResponse();
					Map<String,Object> source = response.getSource();
					Map<String,Object> info = (Map<String, Object>) source.get("info");
					Map<String,Object> categoryVars = (Map<String, Object>) source.get("categoryVars");
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
					log.info("Un autherized user "+userName+" tried to access getProductDetail function");
					return null;
				}
			}else{
				log.info("In valid header data provided to access getProductDetail function");
				return null;
			}
		}catch(Exception e){
			e.printStackTrace();
			log.warn("Internal error occured in getProductDetail function");
			exception = new HandleException(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(),Response.Status.INTERNAL_SERVER_ERROR.getReasonPhrase());
			return exception.getException("Connection refused server stopped", null);
		}
	}

	private boolean isValidJson(String accessParam) {
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
	 * @param id
	 * @return
	 */
	@GET
	@Path("/product/getSellingPrice/{id}")
	public Object getSellingPrice(@PathParam("id") String id){
		
		return null;
	}
	
	/**
	 * @param id
	 * @return
	 */
	@GET
	@Path("/product/getProdDesc/{id}")
	public Object getProducts(@PathParam("id") String id){
		
		return null;
	}
	
	/**
	 * @param id
	 * @return
	 */
	@GET
	@Path("/product/getProdAttr/{id}")
	public Object getProdAttr(@PathParam("id") String id){
		
		return null;
	}
	
	/**
	 * @param id
	 * @return
	 */
	@GET
	@Path("/product/getProdFeature/{id}")
	public Object getProdFeature(@PathParam("id") String id){
		
		return null;
	}
	
	/**
	 * @param id
	 * @return
	 */
	@GET
	@Path("/product/getProdFaq/{id}")
	public Object getProdFaq(@PathParam("id") String id){
		
		return null;
	}
	
	/**
	 * @param id
	 * @return
	 */
	@GET
	@Path("/product/getProdReview/{id}")
	public Object getProdReview(@PathParam("id") String id){
		
		return null;
	}

	@GET
	@Path("/featured")
	@Produces(MediaType.APPLICATION_JSON)
	public Object getFeaturedProduct() {
		try {
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

		} catch (Exception e) {
			InvalidInputReplyClass errorOccured=new InvalidInputReplyClass(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(),Response.Status.INTERNAL_SERVER_ERROR.getReasonPhrase(),"Some error occured while serving request");
			e.printStackTrace();
			return errorOccured;

		}

	}



}
