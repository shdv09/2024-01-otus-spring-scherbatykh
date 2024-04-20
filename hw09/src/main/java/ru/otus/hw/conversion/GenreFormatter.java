package ru.otus.hw.conversion;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.Formatter;
import ru.otus.hw.dto.GenreDto;
import ru.otus.hw.services.GenreService;

import java.util.Locale;


public class GenreFormatter implements Formatter<GenreDto> {

    @Autowired
    private GenreService genreService;


    public GenreFormatter() {
        super();
    }

    public GenreDto parse(final String text, final Locale locale) {
        final long genreId = Long.parseLong(text);
        GenreDto genreDto = genreService.findById(genreId);
        return genreDto;
    }


    public String print(final GenreDto object, final Locale locale) {
        return (object != null ? object.toString() : "");
    }

}
