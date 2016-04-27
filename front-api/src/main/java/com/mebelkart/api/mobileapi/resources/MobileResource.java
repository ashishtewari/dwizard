package com.mebelkart.api.mobileapi.resources;

import com.mebelkart.api.mobileapi.dao.MobileDao;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 * Created by vinitpayal on 27/04/16.
 */
@Path("/v1.0/mobile")
public class MobileResource  {

    MobileDao mobileDao;

    public MobileResource(MobileDao mobileDao) {
        this.mobileDao = mobileDao;
    }

    @GET
    @Path("/featured")
    @Produces(MediaType.APPLICATION_JSON)
    public Object getFeaturedProduct()
    {
        String configVarName="HOME_SUB_CATEGORY_IDS";
        String categoryIds=mobileDao.getConfigVarValue(configVarName);

        for (String retval: categoryIds.split(",")){

            System.out.println("category id"+retval);
        }


        //System.out.println("sub categories "+categoryIds);
        return null;
    }
}
