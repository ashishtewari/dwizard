package com.mebelkart.api.util.factories;

import java.net.InetAddress;

import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;

/**
 * Created by vinitpayal on 29/03/16.
 */
public class ElasticFactory {
    public static Client getElasticClient(){
        try {     
        	/*
        	 *	Transport client
        	 */
        	Client client = TransportClient.builder().build()
         	       .addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("127.0.0.1"), 9300));
        	return client;
        }
        catch (Exception e){
            System.out.println("------------In factory----------");
            e.printStackTrace();
            return null;
        }

    }
    public static Client getRemoteElasticClient(){
        try {       	
        	/*
        	 *	Transport client
        	 */        	
        	Settings settings = Settings.settingsBuilder()
        	        .put("cluster.name", "MK-FRONT-ALL")
        	        .build();
        	Client client = TransportClient.builder().settings(settings).build()
        			.addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("52.74.202.15"), 9300));
        	return client;
        }
        catch (Exception e){
            System.out.println("------------In remote factory----------");
            e.printStackTrace();
            return null;
        }

    }

}