package ru.otus.hw.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.otus.hw.domain.IronOre;
import ru.otus.hw.domain.Steel;

import java.util.LinkedList;
import java.util.List;

@RequiredArgsConstructor
@Service
@Slf4j
public class SteelPlantImpl implements SteelPlant {
    private final SteelPlantGateway steelPlantGateway;

    @Override
    public void startProductionCycle() {
        List<Steel> result = new LinkedList<>();
        for (int i = 0; i < 10; i++) {
            var steel = steelPlantGateway.process(new IronOre());
            result.add(steel);
        }
        log.info("Result of production cycle: {}", result);
    }
}
