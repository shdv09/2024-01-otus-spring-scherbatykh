package ru.otus.hw.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.otus.hw.models.jpa.GenreJpa;
import ru.otus.hw.models.mongo.Genre;

import java.util.Map;

@RequiredArgsConstructor
@Service
public class GenreMongoToSqlTransformer {
    private final Map<String, Long> genresDic;

    private long genreId = 1;

    public GenreJpa transform(Genre genre) {
        genresDic.put(genre.getId(), genreId);
        return new GenreJpa(genreId++, genre.getName());
    }

    public void cleanUp() {
        genreId = 1;
        genresDic.clear();
    }
}
