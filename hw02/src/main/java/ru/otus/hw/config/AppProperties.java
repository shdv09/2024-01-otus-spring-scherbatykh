package ru.otus.hw.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Data
public class AppProperties implements TestFileNameProvider, TestConfig {
    private final String testFileName;

    private final int rightAnswerCountToPass;

    public AppProperties(@Value("${app.test-file-name}") String testFileName,
                         @Value("${app.right-answer-count-to-pass}") int rightAnswerCountToPass) {
        this.testFileName = testFileName;
        this.rightAnswerCountToPass = rightAnswerCountToPass;
    }

    @Override
    public int getRightAnswersCountToPass() {
        return rightAnswerCountToPass;
    }
}
