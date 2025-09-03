package com.smartsensesolutions.commons.dao.sample.service;

import com.smartsensesolutions.commons.dao.base.BaseRepository;
import com.smartsensesolutions.commons.dao.base.BaseService;
import com.smartsensesolutions.commons.dao.filter.FilterRequest;
import com.smartsensesolutions.commons.dao.sample.entity.Country;
import com.smartsensesolutions.commons.dao.sample.repository.CountryRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class CountryService extends BaseService<Country, Long> {
    private final CountryRepository countryRepository;

    @Override
    protected BaseRepository<Country, Long> getRepository() {
        return countryRepository;
    }

    public Page<Country> searchBasedOnPagination(FilterRequest request) {
        return filter(request);
    }

}
