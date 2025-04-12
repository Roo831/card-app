package com.poptsov.start;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EnableJpaRepositories(basePackages = {"com.poptsov.core.repository", "com.poptsov.cards.repository"})
@SpringBootApplication(scanBasePackages = {
        "com.poptsov.auth",
        "com.poptsov.core",
        "com.poptsov.cards",
        "com.poptsov.transactions"
})
@EntityScan("com.poptsov.core.model")
public class CardApplication {
    public static void main(String[] args) {
        SpringApplication.run(CardApplication.class, args);
    }
}