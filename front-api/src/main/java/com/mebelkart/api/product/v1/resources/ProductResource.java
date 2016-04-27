package com.mebelkart.api.product.v1.resources;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.client.Client;
import org.json.simple.JSONObject;

import com.mebelkart.api.product.v1.api.ProductDetailsResponse;
import com.mebelkart.api.util.Reply;
import com.mebelkart.api.util.factories.ElasticFactory;

/**
 * @author Tinku
 *
 */
@Path("/v1.0/products")
@Produces({MediaType.APPLICATION_JSON})
public class ProductResource {

	Client client = ElasticFactory.getProductsElasticClient();
	
	public ProductResource() {
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * @return will return json of all live products in pagination format
	 */
	@GET
	@Path("/getProducts")
	public Object getProducts(){
		
		return null;
	}
	
	/**
	 * @param id
	 * @return will return all details of single product
	 */
	@SuppressWarnings("unchecked")
	@GET
	@Path("/product/getProductDetail/{id}")
	public Object getProductDetail(@PathParam("id") String id){
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
		prodDetails.setEmiPrice(null);
		prodDetails.setRating((Integer) source.get("product_rating"));
		prodDetails.setAttributes((String) source.get("attributes"));
		prodDetails.setProductFeatures(getProductFeatures(source));
		prodDetails.setIsSoldOut(0);
		prodDetails.setAttributeGroups(null);
		prodDetails.setReviews(null);
		prodDetails.setTotalReviews(0);
		prodDetails.setTotalReviewsCount(0);
		return new Reply(Response.Status.OK.getStatusCode(), Response.Status.OK.getReasonPhrase(), prodDetails);
	}
	
	@SuppressWarnings("unchecked")
	private Object getProductFeatures(Map<String, Object> source) {
		// TODO Auto-generated method stub
		List<Map<String,Object>> featuresInfo = (List<Map<String, Object>>) source.get("features");
		Map<Object,Object> features = new HashMap<Object,Object>();
		for(int i = 0; i < featuresInfo.size(); i++){
			features.put(featuresInfo.get(i).get("name").toString(), featuresInfo.get(i).get("value").toString());
		}
		return new JSONObject(features);
	}

	@SuppressWarnings("unchecked")
	private Object[] getGallery(Map<String, Object> source) {
		// TODO Auto-generated method stub
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
}
