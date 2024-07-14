package ru.otus.hw.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.otus.hw.dto.request.BookCreateDto;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Genre;
import ru.otus.hw.repositories.AuthorRepository;
import ru.otus.hw.repositories.BookRepository;
import ru.otus.hw.repositories.CommentRepository;
import ru.otus.hw.repositories.GenreRepository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class CircuitBreakerTest {
    private static final ObjectMapper MAPPER = new ObjectMapper();

    @Autowired
    private MockMvc mvc;

    private Book book;

    private BookCreateDto bookCreateDto;

    @MockBean
    private BookRepository bookRepository;

    @MockBean
    private CommentRepository commentRepository;

    @MockBean
    private AuthorRepository authorRepository;

    @MockBean
    private GenreRepository genreRepository;

    @BeforeEach
    void init() {
        Author author = new Author(1L, "author");
        Genre genre = new Genre(2L, "genre");
        book = new Book(3L, "book", author, List.of(genre));
        bookCreateDto = new BookCreateDto("book", 1L, Set.of(2L));
    }

    @AfterEach
    void after() {
        verifyNoMoreInteractions(bookRepository, commentRepository, authorRepository, genreRepository);
    }

    @Test
    void saveNewBookCircuitBreakerTest() throws Exception {
        given(authorRepository.findById(anyLong())).willReturn(Optional.of(this.book.getAuthor()));
        given(genreRepository.findAllById(anySet())).willReturn(this.book.getGenres());
        given(bookRepository.save(any())).willThrow(RuntimeException.class);

        for (int i = 0; i < 5; i++) {
            mvc.perform(post("/api/book")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(MAPPER.writeValueAsString(bookCreateDto)))
                    .andExpect(status().isInternalServerError()).andDo(print());
        }
        for (int i = 0; i < 5; i++) {
            mvc.perform(post("/api/book")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(MAPPER.writeValueAsString(bookCreateDto)))
                    .andExpect(status().isServiceUnavailable()).andDo(print());
        }

        verify(authorRepository, times(5)).findById(anyLong());
        verify(genreRepository, times(5)).findAllById(anySet());
        verify(bookRepository, times(5)).save(any());
    }
}
