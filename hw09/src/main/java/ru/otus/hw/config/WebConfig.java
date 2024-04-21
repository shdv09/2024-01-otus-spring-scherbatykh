package ru.otus.hw.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import ru.otus.hw.conversion.GenreFormatter;
import ru.otus.hw.services.GenreService;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    private final GenreService genreService;

    @Override
    public void addFormatters(final FormatterRegistry registry) {
        registry.addFormatter(genreFormatter());
    }

    @Bean
    public GenreFormatter genreFormatter() {
        return new GenreFormatter(genreService);
    }
}
