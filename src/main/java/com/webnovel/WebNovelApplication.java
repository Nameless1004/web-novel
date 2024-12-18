package com.webnovel;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class WebNovelApplication {

	public static void main(String[] args) {
		SpringApplication.run(WebNovelApplication.class, args);
	}

}
