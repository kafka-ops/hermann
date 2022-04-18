package com.purbon.kafka.hermann;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@ConfigurationProperties( prefix = "hermann")
@Configuration("hermannProperties")
@Getter
@Setter
public class HermannConfig {

    private String accessControlProviderClass;

}
