package com.smartsensesolutions.java.commons.dao.sample.rest;

import com.smartsensesolutions.java.commons.dao.sample.entity.Author;
import com.smartsensesolutions.java.commons.dao.sample.request.AuthorRequest;
import com.smartsensesolutions.java.commons.dao.sample.service.AuthorService;
import com.smartsensesolutions.java.commons.filter.FilterRequest;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@AllArgsConstructor
public class AuthorResources {

    private final AuthorService authorService;

    @PostMapping("/create/books")
    public List<Author> createBooks(@RequestBody List<AuthorRequest> requests) {
        return authorService.saveAuthorDetails(requests);
    }

    @PostMapping("/author/search")
    public Page<Author> searchAuthor(@RequestBody FilterRequest request) {
        return authorService.searchBasedOnPagination(request);
    }
}
