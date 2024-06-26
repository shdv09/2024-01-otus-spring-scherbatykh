package ru.otus.hw.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.otus.hw.dto.response.AuthorDto;
import ru.otus.hw.dto.mappers.AuthorMapper;
import ru.otus.hw.repositories.AuthorRepository;

@RequiredArgsConstructor
@Service
public class AuthorServiceImpl implements AuthorService {
    private final AuthorRepository authorRepository;

    private final AuthorMapper authorMapper;

    @Transactional(readOnly = true)
    @Override
    public Flux<AuthorDto> findAll() {
        return authorRepository.findAll()
                .map(authorMapper::toDto);
    }

    @Override
    public Mono<AuthorDto> findById(String authorId) {
        return authorRepository.findById(authorId)
                .map(authorMapper::toDto);
    }
}
