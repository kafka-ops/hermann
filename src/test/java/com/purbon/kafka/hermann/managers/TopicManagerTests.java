package com.purbon.kafka.hermann.managers;

import com.purbon.kafka.hermann.HermannApplication;
import com.purbon.kafka.julie.api.JulieAdminClient;
import com.purbon.kafka.hermann.containers.MyKafkaContainer;
import com.purbon.kafka.hermann.controller.request.ArtefactRequest;
import com.purbon.kafka.hermann.controller.request.TopicSpec;
import com.purbon.kafka.hermann.model.Topic;
import com.purbon.kafka.hermann.storage.TopicRepository;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.io.IOException;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = HermannApplication.class)
@DirtiesContext
@Testcontainers
public class TopicManagerTests {

   /* @Container
    public static MySQLContainer mySQLContainer = MySQLContainer.getInstance();
*/
    @Container
    public static MyKafkaContainer kafkaContainer = MyKafkaContainer.getInstance();

    @Autowired
    private KafkaProperties properties;

    @Autowired
    private TopicRepository topicRepository;

    @Autowired
    private JulieAdminClient adminClient;

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

        Optional<Topic> optionalTopic = topicRepository.findById("name.space.foo");
        assertThat(optionalTopic).isPresent();
        assertThat(optionalTopic).has(new Condition<>(
                topic -> topic.get().getName().equalsIgnoreCase("name.space.foo"),
                "name check"
        ));
        assertThat(optionalTopic).has(new Condition<>(
                topic -> topic.get().getPartitions() == 1L,
                "partitions check"
        ));
        assertThat(optionalTopic).has(new Condition<>(
                topic -> topic.get().getReplicationFactor() == 1,
                "replication factor check"
        ));
        assertThat(optionalTopic).has(new Condition<>(
                topic -> topic.get().getConfig().containsKey("retention.ms"),
                "config check"
        ));
    }

    @Test
    public void shouldUpdateAlreadyCreatedTopics() throws IOException {
        var topicManager = new TopicManager(topicRepository, adminClient);
        var name = "foo";
        var namespace = "name.space";
        var fullTopicName = namespace + "." + name;

        var request = makeTopic(namespace, name, Collections.singletonMap("retention.ms", "42"));
        var response = topicManager.apply(request, name);
        assertThat(response).hasFieldOrPropertyWithValue("name", fullTopicName);

        var updateRequest = makeTopic(namespace, name, Collections.singletonMap("retention.ms", "24"));
        topicManager.apply(updateRequest, name);

        Optional<Topic> optionalTopic = topicRepository.findById(fullTopicName);
        assertThat(optionalTopic).has(new Condition<>(
                topic -> topic.get().getConfig().get("retention.ms").equalsIgnoreCase("24"),
                "config check"
        ));

        var config = adminClient.getActualTopicConfig(fullTopicName);
        var configEntry = config.get("retention.ms");
        assertThat(configEntry.value()).isEqualTo("24");
    }

    private ArtefactRequest<TopicSpec> makeTopic(String namespace, String name, Map<String, String> config) {
        var request = new ArtefactRequest<TopicSpec>();
        request.setNamespace(namespace);

        var topicSpec = new TopicSpec();
        topicSpec.setName(name);
        topicSpec.setPartitions(1L);
        topicSpec.setReplicationFactor((short) 1);
        topicSpec.setConfig(config);
        request.setSpec(topicSpec);

        return request;
    }

    @DynamicPropertySource
    public static void properties(DynamicPropertyRegistry registry) {
     /*   registry.add("spring.datasource.url", () -> mySQLContainer.getUrl("hermann"));
        registry.add("spring.datasource.username", () -> "root");
        registry.add("spring.datasource.password", () -> "confluent");
        registry.add("spring.datasource.driver-class-name", () -> "com.mysql.jdbc.Driver");
        registry.add("spring.jpa.database-platform", () -> "org.hibernate.dialect.MySQL5Dialect");
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "update");
       */ registry.add("spring.kafka.bootstrap-servers", () -> kafkaContainer.getBootstrapServers());
    }
}
