package ru.otus.hw.dao;

import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.util.StringUtils;
import ru.otus.hw.config.TestFileNameProvider;
import ru.otus.hw.dao.dto.QuestionDto;
import ru.otus.hw.domain.Question;
import ru.otus.hw.exceptions.QuestionReadException;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

@RequiredArgsConstructor
public class CsvQuestionDao implements QuestionDao {
    private static final String FILE_NAME_ERROR_MESSAGE = "Resource file name not configured.";

    private static final String FILE_NOT_FOUND_ERROR_MESSAGE = "File not found: ";

    private final TestFileNameProvider fileNameProvider;

    @Override
    public List<Question> findAll() {
        String testFileName = fileNameProvider.getTestFileName();
        if (!StringUtils.hasText(testFileName)) {
            throw new QuestionReadException(FILE_NAME_ERROR_MESSAGE, null);
        }
        InputStream is = getClass().getClassLoader().getResourceAsStream(testFileName);
        if (is == null) {
            throw new QuestionReadException(FILE_NOT_FOUND_ERROR_MESSAGE + testFileName, null);
        }
        InputStreamReader reader = new InputStreamReader(is);
        CsvToBean<QuestionDto> csvToBean = new CsvToBeanBuilder<QuestionDto>(reader)
                .withType(QuestionDto.class)
                .withSkipLines(1)
                .withSeparator(';')
                .build();
        List<QuestionDto> list = csvToBean.parse();
        return list.stream()
                .map(QuestionDto::toDomainObject)
                .toList();
    }
}
