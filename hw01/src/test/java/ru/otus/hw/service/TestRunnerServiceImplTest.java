package ru.otus.hw.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.mockito.Mockito.*;

public class TestRunnerServiceImplTest {
    private TestService testService;
    private TestRunnerServiceImpl testRunnerServiceImpl;

    @BeforeEach
    void init() {
        this.testService = Mockito.mock(TestServiceImpl.class);
        this.testRunnerServiceImpl = new TestRunnerServiceImpl(testService);
    }

    @AfterEach
    void after() {
        verifyNoMoreInteractions(testService);
    }

    @Test
    @DisplayName("Проверка TestRunnerServiceImpl. Позитивный кейс")
    void test() {
        doNothing().when(testService).executeTest();

        testRunnerServiceImpl.run();

        verify(testService, times(1)).executeTest();
    }
}
