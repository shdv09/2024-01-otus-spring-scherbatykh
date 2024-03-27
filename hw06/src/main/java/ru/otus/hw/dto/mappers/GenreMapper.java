package ru.otus.hw.dto.mappers;

import org.springframework.stereotype.Component;
import ru.otus.hw.dto.GenreDto;
import ru.otus.hw.models.Genre;

@Component
public class GenreMapper {
    public GenreDto toDto(Genre genre) {
        GenreDto result = new GenreDto();
        result.setId(genre.getId());
        result.setName(genre.getName());
        return result;
    }

    public Genre toModel(GenreDto dto) {
        Genre result = new Genre();
        result.setId(dto.getId());
        result.setName(dto.getName());
        return result;
    }
}
