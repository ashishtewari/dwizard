package com.mebelkart.api.order.v1.resources;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.util.JSONPObject;
import com.mebelkart.api.order.v1.dao.OrderDao;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

/**
 * Created by vinitpayal on 12/04/16.
 */
class Entity {
    @JsonProperty String name;
}

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
            JSONObject json = (JSONObject) new JSONParser().parse(headerParam);
            System.out.println(json.get("fname")+" "+json.get("lname"));
        } catch (ParseException e) {
            e.printStackTrace();
            System.out.println("Exception occured while parsing json string");
        }
        List<Integer> allOrderDetail=orderDao.getAllOrders();
        return allOrderDetail;
    }
 }
