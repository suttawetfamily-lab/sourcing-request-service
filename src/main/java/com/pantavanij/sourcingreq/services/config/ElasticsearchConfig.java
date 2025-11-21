package com.pantavanij.sourcingreq.services.config;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.data.elasticsearch.client.RestClients;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.elasticsearch.client.RestHighLevelClient;

@Configuration
public class ElasticsearchConfig {

    @Value("${elasticsearch.host}")
    private String host;

    @Value("${elasticsearch.port}")
    private int port;

    @Value("${elasticsearch.scheme:http}")
    private String scheme;

    @Bean
    public RestHighLevelClient client() {
        String connection = host + ":" + port;

        ClientConfiguration clientConfiguration = ClientConfiguration.builder()
                .connectedTo(connection)
                .build();

        return RestClients.create(clientConfiguration).rest();
    }

    @Bean
    public ElasticsearchOperations elasticsearchTemplate() {
        return new ElasticsearchRestTemplate(client());
    }
}

