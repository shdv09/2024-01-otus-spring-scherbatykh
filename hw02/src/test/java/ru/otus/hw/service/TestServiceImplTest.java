package ru.otus.hw.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import ru.otus.hw.dao.CsvQuestionDao;
import ru.otus.hw.dao.QuestionDao;
import ru.otus.hw.domain.Answer;
import ru.otus.hw.domain.Question;
import ru.otus.hw.exceptions.QuestionReadException;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

public class TestServiceImplTest {
    private IOService ioService;
    private QuestionDao questionDao;
    private TestServiceImpl testServiceImpl;

    @BeforeEach
    void init() {
        this.ioService = Mockito.mock(StreamsIOService.class);
        this.questionDao = Mockito.mock(CsvQuestionDao.class);
        this.testServiceImpl = new TestServiceImpl(ioService, questionDao);
    }

    @AfterEach
    void after() {
        verifyNoMoreInteractions(questionDao, ioService);
    }

    @Test
    @DisplayName("Проверка TestServiceImpl. Пустой список от DAO")
    void test() {
        List<Question> daoResult = Collections.emptyList();
        when(questionDao.findAll()).thenReturn(daoResult);

        testServiceImpl.executeTest();

        assertAll("Проверка TestServiceImpl на пустом списке",
                () -> verify(ioService, times(1)).printLine(anyString()),
                () -> verify(questionDao, times(1)).findAll()
        );
    }

    @Test
    @DisplayName("Проверка TestServiceImpl. Одна запись от DAO")
    void test1() {
        List<Answer> answers =
                List.of(new Answer("Answer one", false), new Answer("Answer two", true));
        Question question = new Question("Question one", answers);
        List<Question> daoResult = Collections.singletonList(question);
        when(questionDao.findAll()).thenReturn(daoResult);

        testServiceImpl.executeTest();

        assertAll("Проверка TestServiceImpl, одна запись от DAO",
                () -> verify(questionDao, times(1)).findAll(),
                () -> verify(ioService, times(2)).printLine(anyString()),
                () -> verify(ioService, times(1)).printFormattedLine(anyString(), any()),
                () -> verify(ioService, times(2)).printFormattedLine(anyString(), any(), any())
        );
    }

    @Test
    @DisplayName("Проверка TestServiceImpl. Exception от DAO")
    void test2() {
        doThrow(QuestionReadException.class).when(questionDao).findAll();

        assertThrows(QuestionReadException.class, () -> testServiceImpl.executeTest());

        assertAll("Проверка TestServiceImpl, исключение от DAO",
                () -> verify(questionDao, times(1)).findAll(),
                () -> verify(ioService, times(1)).printLine(anyString())
        );
    }
}
