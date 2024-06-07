package ru.otus.hw.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.otus.hw.domain.Iron;
import ru.otus.hw.domain.IronOre;

@RequiredArgsConstructor
@Service
@Slf4j
public class SteelPlantImpl implements SteelPlant {
    private final SteelPlantGateway steelPlantGateway;

    @Override
    public void startProductionCycle() {
        Iron result = steelPlantGateway.process(new IronOre());
        log.info("Result of production cycle: {}", result);
    }
}
