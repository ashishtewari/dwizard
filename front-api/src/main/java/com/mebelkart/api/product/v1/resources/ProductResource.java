package com.mebelkart.api.product.v1.resources;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.client.Client;

import com.mebelkart.api.util.factories.ElasticFactory;

/**
 * @author Tinku
 *
 */
@Path("/v1.0/products")
@Produces({MediaType.APPLICATION_JSON})
public class ProductResource {

	@SuppressWarnings("static-access")
	Client client = new ElasticFactory().getRemoteElasticClient();
	
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
	@GET
	@Path("/product/getProductDetail/{id}")
	public Object getProductDetail(@PathParam("id") String id){
		GetResponse response = client.prepareGet("mkproducts", "product", id).get();
		return response.getSource();
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
