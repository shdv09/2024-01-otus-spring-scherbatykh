package ru.otus.hw.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.mockito.Mockito;
import ru.otus.hw.domain.Student;
import ru.otus.hw.domain.TestResult;

import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.Mockito.*;

public class TestRunnerServiceImplTest {
    private static final String STUDENT_FIRST_NAME = "Ivan";
    private static final String STUDENT_LAST_NAME = "Ivanov";
    private static final String PATH_JSON_TEST_RESULT = "/service/testRunnerServiceImpl/testResult.json";

    private TestService testService;
    private StudentService studentService;
    private ResultService resultService;

    private static ObjectMapper mapper;

    private TestRunnerServiceImpl testRunnerServiceImpl;

    @BeforeAll
    static void initStatic() {
        mapper = new ObjectMapper();
    }

    @BeforeEach
    void init() {
        this.testService = Mockito.mock(TestServiceImpl.class);
        this.resultService = Mockito.mock(ResultService.class);
        this.studentService = Mockito.mock(StudentService.class);
        this.testRunnerServiceImpl = new TestRunnerServiceImpl(testService, studentService, resultService);
    }

    @AfterEach
    void after() {
        verifyNoMoreInteractions(testService);
    }

    @Test
    @DisplayName("Проверка TestRunnerServiceImpl. Позитивный кейс")
    void test() throws Exception {
        Student student = new Student(STUDENT_FIRST_NAME, STUDENT_LAST_NAME);
        when(studentService.determineCurrentStudent()).thenReturn(student);
        TestResult testResult = mapper.readValue(getFileContent(PATH_JSON_TEST_RESULT), TestResult.class);
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
