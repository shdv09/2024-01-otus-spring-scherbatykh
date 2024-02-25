package ru.otus.hw.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import ru.otus.hw.dao.QuestionDao;
import ru.otus.hw.domain.Answer;
import ru.otus.hw.domain.Student;
import ru.otus.hw.domain.TestResult;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TestServiceImpl implements TestService {

    private final LocalizedIOService localizedIoService;

    private final QuestionDao questionDao;

    @Override
    public TestResult executeTestFor(Student student) {
        localizedIoService.printLineLocalized("TestService.answer.the.questions");
        var testResult = new TestResult(student);
        var questions = questionDao.findAll();
        if (CollectionUtils.isEmpty(questions)) {
            return testResult;
        }

        for (var question : questions) {
            localizedIoService.printFormattedLine("%nQ: %s%nA:", question.text());
            printAnswers(question.answers());
            int userInput = readUserInput(question.answers().size());
            Answer answer = question.answers().get(userInput - 1);
            boolean isAnswerValid = answer.isCorrect();
            testResult.applyAnswer(question, isAnswerValid);
        }
        return testResult;
    }

    private void printAnswers(List<Answer> answers) {
        for (int i = 0; i < answers.size(); i++) {
            var answer = answers.get(i);
            localizedIoService.printFormattedLine("\t%d. %s", i + 1, answer.text());
        }
    }

    private int readUserInput(int numberOfAnswers) {
        return localizedIoService.readIntForRangeWithPromptLocalized(1, numberOfAnswers,
                "TestService.input.prompt",
                "TestService.input.error");
    }
}
