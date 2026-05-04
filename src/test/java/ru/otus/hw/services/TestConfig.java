package ru.otus.hw.services;

import com.mongodb.ReadConcern;
import com.mongodb.ReadPreference;
import com.mongodb.WriteConcern;
import io.mongock.driver.api.driver.ConnectionDriver;
import io.mongock.driver.mongodb.springdata.v4.SpringDataMongoV4Driver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.util.concurrent.TimeUnit;

@TestConfiguration
public class TestConfig {

    @Bean
    public ConnectionDriver connectionDriver(@Autowired MongoTemplate mongoTemplate) {
        SpringDataMongoV4Driver driver = SpringDataMongoV4Driver.withDefaultLock(mongoTemplate);
        driver.setWriteConcern(WriteConcern.MAJORITY.withJournal(false).withWTimeout(1000, TimeUnit.MILLISECONDS));
        driver.setReadConcern(ReadConcern.LOCAL);
        driver.setReadPreference(ReadPreference.primary());
        return driver;
    }
}
