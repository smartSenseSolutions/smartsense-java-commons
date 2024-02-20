package com.smartsensesolutions.java.commons.dao.sample.service;

import com.smartsensesolutions.java.commons.base.repository.BaseRepository;
import com.smartsensesolutions.java.commons.base.service.BaseService;
import com.smartsensesolutions.java.commons.dao.sample.entity.Country;
import com.smartsensesolutions.java.commons.dao.sample.repository.CountryRepository;
import com.smartsensesolutions.java.commons.filter.FilterRequest;
import com.smartsensesolutions.java.commons.specification.SpecificationUtil;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class CountryService extends BaseService<Country, Long> {
    private final CountryRepository countryRepository;
    private final SpecificationUtil<Country> specificationUtil;

    @Override
    protected BaseRepository<Country, Long> getRepository() {
        return countryRepository;
    }

    @Override
    protected SpecificationUtil<Country> getSpecificationUtil() {
        return specificationUtil;
    }

    public Page<Country> searchBasedOnPagination(FilterRequest request) {
        return filter(request);
    }

}
