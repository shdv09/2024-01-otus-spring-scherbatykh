package ru.otus.hw.service;

import org.springframework.integration.annotation.Gateway;
import org.springframework.integration.annotation.MessagingGateway;
import ru.otus.hw.domain.IronOre;
import ru.otus.hw.domain.RolledSteel;

@MessagingGateway
public interface SteelPlantGateway {
    @Gateway(requestChannel = "steelPlantFlow.input", replyChannel = "resultChannel")
    RolledSteel process(IronOre ironOre);
}
