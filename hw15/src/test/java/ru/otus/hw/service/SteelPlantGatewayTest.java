package ru.otus.hw.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.otus.hw.domain.ConstructionRolledSteel;
import ru.otus.hw.domain.DynamoRolledSteel;
import ru.otus.hw.domain.IronOre;
import ru.otus.hw.domain.RolledSteel;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
public class SteelPlantGatewayTest {
    @Autowired
    SteelPlantGateway steelPlantGateway;

    @Test
    void test() {
        RolledSteel resultOne = steelPlantGateway.process(new IronOre());
        RolledSteel resultTwo = steelPlantGateway.process(new IronOre());

        assertNotNull(resultOne);
        assertNotNull(resultTwo);
        assertTrue(resultOne instanceof DynamoRolledSteel);
        assertTrue(resultTwo instanceof ConstructionRolledSteel);
    }
}
