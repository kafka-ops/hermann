package com.purbon.kafka.hermann;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class HermannApplicationTests {

	@Test
	void contextLoads() {
		assertThat(true).isTrue();
	}

}
