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

package com.smartsensesolutions.java.commons.specification;

import com.smartsensesolutions.java.commons.filter.Criteria;
import com.smartsensesolutions.java.commons.specification.function.MultiValuePredicateProvider;
import com.smartsensesolutions.java.commons.specification.function.NoValuePredicateProvider;
import com.smartsensesolutions.java.commons.specification.function.PredicateProvider;
import jakarta.persistence.criteria.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Specification will be used to generate specification for {@code T} type of entity JPA query.
 *
 * @param <T> - Indicates @{@link jakarta.persistence.Entity} class.
 */
@Component
public class SpecificationUtil<T> {
    private final String TABLE_FIELD_SEPARATOR = "\\.";
    private final String FIELD_SEPARATOR = ",";

    public Specification<T> generateOrSpecification(List<Criteria> criteriaList) {
        return (Root<T> root, CriteriaQuery<?> cq, CriteriaBuilder cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            criteriaList.forEach(criteria -> predicates.add(getPredicate(criteria, root, cb)));
            return cb.or(toArray(predicates));
        };
    }

    public Specification<T> generateAndSpecification(List<Criteria> criteriaList) {
        return (Root<T> root, CriteriaQuery<?> cq, CriteriaBuilder cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            criteriaList.forEach(criteria -> predicates.add(getPredicate(criteria, root, cb)));
            return cb.and(toArray(predicates));
        };
    }

    private Predicate getPredicate(Criteria criteria, Root<T> root, CriteriaBuilder cb) {
        return switch (criteria.getOperator()) {
            case CONTAIN -> getContainsPredicates(criteria, root, cb);
            case CONTAIN_WITH_WILDCARD -> getContainsWildcardPredicates(criteria, root, cb);
            case NOT_CONTAIN -> getNotContainPredicates(criteria, root, cb);
            case NOT_CONTAIN_WITH_WILDCARD -> getNotContainsWildcardPredicates(criteria, root, cb);
            case EQUALS -> getEqualsPredicate(criteria, root, cb);
            case NOT_EQUAL -> getNotEqualsPredicate(criteria, root, cb);
            case NULL -> getIsNullPredicate(criteria, root, cb);
            case NOT_NULL -> getIsNotNullPredicate(criteria, root, cb);
            case IN -> getInPredicate(criteria, root, cb);
            case NOT_IN -> getNotInPredicate(criteria, root, cb);
            case TRUE -> getTruePredicate(criteria, root, cb);
            case FALSE -> getFalsePredicate(criteria, root, cb);
            case LESSER_THAN -> getLesserThanPredicate(criteria, root, cb);
            case LESSER_EQUALS -> getLesserEqualPredicate(criteria, root, cb);
            case GREATER_THAN -> getGretherThanPredicate(criteria, root, cb);
            case GREATER_EQUALS -> getGretherEqualPredicate(criteria, root, cb);
        };
    }

    private Predicate getContainsPredicates(Criteria criteria, Root<T> root, CriteriaBuilder cb) {
        validateValue(criteria.getValues());
        if (criteria.getColumn().split(TABLE_FIELD_SEPARATOR).length > 1) {
            return getContainJoinPredicate(criteria, root, cb, (c, p, v) -> c.like(c.lower(p.as(String.class)), getContainsEscValue(v.toString())));
        }
        return getContainPredicate(criteria, root, cb, (c, p, v) -> c.like(c.lower(p.as(String.class)),
                getContainsEscValue(v.toString()), '\\'));
    }

    private Predicate getContainsWildcardPredicates(Criteria criteria, Root<T> root, CriteriaBuilder cb) {
        validateValue(criteria.getValues());
        if (criteria.getColumn().split(TABLE_FIELD_SEPARATOR).length > 1) {
            return getContainJoinPredicate(criteria, root, cb, (c, p, v) -> c.like(c.lower(p.as(String.class)), v.toString()));
        }
        return getContainPredicate(criteria, root, cb, (c, p, v) -> c.like(c.lower(p.as(String.class)), v.toString()));
    }

    private Predicate getNotContainPredicates(Criteria criteria, Root<T> root, CriteriaBuilder cb) {
        validateValue(criteria.getValues());
        if (criteria.getColumn().split(TABLE_FIELD_SEPARATOR).length > 1) {
            return getContainJoinPredicate(criteria, root, cb, (c, p, v) -> c.notLike(c.lower(p.as(String.class)), getContainsEscValue(v.toString())));
        }
        return getContainPredicate(criteria, root, cb, (c, p, v) -> c.notLike(c.lower(p.as(String.class)), getContainsEscValue(v.toString())));
    }

    private Predicate getNotContainsWildcardPredicates(Criteria criteria, Root<T> root, CriteriaBuilder cb) {
        validateValue(criteria.getValues());
        if (criteria.getColumn().split(TABLE_FIELD_SEPARATOR).length > 1) {
            return getContainJoinPredicate(criteria, root, cb, (c, p, v) -> c.notLike(c.lower(p.as(String.class)), v.toString()));
        }
        return getContainPredicate(criteria, root, cb, (c, p, v) -> c.notLike(c.lower(p.as(String.class)), v.toString()));
    }

    private Predicate getEqualsPredicate(Criteria criteria, Root<T> root, CriteriaBuilder cb) {
        validateValue(criteria.getValues());
        if (criteria.getColumn().split(TABLE_FIELD_SEPARATOR).length > 1) {
            return getJoinPredicate(criteria, root, cb, (c, p, v) -> c.equal(p, v));
        }
        return getPredicate(criteria, root, cb, (c, p, v) -> c.equal(p, v));
    }

    private Predicate getNotEqualsPredicate(Criteria criteria, Root<T> root, CriteriaBuilder cb) {
        validateValue(criteria.getValues());
        if (criteria.getColumn().split(TABLE_FIELD_SEPARATOR).length > 1) {
            return getJoinPredicate(criteria, root, cb, (c, p, v) -> c.notEqual(p, v));
        }
        return getPredicate(criteria, root, cb, (c, p, v) -> c.notEqual(p, v));
    }

    private Predicate getIsNullPredicate(Criteria criteria, Root<T> root, CriteriaBuilder cb) {
        if (criteria.getColumn().split(TABLE_FIELD_SEPARATOR).length > 1) {
            return getNoValueJoinPredicate(criteria, root, cb, (c, p) -> c.isNull(p));
        }
        return getNoValuePredicate(criteria, root, cb, (c, p) -> c.isNull(p));
    }

    private Predicate getIsNotNullPredicate(Criteria criteria, Root<T> root, CriteriaBuilder cb) {
        if (criteria.getColumn().split(TABLE_FIELD_SEPARATOR).length > 1) {
            return getNoValueJoinPredicate(criteria, root, cb, CriteriaBuilder::isNotNull);
        }
        return getNoValuePredicate(criteria, root, cb, CriteriaBuilder::isNotNull);
    }

    private Predicate getInPredicate(Criteria criteria, Root<T> root, CriteriaBuilder cb) {
        validateValue(criteria.getValues());
        if (criteria.getColumn().split(TABLE_FIELD_SEPARATOR).length > 1) {
            return getMultiValueJoinPredicate(criteria, root, cb, (c, p, s) -> p.in(s));
        }
        return getMultiValuePredicate(criteria, root, cb, (c, p, s) -> p.in(s));
    }

    private Predicate getNotInPredicate(Criteria criteria, Root<T> root, CriteriaBuilder cb) {
        validateValue(criteria.getValues());
        if (criteria.getColumn().split(TABLE_FIELD_SEPARATOR).length > 1) {
            return getMultiValueJoinPredicate(criteria, root, cb, (c, p, s) -> c.not(p.in(s)));
        }
        return getMultiValuePredicate(criteria, root, cb, (c, p, s) -> c.not(p.in(s)));
    }

    private Predicate getTruePredicate(Criteria criteria, Root<T> root, CriteriaBuilder cb) {
        if (criteria.getColumn().split(TABLE_FIELD_SEPARATOR).length > 1) {
            return getNoValueJoinPredicate(criteria, root, cb, CriteriaBuilder::isTrue);
        }
        return getNoValuePredicate(criteria, root, cb, CriteriaBuilder::isTrue);
    }

    private Predicate getFalsePredicate(Criteria criteria, Root<T> root, CriteriaBuilder cb) {
        if (criteria.getColumn().split(TABLE_FIELD_SEPARATOR).length > 1) {
            return getNoValueJoinPredicate(criteria, root, cb, (c, p) -> c.isFalse(p));
        }
        return getNoValuePredicate(criteria, root, cb, (c, p) -> c.isFalse(p));
    }

    private Predicate getLesserThanPredicate(Criteria criteria, Root<T> root, CriteriaBuilder cb) {
        validateValue(criteria.getValues());
        if (criteria.getColumn().split(TABLE_FIELD_SEPARATOR).length > 1) {
            return getJoinPredicate(criteria, root, cb, (c, p, v) -> c.lessThan(p, (Comparable) v));
        }
        return getPredicate(criteria, root, cb, (c, p, v) -> c.lessThan(p, (Comparable) v));
    }

    private Predicate getLesserEqualPredicate(Criteria criteria, Root<T> root, CriteriaBuilder cb) {
        validateValue(criteria.getValues());
        if (criteria.getColumn().split(TABLE_FIELD_SEPARATOR).length > 1) {
            return getJoinPredicate(criteria, root, cb, (c, p, v) -> c.lessThanOrEqualTo(p, (Comparable) v));
        }
        return getPredicate(criteria, root, cb, (c, p, v) -> c.lessThanOrEqualTo(p, (Comparable) v));
    }

    private Predicate getGretherThanPredicate(Criteria criteria, Root<T> root, CriteriaBuilder cb) {
        validateValue(criteria.getValues());
        if (criteria.getColumn().split(TABLE_FIELD_SEPARATOR).length > 1) {
            return getJoinPredicate(criteria, root, cb, (c, p, v) -> c.greaterThan(p, (Comparable) v));
        }
        return getPredicate(criteria, root, cb, (c, p, v) -> c.greaterThan(p, (Comparable) v));
    }

    private Predicate getGretherEqualPredicate(Criteria criteria, Root<T> root, CriteriaBuilder cb) {
        validateValue(criteria.getValues());
        if (criteria.getColumn().split(TABLE_FIELD_SEPARATOR).length > 1) {
            return getJoinPredicate(criteria, root, cb, (c, p, v) -> c.greaterThanOrEqualTo(p, (Comparable) v));
        }
        return getPredicate(criteria, root, cb, (c, p, v) -> c.greaterThanOrEqualTo(p, (Comparable) v));
    }

    private <D> Predicate getNoValueJoinPredicate(Criteria criteria, Root<T> root, CriteriaBuilder cb, NoValuePredicateProvider provider) {
        String name = criteria.getColumn();
        Join<Object, Object> joinTable = getJoinTable(root, name);
        String[] joinFields = getFields(name);
        List<Predicate> predicates = new ArrayList<>();
        for (String field : joinFields) {
            predicates.add(provider.getPredicate(cb, joinTable.get(field)));
        }
        return cb.or(toArray(predicates));
    }

    private Predicate getNoValuePredicate(Criteria criteria, Root<T> root, CriteriaBuilder cb, NoValuePredicateProvider provider) {
        String[] fields = getFields(criteria.getColumn());
        List<Predicate> predicates = new ArrayList<>();
        for (String field : fields) {
            predicates.add(provider.getPredicate(cb, root.get(field)));
        }
        return cb.or(toArray(predicates));
    }

    private Predicate getJoinPredicate(Criteria criteria, Root<T> root, CriteriaBuilder cb, PredicateProvider provider) {
        String name = criteria.getColumn();
        Join<Object, Object> joinTable = getJoinTable(root, name);
        String[] joinFields = getFields(name);
        List<Predicate> predicates = new ArrayList<>();
        for (String field : joinFields) {
            Object value = getValue(joinTable.get(field), criteria.getValues().get(0));
            predicates.add(provider.getPredicate(cb, joinTable.get(field), value));
        }
        return cb.or(toArray(predicates));
    }

    private Predicate getPredicate(Criteria criteria, Root<T> root, CriteriaBuilder cb, PredicateProvider provider) {
        String[] fields = getFields(criteria.getColumn());
        List<Predicate> predicates = new ArrayList<>();
        for (String field : fields) {
            Object value = getValue(root.get(field), criteria.getValues().get(0));
            predicates.add(provider.getPredicate(cb, root.get(field), value));
        }
        return cb.or(toArray(predicates));
    }

    private <D> Predicate getMultiValueJoinPredicate(Criteria criteria, Root<T> root, CriteriaBuilder cb, MultiValuePredicateProvider<D> provider) {
        String name = criteria.getColumn();
        Join<Object, Object> joinTable = getJoinTable(root, name);
        String[] joinFields = getFields(name);
        List<Predicate> predicates = new ArrayList<>();
        for (String field : joinFields) {
            final Set<Object> valueSet = criteria.getValues().stream()
                    .map(value -> getValue(joinTable.get(field), value))
                    .collect(Collectors.toSet());
            predicates.add(provider.getPredicate(cb, joinTable.get(field), valueSet));
        }
        return cb.or(toArray(predicates));
    }

    private <D> Predicate getMultiValuePredicate(Criteria criteria, Root<T> root, CriteriaBuilder cb, MultiValuePredicateProvider<D> provider) {
        String[] fields = getFields(criteria.getColumn());
        List<Predicate> predicates = new ArrayList<>();
        for (String field : fields) {
            final Set<Object> valueSet = criteria.getValues().stream()
                    .map(value -> getValue(root.get(field), value))
                    .collect(Collectors.toSet());
            predicates.add(provider.getPredicate(cb, root.get(field), valueSet));
        }
        return cb.or(toArray(predicates));
    }

    private Predicate getContainJoinPredicate(Criteria criteria, Root<T> root, CriteriaBuilder cb, PredicateProvider provider) {
        String name = criteria.getColumn();
        Join<Object, Object> joinTable = getJoinTable(root, name);
        String[] joinFields = getFields(name);
        List<Predicate> predicates = new ArrayList<>();
        for (String field : joinFields) {
            criteria.getValues().stream()
                    .map(value -> provider.getPredicate(cb, joinTable.get(field), value))
                    .forEach(predicates::add);
        }
        return cb.or(toArray(predicates));
    }

    private Predicate getContainPredicate(Criteria criteria, Root<T> root, CriteriaBuilder cb, PredicateProvider provider) {
        List<Predicate> predicates = new ArrayList<>();
        String[] fields = getFields(criteria.getColumn());
        for (String field : fields) {
            criteria.getValues().stream()
                    .map(value -> provider.getPredicate(cb, root.get(field), value))
                    .forEach(predicates::add);
        }
        return cb.or(toArray(predicates));
    }

    private Predicate[] toArray(List<Predicate> predicates) {
        return predicates.toArray(new Predicate[0]);
    }

    private Object getValue(Path<T> path, String value) {
        if (Objects.equals(path.getJavaType().getName(), "java.util.Date")) {
            return new Date(Long.parseLong(value));
        }
        if (path.getJavaType().isEnum()) {
            for (T enumConstant : path.getJavaType().getEnumConstants()) {
                if (enumConstant.toString().equals(value)) {
                    return enumConstant;
                }
            }
            throw new IllegalArgumentException("Invalid Enum Value");
        }
        return value;
    }

    private String getContainsEscValue(final String value) {
        String newVal = value
                .replace("\\", "\\\\")
                .replace("_", "\\_")
                .replace("[", "\\[")
                .replace("]", "\\]")
                .replace("-", "\\-")
                .replace("%", "\\%");
        return "%".concat(newVal).concat("%").toLowerCase();
    }

    private void validateValue(List<String> values) {
        if (CollectionUtils.isEmpty(values)) {
            throw new IllegalArgumentException("value/s require");
        }
    }

    private Join<Object, Object> getJoinTable(Root<T> root, String column) {
        String[] split = column.split(TABLE_FIELD_SEPARATOR);
        Join<Object, Object> join = root.join(split[0], JoinType.LEFT);
        for (int i = 1; i < split.length - 1; i++) {
            join = join.join(split[i], JoinType.LEFT);
        }
        return join;
    }

    private String[] getFields(String name) {
        String[] split = name.split(TABLE_FIELD_SEPARATOR);
        return split[split.length - 1].split(FIELD_SEPARATOR);
    }
}