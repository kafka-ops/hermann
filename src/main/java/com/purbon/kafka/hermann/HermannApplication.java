package com.purbon.kafka.hermann;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan("com.purbon.kafka")
@EntityScan("com.purbon.kafka")
public class HermannApplication {

	public static void main(String[] args) {
		SpringApplication.run(HermannApplication.class, args);
	}

}
