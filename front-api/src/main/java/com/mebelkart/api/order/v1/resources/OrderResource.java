package com.mebelkart.api.order.v1.resources;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.util.JSONPObject;
import com.mebelkart.api.order.v1.dao.OrderDao;
import io.dropwizard.jersey.params.DateTimeParam;
import org.joda.time.DateTime;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Created by vinitpayal on 12/04/16.
 */
@Path("/v1.0/order")
public class OrderResource {
    OrderDao orderDao;

    public OrderResource(OrderDao orderDao) {
        this.orderDao = orderDao;
    }

    @GET
    @Path("/all")
    @Produces({ MediaType.APPLICATION_JSON })
    public Object getAllOrders(@HeaderParam("headerParam") String headerParam)
    {
        System.out.println(headerParam);
        try {
            JSONObject headerParamJson = (JSONObject) new JSONParser().parse(headerParam);
            SimpleDateFormat formatter = new SimpleDateFormat("dd-M-yyyy HH:mm:SS");
            String whereQuery="";
            Date fromDate=null;
            Date toDate=null;
            Integer merchantId=null;
            String statusRequired=null;
            if(headerParamJson.containsKey("from")) {
                fromDate = formatter.parse((String) headerParamJson.get("from"));
                whereQuery+=" o.date_add >= :fromDate";
                System.out.println("from value :"+fromDate);
            }
            if(headerParamJson.containsKey("to")){
                toDate = formatter.parse((String) headerParamJson.get("to"));
                if(whereQuery!=""){
                    whereQuery+=" and ";
                }
                whereQuery+="o.date_add <= :toDate";
                System.out.println("to value :"+toDate);
            }
            if(headerParamJson.containsKey("merchant")){
                merchantId= (Integer) headerParamJson.get("merchant");

            }
            if(headerParamJson.containsKey("status")){
                statusRequired=((String) headerParamJson.get("status")).toLowerCase();
                if(whereQuery!=""){
                    whereQuery+=" and ";
                }
                whereQuery+="LOWER(status_name)= :statusRequired";

            }
//            System.out.println(" Where condition of sql query "+whereQuery);
            List<Integer> allOrderDetail=orderDao.getAllOrders(whereQuery,fromDate,toDate,statusRequired);
            return allOrderDetail;
        } catch (ParseException e) {
            e.printStackTrace();
            System.out.println("Exception occured while parsing json string");
            return null;
        }
        catch (NullPointerException npe){
            npe.printStackTrace();
            return null;
        }
        catch (Exception e) {
            e.printStackTrace();
            System.out.println("generic exception occured");
            return null;
        }

    }
 }
