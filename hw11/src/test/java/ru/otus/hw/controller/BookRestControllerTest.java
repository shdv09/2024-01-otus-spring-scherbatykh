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
import ru.otus.hw.exceptions.NotFoundException;
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
import static org.mockito.Mockito.doThrow;
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
        AuthorDto authorDto = new AuthorDto("1", "author");
        GenreDto genreDto = new GenreDto("2", "genre");
        bookDto = new BookDto("3", "book", authorDto, List.of(genreDto), null);
        bookCreateDto = new BookCreateDto("book", "1", Set.of("2"));
        bookUpdateDto = new BookUpdateDto("3", "book", "1", Set.of("2"));
        commentDto = new CommentDto("4", "comment text");
    }

    @AfterEach
    void after() {
        verifyNoMoreInteractions(bookService, authorService, genreService, commentService);
    }

    @Test
    void getBookListPositiveTest() throws Exception {
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
    void getBookListError500Test() throws Exception {
        given(bookService.findAll()).willThrow(RuntimeException.class);

        mvc.perform(get("/api/book"))
                .andExpect(status().isInternalServerError()).andDo(print());

        verify(bookService).findAll();
    }

    @Test
    void saveNewBookPositiveTest() throws Exception {
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
    void saveNewBookError500Test() throws Exception {
        given(bookService.create(any())).willThrow(RuntimeException.class);

        mvc.perform(post("/api/book")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(MAPPER.writeValueAsString(bookCreateDto)))
                .andExpect(status().isInternalServerError()).andDo(print());;

        verify(bookService).create(bookCreateDto);
    }

    @Test
    void editBookPositiveTest() throws Exception {
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
    void editBookError404Test() throws Exception {
        given(bookService.update(any())).willThrow(NotFoundException.class);

        mvc.perform(put("/api/book/3")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(MAPPER.writeValueAsString(bookUpdateDto)))
                .andExpect(status().isNotFound()).andDo(print());

        verify(bookService).update(any());
    }

    @Test
    void editBookError500Test() throws Exception {
        given(bookService.update(any())).willThrow(RuntimeException.class);

        mvc.perform(put("/api/book/3")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(MAPPER.writeValueAsString(bookUpdateDto)))
                .andExpect(status().isInternalServerError()).andDo(print());

        verify(bookService).update(any());
    }

    @Test
    void findBookPositiveTest() throws Exception {
        BookDto daoRes = this.bookDto;
        given(bookService.findById(anyString())).willReturn(daoRes);

        MvcResult mvcResult = mvc.perform(get("/api/book/3"))
                .andExpect(status().isOk()).andDo(print())
                .andReturn();

        BookDto response = MAPPER.readValue(mvcResult.getResponse().getContentAsString(), BookDto.class);
        verify(bookService).findById("3");
        assertEquals(daoRes, response);
    }

    @Test
    void findBookError404Test() throws Exception {
        given(bookService.findById(anyString())).willThrow(NotFoundException.class);

        mvc.perform(get("/api/book/3"))
                .andExpect(status().isNotFound()).andDo(print());

        verify(bookService).findById("3");
    }

    @Test
    void findBookError500Test() throws Exception {
        given(bookService.findById(anyString())).willThrow(RuntimeException.class);

        mvc.perform(get("/api/book/3"))
                .andExpect(status().isInternalServerError()).andDo(print());

        verify(bookService).findById("3");
    }

    @Test
    void deleteBookPositiveTest() throws Exception {
        doNothing().when(bookService).deleteById("3");

        mvc.perform(delete("/api/book/3"))
                .andExpect(status().isOk()).andDo(print());

        verify(bookService).deleteById("3");
    }

    @Test
    void deleteBookError500Test() throws Exception {
        doThrow(RuntimeException.class).when(bookService).deleteById("3");

        mvc.perform(delete("/api/book/3"))
                .andExpect(status().isInternalServerError()).andDo(print());

        verify(bookService).deleteById("3");
    }

    @Test
    void getCommentsForBookPositiveTest() throws Exception {
        List<CommentDto> daoRes = List.of(this.commentDto);
        given(commentService.findByBookId(anyString())).willReturn(daoRes);

        MvcResult mvcResult = mvc.perform(get("/api/book/3/comment"))
                .andExpect(status().isOk()).andDo(print())
                .andReturn();
        List<CommentDto> response = MAPPER.readValue(mvcResult.getResponse().getContentAsString(), new TypeReference<>(){});

        verify(commentService).findByBookId("3");
        assertEquals(daoRes, response);
    }

    @Test
    void getCommentsForBookError500Test() throws Exception {
        given(commentService.findByBookId(anyString())).willThrow(RuntimeException.class);

        mvc.perform(get("/api/book/3/comment"))
                .andExpect(status().isInternalServerError()).andDo(print())
                .andReturn();

        verify(commentService).findByBookId("3");
    }

    @Test
    void addNewCommentForBookPositiveTest() throws Exception {
        given(commentService.create(anyString(), anyString())).willReturn(commentDto);

        CommentCreateDto request = new CommentCreateDto("comment text");
        MvcResult mvcResult = mvc.perform(post("/api/book/3/comment")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(MAPPER.writeValueAsString(request)))
                .andExpect(status().isOk()).andDo(print())
                .andReturn();

        CommentDto response = MAPPER.readValue(mvcResult.getResponse().getContentAsString(), CommentDto.class);
        verify(commentService).create("3", request.getText());
        assertEquals(commentDto, response);
    }

    @Test
    void addNewCommentForBookError404Test() throws Exception {
        given(commentService.create(anyString(), anyString())).willThrow(NotFoundException.class);

        CommentCreateDto request = new CommentCreateDto("comment text");
        mvc.perform(post("/api/book/3/comment")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(MAPPER.writeValueAsString(request)))
                .andExpect(status().isNotFound()).andDo(print())
                .andReturn();

        verify(commentService).create("3", request.getText());
    }

    @Test
    void addNewCommentForBookTest() throws Exception {
        given(commentService.create(anyString(), anyString())).willThrow(RuntimeException.class);

        CommentCreateDto request = new CommentCreateDto("comment text");
        mvc.perform(post("/api/book/3/comment")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(MAPPER.writeValueAsString(request)))
                .andExpect(status().isInternalServerError()).andDo(print())
                .andReturn();

        verify(commentService).create("3", request.getText());
    }
}
