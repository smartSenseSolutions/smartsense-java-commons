package com.smartsensesolutions.commons.dao.sample.repository;

import com.smartsensesolutions.commons.dao.base.BaseRepository;
import com.smartsensesolutions.commons.dao.sample.entity.Author;
import org.springframework.stereotype.Repository;

@Repository
public interface AuthorRepository extends BaseRepository<Author, Long> {
}
