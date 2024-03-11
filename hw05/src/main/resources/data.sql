insert into authors(full_name)
values ('Джошуа Блох'), ('Борис Стругацкий'), ('Михаил Зощенко');

insert into genres(name)
values ('Программирование'), ('Фантастика'), ('Сатира');

insert into books(title, author_id, genre_id)
values ('Java. Эффективное программирование', 1, 1), ('Пикник на обочине', 2, 2), ('Голубая книга', 3, 3);
