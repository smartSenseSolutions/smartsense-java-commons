package com.smartsensesolutions.java.commons.dao.sample.repository;

import com.smartsensesolutions.java.commons.base.repository.BaseRepository;
import com.smartsensesolutions.java.commons.dao.sample.entity.Books;
import org.springframework.stereotype.Repository;

@Repository
public interface BooksRepository extends BaseRepository<Books, Long> {
}
