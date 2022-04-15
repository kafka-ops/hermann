package com.purbon.kafka.hermann;

import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.KafkaAdminClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Properties;

@Configuration
public class AppConfig {

    private KafkaProperties kafkaProperties;

    public AppConfig(KafkaProperties kafkaProperties) {
        this.kafkaProperties = kafkaProperties;
    }
    public String getTopicPrefixSeparator() {
        return ".";
    }

    @Bean
    public AdminClient adminClient() {
        Properties props = new Properties();
        kafkaProperties.buildAdminProperties().forEach(props::put);
        return KafkaAdminClient.create(props);
    }
}
