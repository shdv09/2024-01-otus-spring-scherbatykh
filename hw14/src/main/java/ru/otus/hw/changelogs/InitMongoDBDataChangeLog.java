package ru.otus.hw.changelogs;

import com.github.cloudyrock.mongock.ChangeLog;
import com.github.cloudyrock.mongock.ChangeSet;
import com.github.cloudyrock.mongock.driver.mongodb.springdata.v3.decorator.impl.MongockTemplate;
import com.mongodb.client.MongoDatabase;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Comment;
import ru.otus.hw.models.Genre;

import java.util.LinkedList;
import java.util.List;

@ChangeLog(order = "001")
public class InitMongoDBDataChangeLog {

    private final List<Author> authors = new LinkedList<>();

    private final List<Genre> genres = new LinkedList<>();

    private final List<Book> books = new LinkedList<>();

    @ChangeSet(order = "000", id = "dropDB", author = "shdv09", runAlways = true)
    public void dropDB(MongoDatabase database) {
        database.drop();
    }

    @ChangeSet(order = "001", id = "initAuthors", author = "shdv09", runAlways = true)
    public void initAuthors(MongockTemplate template) {
        for (int i = 0; i < 3; i++) {
            Author author = new Author(null, "author" + i);
            template.save(author);
            authors.add(author);
        }
    }

    @ChangeSet(order = "002", id = "initGenres", author = "shdv09", runAlways = true)
    public void initGenres(MongockTemplate template) {
        for (int i = 0; i < 6; i++) {
            Genre genre = new Genre(null, "genre" + i);
            template.save(genre);
            genres.add(genre);
        }
    }

    @ChangeSet(order = "003", id = "initBooks", author = "shdv09", runAlways = true)
    public void initBooks(MongockTemplate template) {
        for (int i = 0; i < 3; i++) {
            Book book = new Book(null, "book" + i, authors.get(i), genres.subList(i, i + 2));
            template.save(book);
            books.add(book);
        }
    }

    @ChangeSet(order = "004", id = "initComments", author = "shdv09", runAlways = true)
    public void initComments(MongockTemplate template) {
        for (int i = 0; i < 3; i++) {
            Comment comment = new Comment(null, "comment" + i, books.get(0));
            template.save(comment);
        }
    }
}
