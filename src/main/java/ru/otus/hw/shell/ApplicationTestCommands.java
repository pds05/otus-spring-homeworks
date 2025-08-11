package ru.otus.hw.shell;

import lombok.RequiredArgsConstructor;
import org.springframework.shell.Availability;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellMethodAvailability;
import org.springframework.shell.standard.ShellOption;
import ru.otus.hw.security.LoginContext;
import ru.otus.hw.service.TestRunnerService;

@ShellComponent
@RequiredArgsConstructor
public class ApplicationTestCommands {

    private final LoginContext loginContext;

    private final TestRunnerService testRunnerService;

    @ShellMethod(value = "login command", key = {"l", "login"})
    public String login(@ShellOption(defaultValue = "defaultUser") String username) {
        loginContext.login(username);
        return String.format("Logged in user %s", username);
    }

    @ShellMethod(value = "starting test", key = {"s", "start"})
    @ShellMethodAvailability(value = "isCommandAvailable")
    public void start() {
        testRunnerService.run();
    }

    private Availability isCommandAvailable() {
        return loginContext.isUserLoggedIn() ? Availability.available() : Availability.unavailable("Not logged in");
    }
}
