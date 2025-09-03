package com.smartsensesolutions.commons.dao.sample.repository;

import com.smartsensesolutions.commons.dao.base.BaseRepository;
import com.smartsensesolutions.commons.dao.sample.entity.Books;
import org.springframework.stereotype.Repository;

@Repository
public interface BooksRepository extends BaseRepository<Books, Long> {
}
