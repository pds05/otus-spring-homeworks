package ru.otus.hw.models.mongo;

public interface MongoDoc {

    String buildId();

    String getId();

    void setId(String id);
}
