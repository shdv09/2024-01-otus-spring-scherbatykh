package ru.otus.hw.conversion;

import lombok.RequiredArgsConstructor;
import org.springframework.format.Formatter;
import ru.otus.hw.dto.response.AuthorDto;
import ru.otus.hw.services.AuthorService;

import java.util.Locale;

@RequiredArgsConstructor
public class AuthorFormatter implements Formatter<AuthorDto> {

    private final AuthorService authorService;

    public AuthorDto parse(final String text, final Locale locale) {
        final long authorId = Long.parseLong(text);
        return authorService.findById(authorId);
    }

    public String print(final AuthorDto author, final Locale locale) {
        return String.valueOf(author.getId());
    }
}
