package ru.otus.hw.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.MessageChannelSpec;
import org.springframework.integration.dsl.MessageChannels;
import ru.otus.hw.domain.ConstructionRolledSteel;
import ru.otus.hw.domain.DynamoRolledSteel;
import ru.otus.hw.domain.Iron;
import ru.otus.hw.domain.Steel;
import ru.otus.hw.service.OxygenFurnaceConverter;

@Configuration
public class IntegrationConfig {

    @Bean
    public MessageChannelSpec<?, ?> resultChannel() {
        return MessageChannels.publishSubscribe();
    }

    @Bean
    public MessageChannelSpec<?, ?> dynamoSteelChannel() {
        return MessageChannels.direct();
    }

    @Bean
    public MessageChannelSpec<?, ?> constructionSteelChannel() {
        return MessageChannels.direct();
    }

    @Bean
    public IntegrationFlow steelPlantFlow(OxygenFurnaceConverter converter) {
        return f -> f.transform(ore -> new Iron())
                .<Iron, Steel>transform(converter::convert)
                .<Steel, Boolean>route(Steel::getHasSilicone, mapping -> mapping
                        .subFlowMapping(true, sf -> sf.channel(dynamoSteelChannel()))
                        .subFlowMapping(false, sf -> sf.channel(constructionSteelChannel())));
    }

    @Bean
    public IntegrationFlow dynamoSteelFlow() {
        return f -> f.channel(dynamoSteelChannel())
                .<Steel, Steel>transform(steel -> new DynamoRolledSteel())
                .channel(resultChannel());
    }

    @Bean
    public IntegrationFlow constructionSteelFlow() {
        return f -> f.channel(constructionSteelChannel())
                .<Steel, Steel>transform(steel -> new ConstructionRolledSteel())
                .channel(resultChannel());
    }
}
