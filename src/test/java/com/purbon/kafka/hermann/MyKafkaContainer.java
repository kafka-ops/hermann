package com.purbon.kafka.hermann;

import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.utility.DockerImageName;

public class MyKafkaContainer extends KafkaContainer {

    private static final DockerImageName DEFAULT_IMAGE_NAME = DockerImageName.parse("confluentinc/cp-kafka");
    private static MyKafkaContainer container;

    private MyKafkaContainer() {
        super(DEFAULT_IMAGE_NAME.withTag("6.2.0"));
        withNetworkAliases("kafka");
    }

    public static MyKafkaContainer getInstance() {
       if (container == null) {
           container = new MyKafkaContainer();
       }
       return container;
    }
}
