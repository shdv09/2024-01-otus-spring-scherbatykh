package ru.otus.hw.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.otus.hw.models.jpa.AuthorJpa;
import ru.otus.hw.models.mongo.Author;

import java.util.Map;

@RequiredArgsConstructor
@Service
public class AuthorMongoToSqlTransformer {
    private final Map<String, Long> authorsDic;

    private long authorId = 1;

    public AuthorJpa transform(Author author) {
        authorsDic.put(author.getId(), authorId);
        return new AuthorJpa(authorId++, author.getFullName());
    }

    public void cleanUp() {
        authorId = 1;
        authorsDic.clear();
    }
}
