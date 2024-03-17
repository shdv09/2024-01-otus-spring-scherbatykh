package ru.otus.hw.dto.converters;

import org.springframework.stereotype.Component;
import ru.otus.hw.dto.AuthorDto;
import ru.otus.hw.models.Author;

@Component
public class AuthorDtoConverter {
    public AuthorDto toDto(Author author) {
        if (author == null) {
            return null;
        }
        AuthorDto result = new AuthorDto();
        result.setId(author.getId());
        result.setFullName(author.getFullName());
        return result;
    }

    public Author fromDto(AuthorDto dto) {
        if (dto == null) {
            return null;
        }
        Author result = new Author();
        result.setId(dto.getId());
        result.setFullName(dto.getFullName());
        return result;
    }
}
