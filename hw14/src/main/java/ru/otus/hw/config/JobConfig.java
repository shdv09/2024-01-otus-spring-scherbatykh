package ru.otus.hw.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.*;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.data.MongoPagingItemReader;
import org.springframework.batch.item.data.builder.MongoPagingItemReaderBuilder;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.builder.FlatFileItemWriterBuilder;
import org.springframework.batch.item.file.transform.DelimitedLineAggregator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.transaction.PlatformTransactionManager;
import ru.otus.hw.models.Book;
import ru.otus.hw.service.BookMongoToSqlTransformer;

import java.util.HashMap;

@SuppressWarnings("unused")
@Configuration
public class JobConfig {
    private static final int CHUNK_SIZE = 5;
    private final Logger logger = LoggerFactory.getLogger("Batch");

    public static final String JOB_NAME = "migrateBooksJob";

    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private PlatformTransactionManager platformTransactionManager;

    @StepScope
    @Bean
    public MongoPagingItemReader<Book> reader(MongoTemplate template) {
        return new MongoPagingItemReaderBuilder<Book>()
                .name("bookMongoReader")
                .template(template)
                .jsonQuery("{}")
                .targetType(Book.class)
                .sorts(new HashMap<>())
                .build();
    }

    @StepScope
    @Bean
    public ItemProcessor<Book, Book> processor(BookMongoToSqlTransformer transformer) {
        return transformer::transform;
    }

    @StepScope
    @Bean
    public FlatFileItemWriter<Book> writer() {
        return new FlatFileItemWriterBuilder<Book>()
                .name("personItemWriter")
                .resource(new FileSystemResource("book.csv"))
                .lineAggregator(new DelimitedLineAggregator<>())
                .build();
    }

    @Bean
    public Job importUserJob(Step transformBooksStep) {
        return new JobBuilder(JOB_NAME, jobRepository)
                .incrementer(new RunIdIncrementer())
                .flow(transformBooksStep)
                .end()
                .build();
    }

    @Bean
    public Step transformBooksStep(ItemReader<Book> reader, FlatFileItemWriter<Book> writer,
                                   ItemProcessor<Book, Book> itemProcessor) {
        return new StepBuilder("transformBooksStep", jobRepository)
                .<Book, Book>chunk(CHUNK_SIZE, platformTransactionManager)
                .reader(reader)
                .processor(itemProcessor)
                .writer(writer)
                .build();
    }
}
