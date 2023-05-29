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

package com.smartsensesolutions.java.commons.specification;

import com.smartsensesolutions.java.commons.filter.FilterCriteria;
import com.smartsensesolutions.java.commons.operator.Operator;
import jakarta.persistence.criteria.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Specification will be used for generate the JPA query.
 *
 * @param <T> - Indicates @{@link jakarta.persistence.Entity} class.
 */
@Component
public class SpecificationUtil<T> {

    private static <T> Predicate getContainsPredicates(FilterCriteria filterCriteria, Root<T> root, CriteriaBuilder cb) {
        validateValue(filterCriteria.getValues());
        List<Predicate> predicates = new ArrayList<>();
        filterCriteria.getValues().forEach(value -> predicates.add(cb.like(root.get(filterCriteria.getColumn()).as(String.class), getContainsValue(value))));
        return cb.or(predicates.toArray(new Predicate[0]));
    }

    public Specification<T> generateOrSpecification(List<FilterCriteria> criteria) {
        return (final Root<T> root, final CriteriaQuery<?> cq, final CriteriaBuilder cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            criteria.forEach(filterCriteria -> predicates.add(this.getPredicate(filterCriteria, root, cb)));
            return cb.or(predicates.toArray(new Predicate[predicates.size()]));
        };
    }

    private static <T> Predicate getNotContainPredicates(FilterCriteria filterCriteria, Root<T> root, CriteriaBuilder cb) {
        validateValue(filterCriteria.getValues());
        List<Predicate> predicates = new ArrayList<>();
        filterCriteria.getValues().forEach(value -> predicates.add(cb.notLike(root.get(filterCriteria.getColumn()), getContainsValue(value))));
        return cb.or(predicates.toArray(new Predicate[0]));
    }

    private static String getContainsValue(String value) {
        return "%".concat(value).concat("%");
    }

    private static void validateValue(List<String> values) {
        if (CollectionUtils.isEmpty(values)) {
            throw new IllegalArgumentException("value/s require");
        }
    }

    public Specification<T> generateSpecification(List<FilterCriteria> criteria) {
        return (final Root<T> root, final CriteriaQuery<?> cq, final CriteriaBuilder cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            criteria.forEach(filterCriteria -> predicates.add(this.getPredicate(filterCriteria, root, cb)));
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    private Predicate getPredicate(FilterCriteria filterCriteria, Root<T> root, CriteriaBuilder cb) {
        Predicate predicate;
        Operator operator = Operator.getOperator(filterCriteria.getOperator());
        switch (operator) {
            case CONTAIN:
                predicate = getContainsPredicates(filterCriteria, root, cb);
                break;
            case NOT_CONTAIN:
                predicate = getNotContainPredicates(filterCriteria, root, cb);
                break;
            case EQUALS:
                predicate = this.getEqualsPredicate(filterCriteria, root, cb);
                break;
            case NOT_EQUAL:
                predicate = this.getNotEqualsPredicate(filterCriteria, root, cb);
                break;
            case NULL:
                predicate = this.getIsNullPredicate(filterCriteria, root, cb);
                break;
            case NOT_NULL:
                predicate = this.getIsNotNullPredicate(filterCriteria, root, cb);
                break;
            case IN:
                predicate = this.getInPredicate(filterCriteria, root);
                break;
            case NOT_IN:
                predicate = this.getNotInPredicate(filterCriteria, root, cb);
                break;
            case TRUE:
                predicate = this.getTruePredicate(filterCriteria, root, cb);
                break;
            case FALSE:
                predicate = this.getFalsePredicate(filterCriteria, root, cb);
                break;
            case LESSER_THAN:
                predicate = this.getLesserThanPredicate(filterCriteria, root, cb);
                break;
            case LESSER_EQUALS:
                predicate = this.getLesserEqualPredicate(filterCriteria, root, cb);
                break;
            case GREATER_THAN:
                predicate = this.getGretherThanPredicate(filterCriteria, root, cb);
                break;
            case GREATER_EQUALS:
                predicate = this.getGretherEqualPredicate(filterCriteria, root, cb);
                break;
            case JOIN_EQUALS:
                predicate = this.prepareJoinPredicate(filterCriteria, root, cb);
                break;
            case JOIN_IN:
                predicate = this.prepareJoinInPredicate(filterCriteria, root);
                break;
            case JOIN_LIKE:
                predicate = this.prepareJoinLikePredicate(filterCriteria, root, cb);
                break;
            default:
                throw new IllegalArgumentException("operator not implemented");
        }
        return predicate;
    }

    private Predicate getEqualsPredicate(FilterCriteria filterCriteria, Root<T> root, CriteriaBuilder criteriaBuilder) {
        validateValue(filterCriteria.getValues());
        Object o = this.getValue(root.get(filterCriteria.getColumn()), filterCriteria.getValues().get(0));
        return criteriaBuilder.equal(root.get(filterCriteria.getColumn()), o);
    }

    private Predicate getNotEqualsPredicate(FilterCriteria filterCriteria, Root<T> root, CriteriaBuilder criteriaBuilder) {
        validateValue(filterCriteria.getValues());
        Object o = this.getValue(root.get(filterCriteria.getColumn()), filterCriteria.getValues().get(0));
        return criteriaBuilder.notEqual(root.get(filterCriteria.getColumn()), o);
    }

    private Predicate getInPredicate(FilterCriteria filterCriteria, Root<T> root) {
        validateValue(filterCriteria.getValues());
        Set<Object> set = filterCriteria.getValues().stream().map(value -> this.getValue(root.get(filterCriteria.getColumn()), value)).collect(Collectors.toSet());
        return root.get(filterCriteria.getColumn()).in(set);
    }

    private Predicate getIsNullPredicate(FilterCriteria filterCriteria, Root<T> root, CriteriaBuilder criteriaBuilder) {
        return criteriaBuilder.isNull(root.get(filterCriteria.getColumn()));
    }

    private Predicate getIsNotNullPredicate(FilterCriteria filterCriteria, Root<T> root, CriteriaBuilder criteriaBuilder) {
        return criteriaBuilder.isNotNull(root.get(filterCriteria.getColumn()));
    }

    private Predicate getNotInPredicate(FilterCriteria filterCriteria, Root<T> root, CriteriaBuilder cb) {
        validateValue(filterCriteria.getValues());
        Set<Object> set = filterCriteria.getValues().stream().map(value -> this.getValue(root.get(filterCriteria.getColumn()), value)).collect(Collectors.toSet());
        return cb.not(root.get(filterCriteria.getColumn()).in(set));
    }

    private Predicate getTruePredicate(FilterCriteria filterCriteria, Root<T> root, CriteriaBuilder criteriaBuilder) {
        return criteriaBuilder.isTrue(root.get(filterCriteria.getColumn()));
    }

    private Predicate getFalsePredicate(FilterCriteria filterCriteria, Root<T> root, CriteriaBuilder criteriaBuilder) {
        return criteriaBuilder.not(criteriaBuilder.isTrue(root.get(filterCriteria.getColumn())));
    }

    private Predicate getLesserThanPredicate(FilterCriteria filterCriteria, Root<T> root, CriteriaBuilder criteriaBuilder) {
        validateValue(filterCriteria.getValues());
        Object o = this.getValue(root.get(filterCriteria.getColumn()), filterCriteria.getValues().get(0));
        return criteriaBuilder.lessThan(root.get(filterCriteria.getColumn()), (Comparable) o);
    }

    private Predicate getLesserEqualPredicate(FilterCriteria filterCriteria, Root<T> root, CriteriaBuilder criteriaBuilder) {
        validateValue(filterCriteria.getValues());
        Object o = this.getValue(root.get(filterCriteria.getColumn()), filterCriteria.getValues().get(0));
        return criteriaBuilder.lessThanOrEqualTo(root.get(filterCriteria.getColumn()), (Comparable) o);
    }

    private Predicate getGretherThanPredicate(FilterCriteria filterCriteria, Root<T> root, CriteriaBuilder criteriaBuilder) {
        validateValue(filterCriteria.getValues());
        Object o = this.getValue(root.get(filterCriteria.getColumn()), filterCriteria.getValues().get(0));
        return criteriaBuilder.greaterThan(root.get(filterCriteria.getColumn()), (Comparable) o);
    }

    private Predicate getGretherEqualPredicate(FilterCriteria filterCriteria, Root<T> root, CriteriaBuilder criteriaBuilder) {
        validateValue(filterCriteria.getValues());
        Object o = this.getValue(root.get(filterCriteria.getColumn()), filterCriteria.getValues().get(0));
        return criteriaBuilder.greaterThanOrEqualTo(root.get(filterCriteria.getColumn()), (Comparable) o);
    }

    private Predicate prepareJoinPredicate(FilterCriteria filterCriteria, Root<T> root, CriteriaBuilder criteriaBuilder) {
        validateValue(filterCriteria.getValues());
        String[] split = filterCriteria.getColumn().split("_");
        Join<Object, Object> join = root.join(split[0]);
        return criteriaBuilder.equal(join.get(split[1]), filterCriteria.getValues().get(0));
    }

    private Predicate prepareJoinInPredicate(FilterCriteria filterCriteria, Root<T> root) {
        validateValue(filterCriteria.getValues());
        String[] split = filterCriteria.getColumn().split("_");
        Join<Object, Object> join = root.join(split[0]);
        return join.get(split[1]).in(filterCriteria.getValues());
    }

    private Predicate prepareJoinLikePredicate(FilterCriteria filterCriteria, Root<T> root, CriteriaBuilder criteriaBuilder) {
        String[] split = filterCriteria.getColumn().split("_");
        validateValue(filterCriteria.getValues());
        List<Predicate> predicates = new ArrayList<>();
        Join<Object, Object> join = root.join(split[0], JoinType.LEFT);
        filterCriteria.getValues().forEach(value -> predicates.add(criteriaBuilder.like(join.get(split[1]).as(String.class), getContainsValue(value))));
        return criteriaBuilder.or(predicates.toArray(new Predicate[0]));
    }

    private Object getValue(Path<T> path, String value) {
        if (path instanceof java.util.Date) {
            return new Date(Long.parseLong(value));
        }
        return value;
    }
}
