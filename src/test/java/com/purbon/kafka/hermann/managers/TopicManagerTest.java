package com.purbon.kafka.hermann.managers;

import com.purbon.kafka.hermann.HermannApplication;
import com.purbon.kafka.hermann.MyKafkaContainer;
import com.purbon.kafka.hermann.MySQLContainer;
import com.purbon.kafka.hermann.api.HermannAdminClient;
import com.purbon.kafka.hermann.controller.request.ArtefactRequest;
import com.purbon.kafka.hermann.controller.request.TopicSpec;
import com.purbon.kafka.hermann.storage.TopicRepository;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.util.Collections;
import java.util.function.Supplier;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = HermannApplication.class)
@ContextConfiguration(classes = HermannApplication.class)
public class TopicManagerTest {

    @ClassRule
    public static MySQLContainer mySQLContainer = MySQLContainer.getInstance();

    @ClassRule
    public static MyKafkaContainer kafkaContainer = MyKafkaContainer.getInstance();

    @Autowired
    private KafkaProperties properties;

    @Autowired
    private TopicRepository topicRepository;

    @Autowired
    private HermannAdminClient adminClient;

    @Test
    public void shouldCreateNewTopics() throws IOException {

        var topicManager = new TopicManager(topicRepository, adminClient);
        var name = "foo";

        var request = new ArtefactRequest<TopicSpec>();
        request.setNamespace("name.space");

        var topicSpec = new TopicSpec();
        topicSpec.setName(name);
        topicSpec.setPartitions(1L);
        topicSpec.setReplicationFactor((short) 1);
        topicSpec.setConfig(Collections.singletonMap("retention.ms", "42"));
        request.setSpec(topicSpec);

        var response = topicManager.apply(request, name);
        assertThat(response).hasFieldOrPropertyWithValue("name", "name.space.foo");

    }

    @DynamicPropertySource
    public static void properties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", () -> mySQLContainer.getUrl("hermann"));
        registry.add("spring.datasource.username", () -> "root");
        registry.add("spring.datasource.password", () -> "confluent");
        registry.add("spring.datasource.driver-class-name", () -> "com.mysql.jdbc.Driver");
        registry.add("spring.jpa.database-platform", () -> "org.hibernate.dialect.MySQL5Dialect");
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "update");
        registry.add("spring.kafka.bootstrap-servers", () -> kafkaContainer.getBootstrapServers());
    }
}
