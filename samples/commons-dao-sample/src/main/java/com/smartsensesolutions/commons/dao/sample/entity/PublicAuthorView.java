package com.smartsensesolutions.commons.dao.sample.entity;

import java.util.List;

public interface PublicAuthorView {

    String getAuthorName();

    Long getAge();

    List<PublicBookView> getBooks();
}

