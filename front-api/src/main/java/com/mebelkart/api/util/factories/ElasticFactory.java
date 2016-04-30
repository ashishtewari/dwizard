package com.mebelkart.api.util.factories;

import java.net.InetAddress;

import com.mebelkart.api.mkApiConfiguration;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;

/**
 * Created by vinitpayal on 29/03/16.
 */
public class ElasticFactory {

    //Singleton elastic factory
    private ElasticFactory(){};

    public static Client getElasticClient(){
        try {     
        	/*
        	 *	Transport client
        	 */
            System.out.println("elastic host from config :"+mkApiConfiguration.getElasticsearchHost());
            System.out.println("elastic cluster name :"+mkApiConfiguration.getClusterName());
            System.out.println("elastic port "+mkApiConfiguration.getElasticPort());
            Client client = TransportClient.builder().build()
         	       .addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName(mkApiConfiguration.getElasticsearchHost()), mkApiConfiguration.getElasticPort()));
        	return client;
        }
        catch (Exception e){
            System.out.println("------------In factory----------");
            e.printStackTrace();
            return null;
        }

    }
    public static Client getProductsElasticClient(){
        try {       	
        	/*
        	 *	Transport client
        	 */        	
        	Settings settings = Settings.settingsBuilder()
        	        .put("cluster.name", mkApiConfiguration.getProductClusterName())
        	        .build();
        	Client client = TransportClient.builder().settings(settings).build()
        			.addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName(mkApiConfiguration.getProductElasticsearchHost()), mkApiConfiguration.getProductElasticPort()));
        	return client;
        }
        catch (Exception e){
            System.out.println("------------In remote factory----------");
            e.printStackTrace();
            return null;
        }

    }

}