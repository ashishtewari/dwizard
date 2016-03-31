package com.mebelkart.api.util.cronTasks.Tasks;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mebelkart.api.util.cronTasks.classes.Order;
import com.mebelkart.api.util.cronTasks.dao.OrderDao;
import com.mebelkart.api.util.factories.ElasticFactory;
import de.spinscale.dropwizard.jobs.Job;
import de.spinscale.dropwizard.jobs.annotations.Every;
import de.spinscale.dropwizard.jobs.annotations.OnApplicationStart;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.Client;

import java.sql.ResultSet;
import java.util.Date;

/**
 * Created by vinitpayal on 29/03/16.
 */
@OnApplicationStart
public class OrderIndex extends Job{
    @Override
    public void doJob() {
        Client elasticInstance= ElasticFactory.getElasticClient();

        try {
            /**
             * getting all new orders from mysql which are updated or created after last indexing
             */
            ResultSet ordersResultSet = new OrderDao().getNewOrders();

            ObjectMapper pojoToJsonMapper = new ObjectMapper();
            /**
             * Iterating every order detail and index order detail in elastic
             */
            while (ordersResultSet.next()) {
                Order order=new Order();
                /**
                 * setting order details in order object
                 */
                order.setOrderId(ordersResultSet.getInt("id_order"));
                order.setCustomerId(ordersResultSet.getInt("id_customer"));
                order.setModule(ordersResultSet.getString("module"));
                order.setModule(ordersResultSet.getString("payment"));
                /**
                 * converting order class to json object which can be indexed in elastic
                 */
                byte[] orderJson = pojoToJsonMapper.writeValueAsBytes(order);
                /**
                 * Indexing order json in elastic if order is already existing then
                 * we will update indexed document
                 */
                IndexRequest indexRequest = new IndexRequest("twitter", "tweet", String.valueOf(ordersResultSet.getInt("id_order")))
                        .source(orderJson);
                UpdateRequest updateRequest = new UpdateRequest("twitter", "tweet",String.valueOf(ordersResultSet.getInt("id_order")))
                        .doc(orderJson)
                        .upsert(indexRequest);
                UpdateResponse updateResponse=elasticInstance.update(updateRequest).get();
                /**
                 * Here we will check whether document was succesfuly indexed or not
                 */

            }
        }
        catch (Exception e){
            System.out.println("In ordering index exception occured ");
            e.printStackTrace();
        }

        System.out.println("--------------------------------------");
        System.out.println("Indexing orders called at "+new Date());
        System.out.println("--------------------------------------");
    }
}
