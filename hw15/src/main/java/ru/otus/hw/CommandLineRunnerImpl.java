package ru.otus.hw;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import ru.otus.hw.service.SteelPlant;

@RequiredArgsConstructor
public class CommandLineRunnerImpl implements CommandLineRunner {
    private final SteelPlant steelPlant;

    @Override
    public void run(String... args) throws Exception {
        steelPlant.startProductionCycle();
    }
}
