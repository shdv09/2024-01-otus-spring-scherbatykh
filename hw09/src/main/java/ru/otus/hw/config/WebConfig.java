package ru.otus.hw.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import ru.otus.hw.conversion.AuthorFormatter;
import ru.otus.hw.conversion.GenreFormatter;
import ru.otus.hw.services.AuthorService;
import ru.otus.hw.services.GenreService;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    private final GenreService genreService;

    private final AuthorService authorService;

    @Override
    public void addFormatters(final FormatterRegistry registry) {
        registry.addFormatter(genreFormatter());
        registry.addFormatter(authorFormatter());
    }

    @Bean
    public GenreFormatter genreFormatter() {
        return new GenreFormatter(genreService);
    }

    @Bean
    public AuthorFormatter authorFormatter() {
        return new AuthorFormatter(authorService);
    }
}
