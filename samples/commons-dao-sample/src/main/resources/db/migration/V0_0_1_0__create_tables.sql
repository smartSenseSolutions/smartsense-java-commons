CREATE TABLE author(
    id SERIAL NOT NULL,
    author_name VARCHAR(50) NOT NULL,
    age NUMERIC NOT NULL,
    active BOOLEAN NOT NULL,
    created_at TIMESTAMP NOT NULL,
    CONSTRAINT pk_author PRIMARY KEY (id)
);

CREATE TABLE books(
    id SERIAL NOT NULL,
    book_name VARCHAR(50) NOT NULL,
    description VARCHAR(650) NOT NULL,
    active BOOLEAN NOT NULL,
    created_at TIMESTAMP NOT NULL,
    CONSTRAINT pk_books PRIMARY KEY (id)
);

CREATE TABLE author_books_mapping(
    book_id SERIAL NOT NULL,
    author_id SERIAL NOT NULL,
    CONSTRAINT fk_author_id FOREIGN KEY (author_id) REFERENCES author(id),
    CONSTRAINT fk_book_id FOREIGN KEY (book_id) REFERENCES books(id)
);

--Insert data
INSERT INTO author(id, author_name, age, active, created_at)
VALUES
(1,'John Doe',25,true, now()),
(2,'Zimple Eriksen',26,true, now()),
(3,'Yeo Miller',26,true, now()),
(4,'Yurky Anderson',26,true, now()),
(5,'Yurky Thomson',26,true, now()),
(6,'Zastrow Olsson',26,true, now()),
(7,'Ziemer Miller',26,true, now());

INSERT INTO books(id, book_name, description, active, created_at)
VALUES
(1,'Spring Data',25,true, now()),
(2,'Automation with Java',25,true, now()),
(3,'Node Js Automation',25,true, now()),
(4,'Data Analytics',25,true, now()),
(5,'Microservice with spring boot',25,true, now()),
(6,'Microservice with python',25,true, now()),
(7,'OOPC',25,true, now());

INSERT INTO author_books_mapping(book_id,author_id)
VALUES
(1,1),
(2,2),
(3,3),
(4,4),
(5,5),
(6,6),
(7,7),
(2,1),
(3,2),
(4,3),
(5,4),
(6,5),
(7,6),
(3,7),
(4,1),
(5,2),
(6,3),
(7,4),
(2,5),
(3,6),
(7,7),
(7,1),
(6,2),
(6,3);