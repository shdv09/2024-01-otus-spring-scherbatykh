package ru.otus.hw.services;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.otus.hw.dto.response.GenreDto;

public interface GenreService {
    Flux<GenreDto> findAll();

    Mono<GenreDto> findById(String genreId);
}
