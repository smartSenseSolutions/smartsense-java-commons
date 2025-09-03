package com.smartsensesolutions.commons.dao.sample.rest;

import com.smartsensesolutions.commons.dao.filter.FilterRequest;
import com.smartsensesolutions.commons.dao.sample.entity.Author;
import com.smartsensesolutions.commons.dao.sample.entity.PublicAuthorView;
import com.smartsensesolutions.commons.dao.sample.request.AuthorRequest;
import com.smartsensesolutions.commons.dao.sample.service.AuthorService;
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

    @PostMapping("/public/author/search")
    public Page<PublicAuthorView> publicSearchAuthor(@RequestBody FilterRequest request) {
        return authorService.filterPublicAuthor(request);
    }
}
