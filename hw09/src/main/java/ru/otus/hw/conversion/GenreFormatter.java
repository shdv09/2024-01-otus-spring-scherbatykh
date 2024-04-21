package ru.otus.hw.conversion;

import lombok.RequiredArgsConstructor;
import org.springframework.format.Formatter;
import ru.otus.hw.dto.GenreDto;
import ru.otus.hw.services.GenreService;

import java.util.Locale;

@RequiredArgsConstructor
public class GenreFormatter implements Formatter<GenreDto> {

    private final GenreService genreService;

    public GenreDto parse(final String text, final Locale locale) {
        final long genreId = Long.parseLong(text);
        return genreService.findById(genreId);
    }

    public String print(final GenreDto object, final Locale locale) {
        return (object != null ? object.toString() : "");
    }
}
