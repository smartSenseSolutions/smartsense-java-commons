/*
 * Copyright 2024 smartSense Consulting Solutions Pvt. Ltd
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

package com.smartsensesolutions.commons.dao.filter;

import com.smartsensesolutions.commons.dao.filter.sort.Sort;
import com.smartsensesolutions.commons.dao.filter.sort.SortType;
import com.smartsensesolutions.commons.dao.operator.CriteriaOperator;
import com.smartsensesolutions.commons.dao.operator.Operator;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.util.CollectionUtils;

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
     * Indicates the page number which used to prepare pagination response from JPA query.
     */
    @NotNull(message = "{NotNull.RecordFilter.page}")
    private int page;

    /**
     * Indicates the size which used to prepare pagination response from JPA query.
     */
    @NotNull(message = "{NotNull.RecordFilter.size}")
    @Min(value = 1)
    private int size;

    /**
     * Indicates the sorting objects that used in JPA query.
     */
    private List<Sort> sort;

    /**
     * Indicates the logical operator that will be used between two criteria/(criteria and orCriteria)
     */
    private CriteriaOperator criteriaOperator = CriteriaOperator.AND;

    /**
     * Indicates the criteria that needs to used for search using JPA query.
     */
    @Valid
    private List<Criteria> criteria;

    /**
     * Indicates the "or" criteria list that needs to used for search using JPA query.
     */
    @Valid
    private List<Criteria> orCriteria;

    /**
     * Append ascending order by for provided fields
     *
     * @param fieldNames - Indicate entity field name
     * @return FilterRequest
     */
    public FilterRequest appendSort(String... fieldNames) {
        for (String fieldName : fieldNames) {
            appendSort(fieldName, SortType.ASC);
        }
        return this;
    }

    /**
     * Append order by for provided field with order
     *
     * @param fieldName - Indicate entity field name
     * @param order     - Indicate order
     * @return FilterRequest
     */
    public FilterRequest appendSort(String fieldName, SortType order) {
        sort = sort != null ? sort : new ArrayList<>();
        sort.add(new Sort(fieldName, order));
        return this;
    }

    /**
     * remove from sort list by field name
     *
     * @param fieldName - Indicate entity field name
     * @return FilterRequest
     */
    public FilterRequest removeSort(String fieldName) {
        if (CollectionUtils.isEmpty(sort)) {
            return this;
        }
        sort.removeIf((next) -> next.column().equals(fieldName));
        return this;
    }


    /**
     * Builder method used to add new Criteria to the {@code criteria} field
     *
     * @param fieldName - Indicates the Column name
     * @param operator  - Indicates operator
     * @param values    - Indicates the Varargs values
     * @return FilterRequest
     */
    public FilterRequest appendCriteria(String fieldName, Operator operator, Object... values) {
        appendCriteria(fieldName, operator, Arrays.asList(values));
        return this;
    }

    /**
     * Builder method used to add new Criteria to the {@code criteria} field
     *
     * @param fieldName - Indicates the Column name
     * @param operator  - Indicates operator
     * @param values    - Indicates the values
     * @return FilterRequest
     */
    public FilterRequest appendCriteria(String fieldName, Operator operator, List<Object> values) {
        criteria = criteria != null ? criteria : new ArrayList<>();
        criteria.add(new Criteria(fieldName, operator, values));
        return this;
    }

    /**
     * Remove all criteria with provided field name from {@code criteria} field.
     *
     * @param fieldName the field name
     * @return FilterRequest
     */
    public FilterRequest removeCriteria(String fieldName) {
        if (CollectionUtils.isEmpty(criteria)) {
            return this;
        }
        criteria.removeIf((next) -> next.column().equals(fieldName));
        return this;
    }

    /**
     * Builder method used to add new Criteria to the {@code orCriteria} field
     *
     * @param fieldName - Indicates the Column name
     * @param operator  - Indicates operator
     * @param values    - Indicates the Varargs values
     * @return FilterRequest
     */
    public FilterRequest appendOrCriteria(String fieldName, Operator operator, String... values) {
        appendOrCriteria(fieldName, operator, Arrays.asList(values));
        return this;
    }

    /**
     * Builder method used to add new Criteria to the {@code orCriteria} field
     *
     * @param fieldName - Indicates the Column name
     * @param operator  - Indicates operator
     * @param values    - Indicates the values
     * @return FilterRequest
     */
    public FilterRequest appendOrCriteria(String fieldName, Operator operator, List<Object> values) {
        orCriteria = orCriteria != null ? orCriteria : new ArrayList<>();
        orCriteria.add(new Criteria(fieldName, operator, values));
        return this;
    }

    /**
     * Remove all criteria with provided field name from {@code orCriteria} field.
     *
     * @param fieldName the field name
     * @return FilterRequest
     */
    public FilterRequest removeOrCriteria(String fieldName) {
        if (CollectionUtils.isEmpty(orCriteria)) {
            return this;
        }
        orCriteria.removeIf((next) -> next.column().equals(fieldName));
        return this;
    }
}