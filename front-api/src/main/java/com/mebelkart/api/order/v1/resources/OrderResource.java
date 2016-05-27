package com.mebelkart.api.order.v1.resources;

import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;

import com.codahale.metrics.annotation.Timed;
import com.mebelkart.api.admin.v1.api.AdminResponse;
import com.mebelkart.api.order.v1.dao.OrderDao;
import com.mebelkart.api.order.v1.core.Order;
import com.mebelkart.api.util.classes.PaginationReply;
import com.mebelkart.api.util.classes.InvalidInputReplyClass;
import com.mebelkart.api.util.classes.Reply;
import com.mebelkart.api.util.factories.ElasticFactory;
import com.mebelkart.api.util.helpers.Authentication;
import com.mebelkart.api.util.helpers.Helper;

import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.client.Client;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Created by vinitpayal on 12/04/16.
 */
@Path("/v1.0")
public class OrderResource {
    OrderDao orderDao;
    /**
	 * Getting client to authenticate
	 */
	Authentication authenticate = new Authentication();
	/**
	 * Getting order-status elastic client connection
	 */
	Client client = ElasticFactory.getElasticClient();
	/**
	 * Helper class from utils
	 */
	Helper helper = new Helper();
	/**
	 * Get actual class name to be printed on log files
	 */
	static Logger log = LoggerFactory.getLogger(OrderResource.class);

    public OrderResource(OrderDao orderDao) {
        this.orderDao = orderDao;
    }

    @GET
    @Path("/orders")
    @Produces({ MediaType.APPLICATION_JSON })
    @Timed
    public Object getAllOrders(@HeaderParam("accessParam") String headerParam)
    {
        try {
            JSONObject headerParamJson = (JSONObject) new JSONParser().parse(headerParam);
            String userName = (String) headerParamJson.get("userName");
			String accessToken = (String) headerParamJson.get("accessToken");
            try{
            	authenticate.validate(userName,accessToken, "order", "get", "getAllOrders");
            }catch(Exception e){
            	log.info("Unautherized user "+userName+" tried to access getAllOrders function");
            	InvalidInputReplyClass invalidRequestReply = new InvalidInputReplyClass(Response.Status.UNAUTHORIZED.getStatusCode(), Response.Status.UNAUTHORIZED.getReasonPhrase(), e.getMessage());
				return invalidRequestReply;
            }
            	SimpleDateFormat formatter = new SimpleDateFormat("dd-M-yyyy HH:mm:SS");
                String whereQuery="";
                Date fromDate=null;
                Date toDate=null;
                Integer merchantId=null;
                String statusRequired=null;
                Integer orderId=null;
                Integer offset=null;//offset is for pagination from where we have to display output
                Integer currentPageNum=null;

                /*
                    Will check for every key if it is present then add it to where condition otherwise not
                 */

                if(headerParamJson.containsKey("from")) {
                    fromDate = formatter.parse((String) headerParamJson.get("from"));
                    whereQuery+=" and o.date_add >= :fromDate";
                }
                if(headerParamJson.containsKey("to")){
                    toDate = formatter.parse((String) headerParamJson.get("to"));
                    /*
                        To check whether any where query is already present if yes then append and and write new where condition
                     */

//                    if(whereQuery!=""){
//                        whereQuery+=" and ";
//                    }
                    whereQuery+=" and o.date_add <= :toDate";
//                    System.out.println("to value :"+toDate);
                }
                if(headerParamJson.containsKey("merchant")){
                    merchantId= (Integer) headerParamJson.get("merchant");

                }
                if(headerParamJson.containsKey("status")){
                    /*
                        To perform case insensitive comparision
                     */
                    statusRequired=((String) headerParamJson.get("status")).toLowerCase();
//                    if(whereQuery!=""){
//                        whereQuery+=" and ";
//                    }
                    whereQuery+=" and LOWER(status_name)= :statusRequired";
                }
                if(headerParamJson.containsKey("orderId")){
                    try {
                        orderId = Integer.parseInt((String) headerParamJson.get("orderId"));
//                        if(whereQuery!=""){
//                            whereQuery+=" and ";
//                        }
                        whereQuery+=" and o.id_order= :orderId ";
                    }
                    catch (NumberFormatException e){
//                        System.out.println("Invalid order id passed");
                        InvalidInputReplyClass invalidOrderId=new InvalidInputReplyClass(Response.Status.BAD_REQUEST.getStatusCode(),Response.Status.BAD_REQUEST.getReasonPhrase(),"Invalid Order Id passed please check your order id");
                        return invalidOrderId;
                    }
                }
                if(headerParamJson.containsKey("pageNum")){
                    try {
                        currentPageNum=(Integer.parseInt((String) headerParamJson.get("pageNum")));
                        offset = ((Integer.parseInt((String) headerParamJson.get("pageNum")))-1)*20;
                        if(offset<0){
                            InvalidInputReplyClass invalidPageNum=new InvalidInputReplyClass(Response.Status.BAD_REQUEST.getStatusCode(),Response.Status.BAD_REQUEST.getReasonPhrase(),"Please enter pageNum as a valid integer number greater then 0");
                            return invalidPageNum;
                        }
                    }
                    catch (NumberFormatException num){
                        InvalidInputReplyClass invalidPageNum=new InvalidInputReplyClass(Response.Status.BAD_REQUEST.getStatusCode(),Response.Status.BAD_REQUEST.getReasonPhrase(),"Please enter pagenum as a valid integer number");
                        return invalidPageNum;
                    }
                }
                else {
                    InvalidInputReplyClass noPageNum=new InvalidInputReplyClass(Response.Status.PARTIAL_CONTENT.getStatusCode(),Response.Status.PARTIAL_CONTENT.getReasonPhrase(),"Please mention pageNum in input parameter as this api can contain large ouput set");
                    return noPageNum;
                }
                //System.out.println("query :"+whereQuery);
                Integer totalResultCount=orderDao.getOrderCount(whereQuery,fromDate,toDate,statusRequired,orderId);
                if(offset>totalResultCount){
                    InvalidInputReplyClass invalidPageNumber=new InvalidInputReplyClass(Response.Status.BAD_REQUEST.getStatusCode(),Response.Status.BAD_REQUEST.getReasonPhrase(),"Page number exceeds limit");
                    return invalidPageNumber;
                }
                List<Order> allOrderDetail=orderDao.getAllOrders(whereQuery,fromDate,toDate,statusRequired,orderId,offset);

                Integer arrayLength=allOrderDetail.size();

                for(int i=0;i<arrayLength;i++){
                    Order orderObj=allOrderDetail.get(i);
                    Integer currentOrderId=orderObj.getOrderId();
                     orderObj.setOrderDetails(orderDao.getSuborderDetail(currentOrderId));
                }

                String currentlyShowing;
                if(offset+20>totalResultCount) {
                    currentlyShowing=offset + " - " +totalResultCount;
                }
                else {
                    currentlyShowing = offset + " - " + (offset + 20);
                }
                int totalPages = totalResultCount/20;
                PaginationReply orderPaginationResult=new PaginationReply(Response.Status.OK.getStatusCode(),Response.Status.OK.getReasonPhrase(),totalResultCount,totalPages,currentPageNum,currentlyShowing,allOrderDetail);
                return orderPaginationResult;
        } catch (ParseException e) {
        	log.info(e.getMessage()+" in order resource, function name is getAllOrders");
            //e.printStackTrace();
//            System.out.println("Exception occured while parsing json string");
            InvalidInputReplyClass invalidJSON=new InvalidInputReplyClass(Response.Status.BAD_REQUEST.getStatusCode(),Response.Status.BAD_REQUEST.getReasonPhrase(),"Input is not valid");
            return invalidJSON;
        }
        catch (NullPointerException npe){
        	log.info(npe.getMessage()+" in order resource, function name is getAllOrders");
            //npe.printStackTrace();
            InvalidInputReplyClass nullPointer=new InvalidInputReplyClass(Response.Status.BAD_REQUEST.getStatusCode(),Response.Status.BAD_REQUEST.getReasonPhrase(),"Null Value Passed");
            return nullPointer;
        }
        catch (Exception e) {
            e.printStackTrace();
            InvalidInputReplyClass serverError=new InvalidInputReplyClass(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(),Response.Status.INTERNAL_SERVER_ERROR.getReasonPhrase(),"Server error occured while serving the request");
            return serverError;
        }

    }
    
    @PUT
    @Path("/updateOrderStatus/{subOrderId}")
    @Produces({ MediaType.APPLICATION_JSON })
    @Timed
    public Object updateOrderStatus(@HeaderParam("accessParam") String headerParam,@PathParam("subOrderId") String subOrderId){
    	try{
    		if(!helper.isValidJson(headerParam)){
    			log.info("Invalid header json provided to access updateOrderStatus function");
    			InvalidInputReplyClass invalidRequestReply = new InvalidInputReplyClass(Response.Status.BAD_REQUEST.getStatusCode(), Response.Status.BAD_REQUEST.getReasonPhrase(), "Header data is invalid json/You may have not passed header details");
				return invalidRequestReply;
    		}
    		if(!isHavingValidKeys(headerParam)){
    			log.info("Invalid header json provided to access updateOrderStatus function");
    			InvalidInputReplyClass invalidRequestReply = new InvalidInputReplyClass(Response.Status.BAD_REQUEST.getStatusCode(), Response.Status.BAD_REQUEST.getReasonPhrase(), "Header data is invalid json/You may have not passed valid header keys");
				return invalidRequestReply;
    		}
    		JSONObject headerParamJson = (JSONObject) new JSONParser().parse(headerParam);
            String userName = (String) headerParamJson.get("userName");
			String accessToken = (String) headerParamJson.get("accessToken");
//            try{
//            	authenticate.validate(userName,accessToken, "order", "put", "updateOrderStatus");
//            }catch(Exception e){
//            	log.info("Unautherized user "+userName+" tried to access updateOrderStatus function");
//            	InvalidInputReplyClass invalidRequestReply = new InvalidInputReplyClass(Response.Status.UNAUTHORIZED.getStatusCode(), Response.Status.UNAUTHORIZED.getReasonPhrase(), e.getMessage());
//				return invalidRequestReply;
//            }
    		String updatedOrderStatus = (String) headerParamJson.get("orderStatus");
           	//int updatedStatus = Integer.parseInt(updatedOrderStatus);
        	UpdateRequest updateRequest = new UpdateRequest();
        	updateRequest.index("order-status");
        	updateRequest.type("retail");
        	updateRequest.id(subOrderId);
        	if(updatedOrderStatus == null)
        		updateRequest.doc(jsonBuilder().startObject().field("id_current_order_detail_status", updatedOrderStatus).endObject());
        	else
        		updateRequest.doc(jsonBuilder().startObject().field("id_current_order_detail_status", Integer.parseInt(updatedOrderStatus)).endObject());
        	if(client.update(updateRequest).get().getShardInfo().getSuccessful() == 1)
        		return new Reply(Response.Status.CREATED.getStatusCode(), Response.Status.CREATED.getReasonPhrase(), null );
        	else
        		return new Reply(Response.Status.NOT_MODIFIED.getStatusCode(), Response.Status.NOT_MODIFIED.getReasonPhrase(),null );
    	}catch (NullPointerException e){
        	log.info(e.getMessage()+" in order resource, function name is updateOrderStatus");
            InvalidInputReplyClass nullPointer=new InvalidInputReplyClass(Response.Status.BAD_REQUEST.getStatusCode(),Response.Status.BAD_REQUEST.getReasonPhrase(),"Null Value Passed");
            return nullPointer;
        }catch (Exception e) {
        	e.printStackTrace();
            InvalidInputReplyClass serverError=new InvalidInputReplyClass(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(),Response.Status.INTERNAL_SERVER_ERROR.getReasonPhrase(),"Server error occured while serving the request");
            return serverError;
        }   	
    }

	/**
	 * Checks if header is having basic valid keys 
	 * @param headerParam
	 * @return boolean
	 */
	private boolean isHavingValidKeys(String headerParam) {
		JSONObject jsonData = helper.jsonParser(headerParam);
		if(jsonData.containsKey("userName") && jsonData.containsKey("accessToken") && jsonData.containsKey("orderStatus"))
			return true;
		else
			return false;
	}    
 }
