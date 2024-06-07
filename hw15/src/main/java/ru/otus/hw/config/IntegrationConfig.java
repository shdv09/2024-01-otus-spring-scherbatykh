package ru.otus.hw.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.MessageChannelSpec;
import org.springframework.integration.dsl.MessageChannels;
import ru.otus.hw.domain.Iron;

@Configuration
public class IntegrationConfig {

    @Bean
    public MessageChannelSpec<?, ?> ironChannel() {
        return MessageChannels.publishSubscribe();
    }

    @Bean
    public IntegrationFlow steelPlantFlow() {
        return f -> f.transform(stone -> new Iron())
                .channel(ironChannel());
    }
}
