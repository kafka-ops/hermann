package com.purbon.kafka.hermann.managers;

import com.purbon.kafka.hermann.AppConfig;
import com.purbon.kafka.hermann.HermannApplication;
import com.purbon.kafka.hermann.containers.MyKafkaContainer;
import com.purbon.kafka.hermann.containers.SaslPlaintextKafkaContainer;
import com.purbon.kafka.hermann.controller.request.ArtefactRequest;
import com.purbon.kafka.hermann.controller.request.acl.ConsumerSpec;
import com.purbon.kafka.hermann.controller.response.AclResponse;
import com.purbon.kafka.hermann.storage.AclsRepository;
import com.purbon.kafka.julie.api.JulieAdminClient;
import com.purbon.kafka.julie.model.users.Consumer;
import com.purbon.kafka.julie.roles.SimpleAclsProvider;
import com.purbon.kafka.julie.roles.acls.AclsBindingsBuilder;
import org.apache.kafka.common.resource.ResourceType;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.io.IOException;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = HermannApplication.class)
@DirtiesContext
@Testcontainers
public class AclsManagerTests {

    @Container
    public static SaslPlaintextKafkaContainer kafkaContainer = new SaslPlaintextKafkaContainer();

    @Autowired
    private JulieAdminClient adminClient;

    private AclsBindingsBuilder bindingProvider;
    private SimpleAclsProvider aclsProvider;

    private AccessControlManager<ConsumerSpec, Consumer> aclManager;

    @Autowired
    private AclsRepository<Consumer> aclsRepository;

    @Autowired
    private AppConfig config;

    @BeforeEach
    public void before() {
        aclsProvider = new SimpleAclsProvider(adminClient);
        bindingProvider = new AclsBindingsBuilder(config);
        aclManager = new AccessControlManager<>(aclsProvider, bindingProvider, aclsRepository);
    }

    @Test
    public void shouldCreateNewAcls() throws IOException {

        var namespace = "name.space";
        var name = "foo";

        ArtefactRequest<ConsumerSpec> request = new ArtefactRequest<>();
        request.setNamespace(namespace);
        request.setName(name);

        ConsumerSpec spec = new ConsumerSpec();
        spec.setPrincipal("User:foo");
        request.setSpec(spec);

        aclManager.apply(request, name);

        var bindingsMap = adminClient.fetchAclsList();
        var bindings = bindingsMap.get(namespace);
        bindings.addAll(bindingsMap.get("*")); // adding the group resourceType
        assertThat(bindings).hasSize(3);

        var resourceTypes = bindings.stream().map(b -> b.pattern().resourceType()).collect(Collectors.toList());
        assertThat(resourceTypes).contains(ResourceType.TOPIC);
        assertThat(resourceTypes).contains(ResourceType.GROUP);

        var names = bindings.stream().map(b -> b.pattern().name()).collect(Collectors.toList());
        assertThat(names).contains("*");
        assertThat(names).contains(namespace);

        Optional<Consumer> optionalConsumer = aclsRepository.findById(name);
        assertThat(optionalConsumer).isPresent();
        assertThat(optionalConsumer).has(new Condition<>(
                consumer -> consumer.get().getPrincipal().equalsIgnoreCase("User:foo"),
                "principal name exist"));
    }

    @DynamicPropertySource
    public static void properties(DynamicPropertyRegistry registry) {
        var saslString = "org.apache.kafka.common.security.plain.PlainLoginModule required  " +
                "  username=\"kafka\" " +
                "  password=\"kafka\";";
        registry.add("spring.kafka.bootstrap-servers", () -> kafkaContainer.getBootstrapServers());
        registry.add("spring.kafka.properties.security.protocol", () -> "SASL_PLAINTEXT");
        registry.add("spring.kafka.properties.sasl.mechanism", () -> "PLAIN");
        registry.add("spring.kafka.properties.sasl.jaas.config", () -> saslString);
    }
}
