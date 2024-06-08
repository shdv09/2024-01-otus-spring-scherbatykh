package ru.otus.hw.service;

import org.springframework.stereotype.Service;
import ru.otus.hw.domain.Iron;
import ru.otus.hw.domain.Steel;

@Service
public class OxygenFurnaceConverter {
    private boolean hasSilicone = false;

    public Steel convert(Iron iron) {
        hasSilicone = !hasSilicone;
        return new Steel(hasSilicone);
    }
}
