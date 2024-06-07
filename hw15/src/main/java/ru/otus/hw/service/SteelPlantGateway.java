package ru.otus.hw.service;

import org.springframework.integration.annotation.Gateway;
import org.springframework.integration.annotation.MessagingGateway;
import ru.otus.hw.domain.Iron;
import ru.otus.hw.domain.IronOre;

@MessagingGateway
public interface SteelPlantGateway {
    @Gateway(requestChannel = "steelPlantFlow.input", replyChannel = "ironChannel")
    Iron process(IronOre ironStone);
}
