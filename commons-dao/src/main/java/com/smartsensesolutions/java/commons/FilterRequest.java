/*
 * Copyright 2023 smartSense Consulting Solutions Pvt. Ltd
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.smartsensesolutions.java.commons;

import com.smartsensesolutions.java.commons.criteria.CriteriaOperator;
import com.smartsensesolutions.java.commons.filter.FilterCriteria;
import com.smartsensesolutions.java.commons.operator.Operator;
import com.smartsensesolutions.java.commons.sort.Sort;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * FilterRequest used for the filter the data based on the given criteria.
 */
@Getter
@Setter
public class FilterRequest {

    /**
     * Indicates the page number which used for prepare pagination response from JPA query.
     */
    @NotNull(message = "{NotNull.RecordFilter.page}")
    private int page;
    /**
     * Indicates the size which used for prepare pagination response from JPA query.
     */
    @NotNull(message = "{NotNull.RecordFilter.size}")
    @Min(value = 10)
    private int size;
    /**
     * Indicates the sorting objects that used in JPA query.
     */
    private Sort sort;
    /**
     * Indicates the criteria that needs to used for JPA query.
     */
    private CriteriaOperator criteriaOperator = CriteriaOperator.AND;
    /**
     * Indicates the criteria that needs to used for search using JPA query.
     */
    @Valid
    private List<FilterCriteria> criteria;

    /**
     * Method used for append new criteria to the given FilterRequest.
     *
     * @param fieldName  - Indicates the Column name
     * @param operator   - Indicates operator
     * @param recordType - Indicates the values
     */
    public void appendCriteria(String fieldName, Operator operator, String... recordType) {
        this.appendCriteria(fieldName, operator, Arrays.asList(recordType));
    }

    /**
     * Method used for append new criteria to the given FilterRequest.
     *
     * @param fieldName  - Indicates the Column name
     * @param operator   - Indicates operator
     * @param recordType - Indicates the values
     */
    public void appendCriteria(String fieldName, Operator operator, List<String> recordType) {
        List<FilterCriteria> filterCriteria = this.getCriteria() != null ? this.getCriteria() : new ArrayList<>();
        filterCriteria.add(new FilterCriteria(fieldName, operator.getOperatorValue(), recordType));
        this.setCriteria(filterCriteria);
    }

}