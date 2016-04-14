package com.mebelkart.api.util.factories;

import org.elasticsearch.client.Client;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.node.Node;

import static org.elasticsearch.node.NodeBuilder.nodeBuilder;

/**
 * Created by vinitpayal on 29/03/16.
 */
public class ElasticFactory {
    public static Client getElasticClient(){
        try {
            Node node = nodeBuilder().settings(
                    Settings.settingsBuilder()
                            .put("http.enabled", false)
                            .put("path.home", "localhost:9200")
                    //.put("cluster.name", "MK-FRONT-ALL")
            )
                    .client(true)
                    .data(false)
                    .node();
            return node.client();
        }
        catch (Exception e){
            System.out.println("------------In factory----------");
            e.printStackTrace();
            return null;
        }

    }

}