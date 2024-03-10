package ru.otus.hw.dao;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import ru.otus.hw.config.*;
import ru.otus.hw.domain.Question;
import ru.otus.hw.exceptions.QuestionReadException;

import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest(classes = {CsvQuestionDao.class, AppProperties.class, TestBeanConfig.class})
public class CsvQuestionDaoTest {
    private static final String TEST_RESOURCE_NAME = "dao/csvQuestionDao/csvResource/questions.csv";
    private static final String EMPTY_TEST_RESOURCE_NAME = "dao/csvQuestionDao/csvResource/questions_empty.csv";
    private static final String NO_ANSWERS_TEST_RESOURCE_NAME = "dao/csvQuestionDao/csvResource/questions_no_answers.csv";

    private static final String PATH_JSON_REFERENCE_FILE = "/dao/csvQuestionDao/reference/questions.json";
    private static final String PATH_JSON_REFERENCE_FILE_NO_ANSWERS = "/dao/csvQuestionDao/reference/questions_no_answers.json";

    private static final String ERROR_MESSAGE = "File not found: dao/csvQuestionDao/csvResource/wuestions.csv";

    @MockBean
    private TestFileNameProvider fileNameProvider;
    @Autowired
    private CsvQuestionDao csvQuestionDao;
    @Autowired
    private ObjectMapper mapper;

    @AfterEach
    void after() {
        verifyNoMoreInteractions(fileNameProvider);
    }

    @Test
    @DisplayName("Проверка CsvQuestionDao. Успешный тест")
    void test1() throws Exception {
        when(fileNameProvider.getTestFileName()).thenReturn(TEST_RESOURCE_NAME);

        List<Question> result = csvQuestionDao.findAll();

        List<Question> reference = mapper.readValue(getFileContent(PATH_JSON_REFERENCE_FILE), new TypeReference<>() {});
        assertAll("CsvQuestionDao. Успешный тест",
                () -> verify(fileNameProvider, times(1)).getTestFileName(),
                () -> assertEquals(reference, result)
        );
    }

    @Test
    @DisplayName("Проверка CsvQuestionDao. Тест на csv-файле с вопросами без ответов")
    void test2() throws Exception {
        when(fileNameProvider.getTestFileName()).thenReturn(NO_ANSWERS_TEST_RESOURCE_NAME);

        List<Question> result = csvQuestionDao.findAll();

        List<Question> reference = mapper.readValue(getFileContent(PATH_JSON_REFERENCE_FILE_NO_ANSWERS), new TypeReference<>() {});
        assertAll("CsvQuestionDao. Тест на csv-файле с вопросами без ответов",
                () -> verify(fileNameProvider, times(1)).getTestFileName(),
                () -> assertEquals(reference, result)
        );
    }

    @Test
    @DisplayName("Проверка CsvQuestionDao. Тест на пустом csv-файле")
    void test3() {
        when(fileNameProvider.getTestFileName()).thenReturn(EMPTY_TEST_RESOURCE_NAME);

        List<Question> result = csvQuestionDao.findAll();

        assertAll("CsvQuestionDao. Тест на пустом csv-файле",
                () -> verify(fileNameProvider, times(1)).getTestFileName(),
                () -> assertEquals(Collections.emptyList(), result)
        );
    }

    @Test
    @DisplayName("Проверка CsvQuestionDao. Ошибка, файл не найден")
    void test4() {
        when(fileNameProvider.getTestFileName()).thenReturn(TEST_RESOURCE_NAME.replace("q", "w"));

        QuestionReadException exception = assertThrows(QuestionReadException.class, () -> csvQuestionDao.findAll());

        assertAll("CsvQuestionDao. Ошибка, файл не найден",
                () -> verify(fileNameProvider, times(1)).getTestFileName(),
                () -> assertEquals(ERROR_MESSAGE, exception.getMessage())
        );
    }

    private String getFileContent(String path) throws Exception {
        URI fileUri = getClass().getResource(path).toURI();
        List<String> strings = Files.readAllLines(Paths.get(fileUri));
        return String.join("", strings);
    }
}
