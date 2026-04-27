package ru.otus.hw.services;

import io.mongock.driver.api.driver.ConnectionDriver;
import io.mongock.driver.mongodb.springdata.v4.SpringDataMongoV4Driver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.data.mongodb.core.MongoTemplate;

@TestConfiguration
public class TestConfig {

    @Bean
    public ConnectionDriver connectionDriver(@Autowired MongoTemplate mongoTemplate) {
        return SpringDataMongoV4Driver.withDefaultLock(mongoTemplate);

    }

}
