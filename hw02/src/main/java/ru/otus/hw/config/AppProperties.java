package ru.otus.hw.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Data
public class AppProperties implements TestFileNameProvider {
    private final String testFileName;

    public AppProperties(@Value("questions.csv")String testFileName) {
        this.testFileName = testFileName;
    }
}
