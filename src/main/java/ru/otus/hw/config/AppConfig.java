package ru.otus.hw.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.otus.hw.models.mongo.MongoDoc;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Configuration
public class AppConfig {

    @Bean
    public Map<UUID, MongoDoc> mongoCache() {
        return new HashMap<>();
    }
}