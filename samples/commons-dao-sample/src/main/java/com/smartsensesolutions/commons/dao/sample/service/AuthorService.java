package com.smartsensesolutions.commons.dao.sample.service;

import com.smartsensesolutions.commons.dao.base.BaseRepository;
import com.smartsensesolutions.commons.dao.base.BaseService;
import com.smartsensesolutions.commons.dao.filter.FilterRequest;
import com.smartsensesolutions.commons.dao.sample.entity.Author;
import com.smartsensesolutions.commons.dao.sample.entity.Books;
import com.smartsensesolutions.commons.dao.sample.entity.PublicAuthorView;
import com.smartsensesolutions.commons.dao.sample.repository.AuthorRepository;
import com.smartsensesolutions.commons.dao.sample.repository.BooksRepository;
import com.smartsensesolutions.commons.dao.sample.request.AuthorRequest;
import com.smartsensesolutions.commons.dao.sample.request.BookRequest;
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

    public Page<PublicAuthorView> filterPublicAuthor(FilterRequest request) {
        return filter(request, PublicAuthorView.class);
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
