package ru.otus.hw.service;

import lombok.RequiredArgsConstructor;
import org.springframework.util.CollectionUtils;
import ru.otus.hw.dao.QuestionDao;
import ru.otus.hw.domain.Question;

import java.util.List;

@RequiredArgsConstructor
public class TestServiceImpl implements TestService {

    private final IOService ioService;

    private final QuestionDao questionDao;

    @Override
    public void executeTest() {
        ioService.printFormattedLine("%nPlease answer the questions below%n");
        List<Question> questions = questionDao.findAll();
        if (CollectionUtils.isEmpty(questions)) {
            return;
        }
        questions.forEach(question -> {
            ioService.printFormattedLine("Q: %s%nA:", question.text());
            for (int i = 0; i < question.answers().size(); i++) {
                ioService.printFormattedLine("\t%d. %s", i + 1, question.answers().get(i).text());
            }
            ioService.printLine("");
        });
    }
}
