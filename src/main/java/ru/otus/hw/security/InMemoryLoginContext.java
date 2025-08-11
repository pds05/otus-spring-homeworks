package ru.otus.hw.security;

import org.springframework.stereotype.Component;

@Component
public class InMemoryLoginContext implements LoginContext {

    private String username;

    @Override
    public void login(String username) {
        this.username = username;

    }

    @Override
    public boolean isUserLoggedIn() {
        return username != null;
    }
}
