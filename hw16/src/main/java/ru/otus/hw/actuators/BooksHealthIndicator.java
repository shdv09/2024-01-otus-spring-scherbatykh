package ru.otus.hw.actuators;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.boot.actuate.health.Status;
import org.springframework.stereotype.Component;
import ru.otus.hw.repositories.BookRepository;

@RequiredArgsConstructor
@Component
public class BooksHealthIndicator implements HealthIndicator {

    private final BookRepository bookRepository;

    @Override
    public Health health() {
        boolean storageEmpty = bookRepository.count() == 0;
        if (storageEmpty) {
            return Health.down()
                    .status(Status.DOWN)
                    .withDetail("message", "Book storage is empty")
                    .build();
        } else {
            return Health.up().withDetail("message", "Everything is good, so far)").build();
        }
    }
}
