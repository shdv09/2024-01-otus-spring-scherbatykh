package ru.otus.hw.repositories;

import ru.otus.hw.dto.GenreDto;

import java.util.List;
import java.util.Set;

public interface GenreRepository {
    List<GenreDto> findAll();

    List<GenreDto> findAllByIds(Set<Long> ids);
}
