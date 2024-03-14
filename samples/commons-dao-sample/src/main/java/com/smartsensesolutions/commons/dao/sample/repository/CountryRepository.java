package com.smartsensesolutions.commons.dao.sample.repository;

import com.smartsensesolutions.commons.dao.base.BaseRepository;
import com.smartsensesolutions.commons.dao.sample.entity.Country;
import org.springframework.stereotype.Repository;

@Repository
public interface CountryRepository extends BaseRepository<Country, Long> {
}
