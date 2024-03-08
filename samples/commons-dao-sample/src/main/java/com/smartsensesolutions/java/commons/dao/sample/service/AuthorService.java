package com.smartsensesolutions.java.commons.dao.sample.service;

import com.smartsensesolutions.java.commons.FilterRequest;
import com.smartsensesolutions.java.commons.base.repository.BaseRepository;
import com.smartsensesolutions.java.commons.base.service.BaseService;
import com.smartsensesolutions.java.commons.dao.sample.entity.Author;
import com.smartsensesolutions.java.commons.dao.sample.entity.Books;
import com.smartsensesolutions.java.commons.dao.sample.repository.AuthorRepository;
import com.smartsensesolutions.java.commons.dao.sample.repository.BooksRepository;
import com.smartsensesolutions.java.commons.dao.sample.request.AuthorRequest;
import com.smartsensesolutions.java.commons.dao.sample.request.BookRequest;
import com.smartsensesolutions.java.commons.specification.SpecificationUtil;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class AuthorService extends BaseService<Author, Long> {
    private final AuthorRepository authorRepository;
    private final BooksRepository booksRepository;

    @Override
    protected BaseRepository<Author, Long> getRepository() {
        return authorRepository;
    }
	
    public Page<Author> searchBasedOnPagination(FilterRequest request) {
        return filter(request);
    }

    public List<Author> saveAuthorDetails(List<AuthorRequest> requests) {
        List<Author> authors = new ArrayList<>();
        for (AuthorRequest request : requests) {
            Author author = new Author();
            author.setAuthorName(request.authorName());
            author.setAge(request.age());
            List<Books> books = new ArrayList<>();
            for (BookRequest bookRequest : request.books()) {
                Books book = new Books();
                book.setBookName(bookRequest.bookName());
                book.setDescription(bookRequest.description());
                books.add(booksRepository.save(book));
            }
            author.setBooks(books);
            authors.add(create(author));
        }
        return authors;
    }
}
