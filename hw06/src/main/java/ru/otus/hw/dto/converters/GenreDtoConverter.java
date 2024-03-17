package ru.otus.hw.dto.converters;

import org.springframework.stereotype.Component;
import ru.otus.hw.dto.GenreDto;
import ru.otus.hw.models.Genre;

@Component
public class GenreDtoConverter {
    public GenreDto toDto(Genre genre) {
        if (genre == null) {
            return null;
        }
        GenreDto result = new GenreDto();
        result.setId(genre.getId());
        result.setName(genre.getName());
        return result;
    }

    public Genre fromDto(GenreDto dto) {
        if (dto == null) {
            return null;
        }
        Genre result = new Genre();
        result.setId(dto.getId());
        result.setName(dto.getName());
        return result;
    }
}
