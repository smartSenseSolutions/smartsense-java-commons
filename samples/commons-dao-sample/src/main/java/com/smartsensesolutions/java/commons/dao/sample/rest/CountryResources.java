package com.smartsensesolutions.java.commons.dao.sample.rest;

import com.smartsensesolutions.java.commons.dao.sample.entity.Country;
import com.smartsensesolutions.java.commons.dao.sample.service.CountryService;
import com.smartsensesolutions.java.commons.filter.FilterRequest;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
public class CountryResources {

    private final CountryService countryService;


    @PostMapping("/country/search")
    public Page<Country> countrySearch(@RequestBody FilterRequest request) {
        return countryService.searchBasedOnPagination(request);
    }
}
