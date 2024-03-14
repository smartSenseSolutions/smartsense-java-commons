package com.smartsensesolutions.commons.dao.sample.request;

import java.util.List;

public record AuthorRequest(String authorName, Long age, List<BookRequest> books) {
}
