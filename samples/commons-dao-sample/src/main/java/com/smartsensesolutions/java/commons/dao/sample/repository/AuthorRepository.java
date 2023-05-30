package com.smartsensesolutions.java.commons.dao.sample.repository;

import com.smartsensesolutions.java.commons.base.repository.BaseRepository;
import com.smartsensesolutions.java.commons.dao.sample.entity.Author;
import org.springframework.stereotype.Repository;

@Repository
public interface AuthorRepository extends BaseRepository<Author, Long> {
}
