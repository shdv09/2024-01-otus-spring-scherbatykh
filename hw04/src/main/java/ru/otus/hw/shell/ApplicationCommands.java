package ru.otus.hw.shell;

import lombok.RequiredArgsConstructor;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import ru.otus.hw.service.TestRunnerService;

@ShellComponent(value = "Test App Commands")
@RequiredArgsConstructor
public class ApplicationCommands {
    private final TestRunnerService testRunnerService;

    @ShellMethod(value = "Start Test Command", key = {"s", "start"})
    public void startTest() {
        testRunnerService.run();
    }
}
