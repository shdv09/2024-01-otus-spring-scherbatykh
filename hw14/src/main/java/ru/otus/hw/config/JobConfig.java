package ru.otus.hw.config;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.MethodInvokingTaskletAdapter;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.data.MongoPagingItemReader;
import org.springframework.batch.item.data.builder.MongoPagingItemReaderBuilder;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.batch.item.database.builder.JpaItemWriterBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.transaction.PlatformTransactionManager;
import ru.otus.hw.models.jpa.BookJpa;
import ru.otus.hw.models.mongo.Book;
import ru.otus.hw.service.BookMongoToSqlTransformer;

import java.util.HashMap;

@SuppressWarnings("unused")
@RequiredArgsConstructor
@Configuration
public class JobConfig {
    private static final int CHUNK_SIZE = 5;

    private static final String JOB_NAME = "migrateBooksJob";

    private final Logger logger = LoggerFactory.getLogger("Batch");

    private final JobRepository jobRepository;

    private final PlatformTransactionManager platformTransactionManager;

    private final BookMongoToSqlTransformer bookMongoToSqlTransformer;

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

    @Bean
    public ItemProcessor<Book, BookJpa> processor() {
        return bookMongoToSqlTransformer::transform;
    }

    @Bean
    public JpaItemWriter<BookJpa> writer(EntityManager entityManager) {
        return new JpaItemWriterBuilder<BookJpa>()
                .entityManagerFactory(entityManager.getEntityManagerFactory())
                .build();
    }

    @Bean
    public MethodInvokingTaskletAdapter cleanUpTasklet() {
        MethodInvokingTaskletAdapter adapter = new MethodInvokingTaskletAdapter();

        adapter.setTargetObject(bookMongoToSqlTransformer);
        adapter.setTargetMethod("cleanUp");

        return adapter;
    }

    @Bean
    public Job importUserJob(Step transformBooksStep) {
        return new JobBuilder(JOB_NAME, jobRepository)
                .incrementer(new RunIdIncrementer())
                .flow(transformBooksStep)
                .next(cleanUpStep())
                .end()
                .build();
    }

    @Bean
    public Step transformBooksStep(ItemReader<Book> reader, JpaItemWriter<BookJpa> writer,
                                   ItemProcessor<Book, BookJpa> itemProcessor) {
        return new StepBuilder("transformBooksStep", jobRepository)
                .<Book, BookJpa>chunk(CHUNK_SIZE, platformTransactionManager)
                .reader(reader)
                .processor(itemProcessor)
                .writer(writer)
                .build();
    }

    @Bean
    public Step cleanUpStep() {
        return new StepBuilder("cleanUpStep", jobRepository)
                .tasklet(cleanUpTasklet(), platformTransactionManager)
                .build();
    }
}
