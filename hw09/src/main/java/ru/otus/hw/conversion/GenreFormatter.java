package ru.otus.hw.conversion;

import lombok.RequiredArgsConstructor;
import org.springframework.format.Formatter;
import ru.otus.hw.dto.response.GenreDto;
import ru.otus.hw.services.GenreService;

import java.util.Locale;

@RequiredArgsConstructor
public class GenreFormatter implements Formatter<GenreDto> {

    private final GenreService genreService;

    public GenreDto parse(final String text, final Locale locale) {
        final long genreId = Long.parseLong(text);
        return genreService.findById(genreId);
    }

    public String print(final GenreDto genre, final Locale locale) {
        return String.valueOf(genre.getId());
    }
}
