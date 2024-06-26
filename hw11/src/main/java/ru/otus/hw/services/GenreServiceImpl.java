package ru.otus.hw.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.otus.hw.dto.response.GenreDto;
import ru.otus.hw.dto.mappers.GenreMapper;
import ru.otus.hw.repositories.GenreRepository;

@RequiredArgsConstructor
@Service
public class GenreServiceImpl implements GenreService {
    private final GenreRepository genreRepository;

    private final GenreMapper genreMapper;

    @Transactional(readOnly = true)
    @Override
    public Flux<GenreDto> findAll() {
        return genreRepository.findAll()
                .map(genreMapper::toDto);
    }

    @Override
    public Mono<GenreDto> findById(String genreId) {
        return genreRepository.findById(genreId)
                .map(genreMapper::toDto);
    }
}
