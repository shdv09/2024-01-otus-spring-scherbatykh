package ru.otus.hw.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import ru.otus.hw.controllers.api.BookRestController;
import ru.otus.hw.dto.request.BookCreateDto;
import ru.otus.hw.dto.request.BookUpdateDto;
import ru.otus.hw.dto.request.CommentCreateDto;
import ru.otus.hw.dto.response.AuthorDto;
import ru.otus.hw.dto.response.BookDto;
import ru.otus.hw.dto.response.CommentDto;
import ru.otus.hw.dto.response.GenreDto;
import ru.otus.hw.services.AuthorService;
import ru.otus.hw.services.BookService;
import ru.otus.hw.services.CommentService;
import ru.otus.hw.services.GenreService;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookRestController.class)
public class BookRestControllerTest {
    private static final ObjectMapper MAPPER = new ObjectMapper();

    @Autowired
    private MockMvc mvc;

    private BookDto bookDto;

    private BookCreateDto bookCreateDto;

    private BookUpdateDto bookUpdateDto;

    private CommentDto commentDto;

    @MockBean
    private BookService bookService;

    @MockBean
    private AuthorService authorService;

    @MockBean
    private GenreService genreService;

    @MockBean
    private CommentService commentService;

    @BeforeEach
    void init() {
        AuthorDto authorDto = new AuthorDto(1L, "author");
        GenreDto genreDto = new GenreDto(2L, "genre");
        bookDto = new BookDto(3L, "book", authorDto, List.of(genreDto), null);
        bookCreateDto = new BookCreateDto("book", 1L, Set.of(2L));
        bookUpdateDto = new BookUpdateDto(3L, "book", 1L, Set.of(2L));
        commentDto = new CommentDto(4L, "comment text");
    }

    @AfterEach
    void after() {
        verifyNoMoreInteractions(bookService, authorService, genreService, commentService);
    }

    @Test
    void shouldReturnCorrectBookList() throws Exception {
        List<BookDto> daoRes = List.of(this.bookDto);
        given(bookService.findAll()).willReturn(daoRes);

        MvcResult mvcResult = mvc.perform(get("/api/book"))
                .andExpect(status().isOk()).andDo(print())
                .andReturn();
        List<BookDto> response = MAPPER.readValue(mvcResult.getResponse().getContentAsString(), new TypeReference<>(){});

        verify(bookService).findAll();
        assertEquals(daoRes, response);
    }

    @Test
    void shouldSaveNewBook() throws Exception {
        given(bookService.create(any())).willReturn(bookDto);

        MvcResult mvcResult = mvc.perform(post("/api/book")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(MAPPER.writeValueAsString(bookCreateDto)))
                .andExpect(status().isOk()).andDo(print())
                .andReturn();

        BookDto response = MAPPER.readValue(mvcResult.getResponse().getContentAsString(), BookDto.class);
        verify(bookService).create(bookCreateDto);
        assertEquals(bookDto, response);
    }

    @Test
    void shouldEditExistedBook() throws Exception {
        given(bookService.update(any())).willReturn(bookDto);

        MvcResult mvcResult = mvc.perform(put("/api/book/3")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(MAPPER.writeValueAsString(bookUpdateDto)))
                .andExpect(status().isOk()).andDo(print())
                .andReturn();

        BookDto response = MAPPER.readValue(mvcResult.getResponse().getContentAsString(), BookDto.class);
        verify(bookService).update(any());
        assertEquals(bookDto, response);
    }

    @Test
    void shouldFindCertainBook() throws Exception {
        BookDto daoRes = this.bookDto;
        given(bookService.findById(anyLong())).willReturn(daoRes);

        MvcResult mvcResult = mvc.perform(get("/api/book/3"))
                .andExpect(status().isOk()).andDo(print())
                .andReturn();

        BookDto response = MAPPER.readValue(mvcResult.getResponse().getContentAsString(), BookDto.class);
        verify(bookService).findById(3L);
        assertEquals(daoRes, response);
    }

    @Test
    void shouldDeleteBook() throws Exception {
        doNothing().when(bookService).deleteById(3L);

        mvc.perform(delete("/api/book/3"))
                .andExpect(status().isOk()).andDo(print());

        verify(bookService).deleteById(3L);
    }

    @Test
    void shouldReturnCommentsForBook() throws Exception {
        List<CommentDto> daoRes = List.of(this.commentDto);
        given(commentService.findByBookId(anyLong())).willReturn(daoRes);

        MvcResult mvcResult = mvc.perform(get("/api/book/3/comment"))
                .andExpect(status().isOk()).andDo(print())
                .andReturn();
        List<CommentDto> response = MAPPER.readValue(mvcResult.getResponse().getContentAsString(), new TypeReference<>(){});

        verify(commentService).findByBookId(3L);
        assertEquals(daoRes, response);
    }

    @Test
    void shouldAddNewCommentForBook() throws Exception {
        given(commentService.create(anyLong(), anyString())).willReturn(commentDto);

        CommentCreateDto request = new CommentCreateDto("comment text");
        MvcResult mvcResult = mvc.perform(post("/api/book/3/comment")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(MAPPER.writeValueAsString(request)))
                .andExpect(status().isOk()).andDo(print())
                .andReturn();

        CommentDto response = MAPPER.readValue(mvcResult.getResponse().getContentAsString(), CommentDto.class);
        verify(commentService).create(3L, request.getText());
        assertEquals(commentDto, response);
    }
}
