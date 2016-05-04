package com.mebelkart.api.util.cronTasks.Tasks;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mebelkart.api.util.cronTasks.classes.ProductQuestion;
import com.mebelkart.api.util.cronTasks.dao.ProductsQuestionDao;
import com.mebelkart.api.util.factories.ElasticFactory;
import de.spinscale.dropwizard.jobs.Job;
import de.spinscale.dropwizard.jobs.annotations.OnApplicationStart;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.Client;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

/**
 * Created by vinitpayal on 03/05/16.
 */
@OnApplicationStart
public class ProductQuestionsIndex extends Job{
    @Override
    public void doJob() {
        try {
            Client elasticInstance= ElasticFactory.getElasticClient();
            ResultSet productsQuestionResultSet=new ProductsQuestionDao().getProductsQuestion();
            while (productsQuestionResultSet.next()) {
                try {
                    ObjectMapper pojoToJsonMapper = new ObjectMapper();
                    ProductQuestion productQuestion = new ProductQuestion(productsQuestionResultSet.getString("id_qna"), productsQuestionResultSet.getString("question"), productsQuestionResultSet.getString("email")
                            , productsQuestionResultSet.getString("name"), productsQuestionResultSet.getString("id_product")
                            , productsQuestionResultSet.getInt("approved"), productsQuestionResultSet.getString("date_added"), productsQuestionResultSet.getString("answer"));
                    byte[] productQnaJSON = pojoToJsonMapper.writeValueAsBytes(productQuestion);

                    IndexRequest indexRequest = new IndexRequest("product", "faq", productsQuestionResultSet.getString("id_qna"))
                            .source(productQnaJSON);
                    UpdateRequest updateRequest = new UpdateRequest("product", "faq", productsQuestionResultSet.getString("id_qna"))
                            .doc(productQnaJSON)
                            .upsert(indexRequest);

                    UpdateResponse updateResponse = elasticInstance.update(updateRequest).get();
                    System.out.println("q&a id :"+productsQuestionResultSet.getString("id_qna")+" indexed ");
                }
                catch (Exception e){
                    System.out.println("Exception in indexing product q&a for product id:"+productsQuestionResultSet.getString("id_product")+" and exception is ");
                    e.printStackTrace();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("SqlException occured while q&a indexing received from dao");
        }
        catch (Exception e) {
            e.printStackTrace();
            System.out.println("Generic exception occured while q&a indexing");
        }
    }
}
