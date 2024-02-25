package ru.otus.hw.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.otus.hw.config.TestConfig;
import ru.otus.hw.domain.TestResult;

@Service
@RequiredArgsConstructor
public class ResultServiceImpl implements ResultService {

    private final TestConfig testConfig;

    private final LocalizedIOService localizedIoService;

    @Override
    public void showResult(TestResult testResult) {
        localizedIoService.printLine("");
        localizedIoService.printLineLocalized("ResultService.test.results");
        localizedIoService.printFormattedLineLocalized("ResultService.student", testResult.getStudent().getFullName());
        localizedIoService.printFormattedLineLocalized("ResultService.answered.questions.count", testResult.getAnsweredQuestions().size());
        localizedIoService.printFormattedLineLocalized("ResultService.right.answers.count", testResult.getRightAnswersCount());

        if (testResult.getRightAnswersCount() >= testConfig.getRightAnswersCountToPass()) {
            localizedIoService.printLineLocalized("ResultService.passed.test");
            return;
        }
        localizedIoService.printLineLocalized("ResultService.fail.test");
    }
}
