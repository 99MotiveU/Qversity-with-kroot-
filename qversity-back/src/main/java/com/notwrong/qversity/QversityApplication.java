package com.notwrong.qversity;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class QversityApplication {
	public static void main(String[] args) {
		SpringApplication.run(QversityApplication.class, args);
	}
}

