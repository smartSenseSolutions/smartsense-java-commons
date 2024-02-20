CREATE TABLE country(
    id SERIAL NOT NULL,
    country_name VARCHAR(50) NOT NULL,
    population NUMERIC NOT NULL,
    active BOOLEAN NOT NULL,
    created_at TIMESTAMP NOT NULL,
    CONSTRAINT pk_country PRIMARY KEY (id)
);

CREATE TABLE address(
    id SERIAL NOT NULL,
    city VARCHAR(50) NOT NULL,
    house VARCHAR(50) NOT NULL,
    street VARCHAR(50) NOT NULL,
    active BOOLEAN NOT NULL,
    created_at TIMESTAMP NOT NULL,
    CONSTRAINT pk_address PRIMARY KEY (id)
);

CREATE TABLE author(
    id SERIAL NOT NULL,
    author_name VARCHAR(50) NOT NULL,
    age NUMERIC NOT NULL,
    active BOOLEAN NOT NULL,
    created_at TIMESTAMP NOT NULL,
    address_id SERIAL NOT NULL,
    status varchar(255) NOT NULL,
    "level" int2 NOT NULL,
    CONSTRAINT pk_author PRIMARY KEY (id),
    CONSTRAINT fk_address_id FOREIGN KEY (address_id) REFERENCES address(id)
);

CREATE TABLE books(
    id SERIAL NOT NULL,
    book_name VARCHAR(50) NOT NULL,
    description VARCHAR(650) NOT NULL,
    active BOOLEAN NOT NULL,
    created_at TIMESTAMP NOT NULL,
    CONSTRAINT pk_books PRIMARY KEY (id)
);

CREATE TABLE country_author_mapping(
    country_id SERIAL NOT NULL,
    author_id SERIAL NOT NULL,
    CONSTRAINT fk_country_id FOREIGN KEY (country_id) REFERENCES country(id),
    CONSTRAINT fk_author_id FOREIGN KEY (author_id) REFERENCES author(id)
);

CREATE TABLE author_books_mapping(
    book_id SERIAL NOT NULL,
    author_id SERIAL NOT NULL,
    CONSTRAINT fk_author_id FOREIGN KEY (author_id) REFERENCES author(id),
    CONSTRAINT fk_book_id FOREIGN KEY (book_id) REFERENCES books(id)
);

--Insert data
INSERT INTO country(id, country_name, population, active, created_at)
VALUES
(1,'India',1407600000,true, now()),
(2,'USA',331900000,true, now()),
(3,'China',1412400000,true, now()),
(4,'Pakistan',231400000,false, now()),
(5,'Sri Lanka',22200000,true, now()),
(6,'Germany',83200000,true, now()),
(7,'Netherlands',17500000,true, now());

INSERT INTO address(id, city, house, street, active, created_at)
VALUES
(1,'Ahmedabad', 'C 207','Street No 2',true, now()),
(2,'Rajkot','E 1232','Street No 10', true, now()),
(3,'Gandhinagar','T 15','Street No 4',true, now()),
(4,'Kucch','B 845','Street No 5',true, now()),
(5,'Amreli','A 156','Street No 7',true, now()),
(6,'Jamnagar','N 483','Street No 3',true, now()),
(7,'Surat','B 325','Street No 2',true, now()),
(8,'Surat','B 321','Street No 4',true, now()),
(9,'Amreli','C 750','Street No 15',true, now());

INSERT INTO author(id, author_name, age, active, created_at, address_id, status, "level")
VALUES
(1,'John Doe',25,true, now(), 1, 'OnLine', 0),
(2,'Zimple Eriksen',26,true, now(), 2, 'OffLine', 1),
(3,'Yeo Miller',26,false, now(), 3, 'Away', 1),
(4,'Yurky Anderson',16,true, now(), 4, 'OnLine', 2),
(5,'Yurky Thomson',19,true, now(), 5, 'DND', 0),
(6,'Zastrow Olsson',26,true, now(), 6, 'NoCall', 3),
(7,'Ziemer Miller',25,true, now(), 7, 'NoCall', 0),
(8,'Ramesh Purohit',12,true, now(), 8, 'OnLine', 3),
(9,'Kanji Varma',64,true, now(), 9, 'OffLine', 3);

INSERT INTO books(id, book_name, description, active, created_at)
VALUES
(1,'Spring Data','Spring Data related',true, now()),
(2,'Automation with Java','Automation with Java Related to automation programming in java',true, now()),
(3,'Node Js Automation','Node Js Automation Related to automation programming in Node.js',true, now()),
(4,'Data Analytics','Data Analytics',true, now()),
(5,'Microservice with spring boot','Microservice with spring boot',true, now()),
(6,'Microservice with python','Microservice with python',true, now()),
(7,'SOLID','Single Responsibility, Open/Closed, Liskov Substitution, Interface Segregation, Dependency Inversion',true, now()),
(8,'Java 21','Latest version of JDK',true, now()),
(9,'Design Pattern','Design Pattern',true, now()),
(10,'Refactoring 10.15','Refactoring 10.15',true, now()),
(11,'Node of nodes','Node of nodes, Node.js',true, now()),
(12,'Clean Architecture','Clean Architecture',true, now());

INSERT INTO country_author_mapping(country_id,author_id)
VALUES
(1,1), (1,8), (1,9),
(2,2),
(3,3),
(4,4),
(5,5), (5,7),
(6,6),
(7,7);

INSERT INTO author_books_mapping(author_id, book_id)
VALUES
(1,1),
(2,2),
(3,3),
(4,4),
(5,5), (5,10),
(6,6), (6,8),
(7,7), (7,11),
(8, 12),
(9, 9);