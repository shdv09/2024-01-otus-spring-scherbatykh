package ru.otus.hw.controller;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import ru.otus.hw.controllers.BookController;
import ru.otus.hw.dto.request.BookCreateDto;
import ru.otus.hw.dto.request.BookUpdateDto;
import ru.otus.hw.dto.response.AuthorDto;
import ru.otus.hw.dto.response.BookDto;
import ru.otus.hw.dto.response.GenreDto;
import ru.otus.hw.security.SecurityConfig;
import ru.otus.hw.services.AuthorService;
import ru.otus.hw.services.BookService;
import ru.otus.hw.services.GenreService;

import java.util.List;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrlPattern;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@WebMvcTest(BookController.class)
@Import({SecurityConfig.class})
public class BookControllerTest {
    @Autowired
    private MockMvc mvc;

    private BookDto bookDto;

    private BookCreateDto bookCreateDto;

    private BookUpdateDto bookUpdateDto;

    @MockBean
    private BookService bookService;

    @MockBean
    private AuthorService authorService;

    @MockBean
    private GenreService genreService;

    @BeforeEach
    void init() {
        AuthorDto authorDto = new AuthorDto(1L, "author");
        GenreDto genreDto = new GenreDto(2L, "genre");
        bookDto = new BookDto(3L, "book", authorDto, List.of(genreDto), null);
        bookCreateDto = new BookCreateDto(3L, "book", 1L, Set.of(2L));
        bookUpdateDto = new BookUpdateDto(3L, "book", 1L, Set.of(2L));
    }

    @AfterEach
    void after() {
        verifyNoMoreInteractions(bookService, authorService, genreService);
    }

    @WithMockUser(username = "user")
    @Test
    void bookListPositiveTest() throws Exception {
        given(bookService.findAll()).willReturn(List.of(bookDto));

        mvc.perform(get("/"))
                .andExpect(status().isOk()).andDo(print())
                .andExpect(view().name("booksList"));

        verify(bookService).findAll();
    }

    @Test
    void bookListUnauthorizedTest() throws Exception {
        mvc.perform(get("/"))
                .andExpect(status().isFound())
                .andExpect(redirectedUrlPattern("**/login"))
                .andDo(print());
    }

    @WithMockUser(username = "user")
    @Test
    void bookEditPagePositiveTest() throws Exception {
        given(bookService.findById(3L)).willReturn(bookDto);

        mvc.perform(get("/book/3"))
                .andExpect(status().isOk()).andDo(print())
                .andExpect(view().name("bookEdit"));

        verify(bookService).findById(3L);
        verify(authorService).findAll();
        verify(genreService).findAll();
    }

    @Test
    void BookEditPageUnauthorizedTest() throws Exception {
        mvc.perform(get("/book/3"))
                .andExpect(status().isFound())
                .andExpect(redirectedUrlPattern("**/login"))
                .andDo(print());
    }

    @WithMockUser(username = "user")
    @Test
    void saveNewBookPositiveTest() throws Exception {
        bookDto.setId(0L);
        given(bookService.create(any())).willReturn(bookDto);

        mvc.perform(post("/book")
                        .flashAttr("book", bookCreateDto))
                .andExpect(status().isFound()).andDo(print())
                .andExpect(view().name("redirect:/"));

        verify(bookService).create(any());
    }

    @Test
    void saveNewBookUnauthorizedTest() throws Exception {
        mvc.perform(post("/book")
                        .flashAttr("book", bookCreateDto))
                .andExpect(status().isFound())
                .andExpect(redirectedUrlPattern("**/login"))
                .andDo(print());
    }

    @WithMockUser(username = "user")
    @Test
    void editExistedBookPositiveTest() throws Exception {
        given(bookService.update(any())).willReturn(bookDto);

        mvc.perform(post("/book/3")
                        .flashAttr("book", bookUpdateDto))
                .andExpect(status().isFound()).andDo(print())
                .andExpect(view().name("redirect:/"));

        verify(bookService).update(any());
    }

    @Test
    void editExistedBookUnauthorizedTest() throws Exception {
        mvc.perform(post("/book/3")
                        .flashAttr("book", bookUpdateDto))
                .andExpect(status().isFound())
                .andExpect(redirectedUrlPattern("**/login"))
                .andDo(print());
    }

    @WithMockUser(username = "user")
    @Test
    void bookCreatePagePositiveTest() throws Exception {
        mvc.perform(get("/book"))
                .andExpect(status().isOk()).andDo(print())
                .andExpect(view().name("bookEdit"));

        verify(authorService).findAll();
        verify(genreService).findAll();
    }

    @Test
    void bookCreatePageUnauthorizedTest() throws Exception {
        mvc.perform(get("/book"))
                .andExpect(status().isFound())
                .andExpect(redirectedUrlPattern("**/login"))
                .andDo(print());
    }

    @WithMockUser(username = "user")
    @Test
    void deleteBookPositiveTest() throws Exception {
        doNothing().when(bookService).deleteById(3L);

        mvc.perform(post("/book/3/delete")
                        .flashAttr("book", bookDto))
                .andExpect(status().isFound()).andDo(print())
                .andExpect(view().name("redirect:/"));

        verify(bookService).deleteById(3L);
    }

    @Test
    void deleteBookUnauthorizedTest() throws Exception {
        mvc.perform(post("/book/3/delete")
                        .flashAttr("book", bookDto))
                .andExpect(status().isFound())
                .andExpect(redirectedUrlPattern("**/login"))
                .andDo(print());
    }
}
