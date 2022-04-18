package com.purbon.kafka.hermann;

import com.purbon.kafka.julie.api.JulieAdminClient;
import lombok.NoArgsConstructor;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.KafkaAdminClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Properties;

@Configuration
@NoArgsConstructor
public class AppConfig {

    @Autowired
    private KafkaProperties kafkaProperties;

    @Autowired
    private HermannConfig hermannConfig;

    public AppConfig(KafkaProperties kafkaProperties, HermannConfig hermannConfig) {
        this.kafkaProperties = kafkaProperties;
        this.hermannConfig = hermannConfig;
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

    public String getTopicPrefixFormat() {
        return "";
    }

    public boolean enabledConnectorTopicCreateAcl() {
        return false;
    }

    public String getConfluentMonitoringTopic() {
        return null;
    }

    public String getConfluentCommandTopic() {
        return null;
    }

    public String getConfluentMetricsTopic() {
        return null;
    }

    @Bean
    public AccessControlProviderFactoryBean accessControlProviderFactoryBean() {
       var julieAdminClient = new JulieAdminClient(adminClient());
       AccessControlProviderFactoryBean factoryBean = new AccessControlProviderFactoryBean(julieAdminClient);
       factoryBean.setAccessControlProviderClass(hermannConfig.getAccessControlProviderClass());
       return factoryBean;
    }

    @Bean
    public BindingsProviderFactoryBean bindingsProviderFactoryBean() {
        BindingsProviderFactoryBean factoryBean = new BindingsProviderFactoryBean(this);
        factoryBean.setAccessControlProviderClass(hermannConfig.getAccessControlProviderClass());
        return factoryBean;
    }
}
