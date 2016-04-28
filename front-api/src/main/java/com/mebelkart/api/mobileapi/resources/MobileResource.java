package com.mebelkart.api.mobileapi.resources;

import com.mebelkart.api.mobileapi.api.CategoryFeatured;
import com.mebelkart.api.mobileapi.dao.MobileDao;
import com.mebelkart.api.util.classes.InvalidInputReplyClass;
import com.mebelkart.api.util.factories.ElasticFactory;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.client.Client;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.*;

/**
 * Created by vinitpayal on 27/04/16.
 */
@Path("/v1.0/mobile")
public class MobileResource {

    MobileDao mobileDao;
    Client productsElasticClient = ElasticFactory.getProductsElasticClient();

    public MobileResource(MobileDao mobileDao) {
        this.mobileDao = mobileDao;
    }

    @GET
    @Path("/featured")
    @Produces(MediaType.APPLICATION_JSON)
    public Object getFeaturedProduct() {
        try {
            String configVarName = "HOME_SUB_CATEGORY_IDS";
            String categoryIds = mobileDao.getConfigVarValue(configVarName);
            List<CategoryFeatured> categoryList=new ArrayList<>();

            for (String catId : categoryIds.split(",")) {
                GetResponse response = productsElasticClient.prepareGet("mkcategories", "categoryPopularProducts", catId)
                        .execute()
                        .actionGet();
                String catName=mobileDao.getNameOfCategory(catId);

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

            return categoryList;

        } catch (Exception e) {
            InvalidInputReplyClass errorOccured=new InvalidInputReplyClass(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(),Response.Status.INTERNAL_SERVER_ERROR.getReasonPhrase(),"Some error occured while serving request");
            e.printStackTrace();
            return errorOccured;

        }

    }
}
