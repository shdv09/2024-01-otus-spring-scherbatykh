package ru.otus.hw.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import ru.otus.hw.config.TestBeanConfig;
import ru.otus.hw.domain.Question;
import ru.otus.hw.domain.Student;
import ru.otus.hw.domain.TestResult;

import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.Mockito.*;

@SpringBootTest(classes = TestBeanConfig.class)
public class TestRunnerServiceImplTest {
    private static final String STUDENT_FIRST_NAME = "Ivan";
    private static final String STUDENT_LAST_NAME = "Ivanov";
    private static final String PATH_JSON_QUESTIONS = "/service/testRunnerServiceImpl/questions.json";

    @MockBean
    private TestService testService;
    @MockBean
    private StudentService studentService;
    @MockBean
    private ResultService resultService;
    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private TestRunnerServiceImpl testRunnerServiceImpl;

    @AfterEach
    void after() {
        verifyNoMoreInteractions(testService);
    }

    @Test
    @DisplayName("Проверка TestRunnerServiceImpl. Позитивный кейс")
    void test() throws Exception {
        Student student = new Student(STUDENT_FIRST_NAME, STUDENT_LAST_NAME);
        when(studentService.determineCurrentStudent()).thenReturn(student);
        TestResult testResult = new TestResult(student);
        List<Question> questions = mapper.readValue(getFileContent(PATH_JSON_QUESTIONS), new TypeReference<>(){});
        for (var question : questions) {
            testResult.applyAnswer(question, false);
        }
        testResult.setRightAnswersCount(2);
        when(testService.executeTestFor(student)).thenReturn(testResult);
        doNothing().when(resultService).showResult(testResult);

        testRunnerServiceImpl.run();

        assertAll(
                () -> verify(studentService, times(1)).determineCurrentStudent(),
                () -> verify(testService, times(1)).executeTestFor(student),
                () -> verify(resultService, times(1)).showResult(testResult)
        );
    }

    private String getFileContent(String path) throws Exception {
        URI fileUri = getClass().getResource(path).toURI();
        List<String> strings = Files.readAllLines(Paths.get(fileUri));
        return String.join("", strings);
    }
}
