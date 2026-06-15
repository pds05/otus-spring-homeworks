package ru.otus.hw.data;

import lombok.AllArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import ru.otus.hw.models.User;
import ru.otus.hw.security.AppUserDetailsService;

@Component
@AllArgsConstructor
public class UserInitializer implements ApplicationRunner {

    private final AppUserDetailsService userDetailsService;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        System.out.println("!!!!!!!!!!!!!!!! INITIALIZER STARTED!!!!!!!!");
        User user = new User();
        user.setUsername("user");
        user.setPassword("password");
        user.addAuthority("ROLE_USER");
        userDetailsService.save(user);

        User editor = new User();
        editor.setUsername("editor");
        editor.setPassword("password");
        editor.addAuthority("ROLE_EDITOR");
        userDetailsService.save(editor);
    }
}
