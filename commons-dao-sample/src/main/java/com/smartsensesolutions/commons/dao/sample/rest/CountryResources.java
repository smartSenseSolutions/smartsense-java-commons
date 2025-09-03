package com.smartsensesolutions.commons.dao.sample.rest;

import com.smartsensesolutions.commons.dao.filter.FilterRequest;
import com.smartsensesolutions.commons.dao.sample.entity.Country;
import com.smartsensesolutions.commons.dao.sample.service.CountryService;
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
