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

package com.smartsensesolutions.commons.dao.specification;

import com.smartsensesolutions.commons.dao.base.BaseEntity;
import com.smartsensesolutions.commons.dao.filter.Criteria;
import com.smartsensesolutions.commons.dao.specification.function.MultiValuePredicateProvider;
import com.smartsensesolutions.commons.dao.specification.function.NoValuePredicateProvider;
import com.smartsensesolutions.commons.dao.specification.function.PredicateProvider;
import com.smartsensesolutions.commons.dao.specification.function.StringPredicateProvider;
import jakarta.persistence.criteria.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Specification will be used to generate specification for {@code T} type of entity JPA query.
 *
 * @param <T> - Indicates @{@link jakarta.persistence.Entity} class.
 */
@Component
@RequiredArgsConstructor
public class SpecificationUtil<T extends BaseEntity> {
    private final String TABLE_FIELD_SEPARATOR = "\\.";
    private final String FIELD_SEPARATOR = ",";
    private final SpecificationValueConverter valueConverter;

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
        return switch (criteria.operator()) {
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
        validateValue(criteria.values());
        if (criteria.column().split(TABLE_FIELD_SEPARATOR).length > 1) {
            return getContainJoinPredicate(criteria, root, cb, (c, p, v) -> c.like(c.lower(p.as(String.class)), getContainsEscValue(v.toString())));
        }
        return getContainPredicate(criteria, root, cb, (c, p, v) -> c.like(c.lower(p.as(String.class)), getContainsEscValue(v.toString()), '\\'));
    }

    private Predicate getContainsWildcardPredicates(Criteria criteria, Root<T> root, CriteriaBuilder cb) {
        validateValue(criteria.values());
        if (criteria.column().split(TABLE_FIELD_SEPARATOR).length > 1) {
            return getContainJoinPredicate(criteria, root, cb, (c, p, v) -> c.like(c.lower(p.as(String.class)), v.toString()));
        }
        return getContainPredicate(criteria, root, cb, (c, p, v) -> c.like(c.lower(p.as(String.class)), v.toString()));
    }

    private Predicate getNotContainPredicates(Criteria criteria, Root<T> root, CriteriaBuilder cb) {
        validateValue(criteria.values());
        if (criteria.column().split(TABLE_FIELD_SEPARATOR).length > 1) {
            return getContainJoinPredicate(criteria, root, cb, (c, p, v) -> c.notLike(c.lower(p.as(String.class)), getContainsEscValue(v.toString())));
        }
        return getContainPredicate(criteria, root, cb, (c, p, v) -> c.notLike(c.lower(p.as(String.class)), getContainsEscValue(v.toString())));
    }

    private Predicate getNotContainsWildcardPredicates(Criteria criteria, Root<T> root, CriteriaBuilder cb) {
        validateValue(criteria.values());
        if (criteria.column().split(TABLE_FIELD_SEPARATOR).length > 1) {
            return getContainJoinPredicate(criteria, root, cb, (c, p, v) -> c.notLike(c.lower(p.as(String.class)), v.toString()));
        }
        return getContainPredicate(criteria, root, cb, (c, p, v) -> c.notLike(c.lower(p.as(String.class)), v.toString()));
    }

    private Predicate getEqualsPredicate(Criteria criteria, Root<T> root, CriteriaBuilder cb) {
        validateValue(criteria.values());
        if (criteria.column().split(TABLE_FIELD_SEPARATOR).length > 1) {
            return getJoinPredicate(criteria, root, cb, CriteriaBuilder::equal);
        }
        return getPredicate(criteria, root, cb, CriteriaBuilder::equal);
    }

    private Predicate getNotEqualsPredicate(Criteria criteria, Root<T> root, CriteriaBuilder cb) {
        validateValue(criteria.values());
        if (criteria.column().split(TABLE_FIELD_SEPARATOR).length > 1) {
            return getJoinPredicate(criteria, root, cb, CriteriaBuilder::notEqual);
        }
        return getPredicate(criteria, root, cb, CriteriaBuilder::notEqual);
    }

    private Predicate getIsNullPredicate(Criteria criteria, Root<T> root, CriteriaBuilder cb) {
        if (criteria.column().split(TABLE_FIELD_SEPARATOR).length > 1) {
            return getNoValueJoinPredicate(criteria, root, cb, CriteriaBuilder::isNull);
        }
        return getNoValuePredicate(criteria, root, cb, CriteriaBuilder::isNull);
    }

    private Predicate getIsNotNullPredicate(Criteria criteria, Root<T> root, CriteriaBuilder cb) {
        if (criteria.column().split(TABLE_FIELD_SEPARATOR).length > 1) {
            return getNoValueJoinPredicate(criteria, root, cb, CriteriaBuilder::isNotNull);
        }
        return getNoValuePredicate(criteria, root, cb, CriteriaBuilder::isNotNull);
    }

    private Predicate getInPredicate(Criteria criteria, Root<T> root, CriteriaBuilder cb) {
        validateValue(criteria.values());
        if (criteria.column().split(TABLE_FIELD_SEPARATOR).length > 1) {
            return getMultiValueJoinPredicate(criteria, root, cb, (c, p, s) -> p.in(s));
        }
        return getMultiValuePredicate(criteria, root, cb, (c, p, s) -> p.in(s));
    }

    private Predicate getNotInPredicate(Criteria criteria, Root<T> root, CriteriaBuilder cb) {
        validateValue(criteria.values());
        if (criteria.column().split(TABLE_FIELD_SEPARATOR).length > 1) {
            return getMultiValueJoinPredicate(criteria, root, cb, (c, p, s) -> c.not(p.in(s)));
        }
        return getMultiValuePredicate(criteria, root, cb, (c, p, s) -> c.not(p.in(s)));
    }

    private Predicate getTruePredicate(Criteria criteria, Root<T> root, CriteriaBuilder cb) {
        if (criteria.column().split(TABLE_FIELD_SEPARATOR).length > 1) {
            return getNoValueJoinPredicate(criteria, root, cb, CriteriaBuilder::isTrue);
        }
        return getNoValuePredicate(criteria, root, cb, CriteriaBuilder::isTrue);
    }

    private Predicate getFalsePredicate(Criteria criteria, Root<T> root, CriteriaBuilder cb) {
        if (criteria.column().split(TABLE_FIELD_SEPARATOR).length > 1) {
            return getNoValueJoinPredicate(criteria, root, cb, CriteriaBuilder::isFalse);
        }
        return getNoValuePredicate(criteria, root, cb, CriteriaBuilder::isFalse);
    }

    private Predicate getLesserThanPredicate(Criteria criteria, Root<T> root, CriteriaBuilder cb) {
        validateValue(criteria.values());
        if (criteria.column().split(TABLE_FIELD_SEPARATOR).length > 1) {
            return getJoinPredicate(criteria, root, cb, (c, p, v) -> c.lessThan(p, (Comparable) v));
        }
        return getPredicate(criteria, root, cb, (c, p, v) -> c.lessThan(p, (Comparable) v));
    }

    private Predicate getLesserEqualPredicate(Criteria criteria, Root<T> root, CriteriaBuilder cb) {
        validateValue(criteria.values());
        if (criteria.column().split(TABLE_FIELD_SEPARATOR).length > 1) {
            return getJoinPredicate(criteria, root, cb, (c, p, v) -> c.lessThanOrEqualTo(p, (Comparable) v));
        }
        return getPredicate(criteria, root, cb, (c, p, v) -> c.lessThanOrEqualTo(p, (Comparable) v));
    }

    private Predicate getGretherThanPredicate(Criteria criteria, Root<T> root, CriteriaBuilder cb) {
        validateValue(criteria.values());
        if (criteria.column().split(TABLE_FIELD_SEPARATOR).length > 1) {
            return getJoinPredicate(criteria, root, cb, (c, p, v) -> c.greaterThan(p, (Comparable) v));
        }
        return getPredicate(criteria, root, cb, (c, p, v) -> c.greaterThan(p, (Comparable) v));
    }

    private Predicate getGretherEqualPredicate(Criteria criteria, Root<T> root, CriteriaBuilder cb) {
        validateValue(criteria.values());
        if (criteria.column().split(TABLE_FIELD_SEPARATOR).length > 1) {
            return getJoinPredicate(criteria, root, cb, (c, p, v) -> c.greaterThanOrEqualTo(p, (Comparable) v));
        }
        return getPredicate(criteria, root, cb, (c, p, v) -> c.greaterThanOrEqualTo(p, (Comparable) v));
    }

    private Predicate getNoValueJoinPredicate(Criteria criteria, Root<T> root, CriteriaBuilder cb, NoValuePredicateProvider provider) {
        String name = criteria.column();
        Join<Object, Object> joinTable = getJoinTable(root, name);
        String[] joinFields = getFields(name);
        List<Predicate> predicates = new ArrayList<>();
        for (String field : joinFields) {
            predicates.add(provider.getPredicate(cb, joinTable.get(field)));
        }
        return cb.or(toArray(predicates));
    }

    private Predicate getNoValuePredicate(Criteria criteria, Root<T> root, CriteriaBuilder cb, NoValuePredicateProvider provider) {
        String[] fields = getFields(criteria.column());
        List<Predicate> predicates = new ArrayList<>();
        for (String field : fields) {
            predicates.add(provider.getPredicate(cb, root.get(field)));
        }
        return cb.or(toArray(predicates));
    }

    private Predicate getJoinPredicate(Criteria criteria, Root<T> root, CriteriaBuilder cb, PredicateProvider provider) {
        String name = criteria.column();
        Join<Object, Object> joinTable = getJoinTable(root, name);
        String[] joinFields = getFields(name);
        List<Predicate> predicates = new ArrayList<>();
        for (String field : joinFields) {
            Object value = valueConverter.getValue(joinTable.get(field), criteria.values().get(0));
            predicates.add(provider.getPredicate(cb, joinTable.get(field), value));
        }
        return cb.or(toArray(predicates));
    }

    private Predicate getPredicate(Criteria criteria, Root<T> root, CriteriaBuilder cb, PredicateProvider provider) {
        String[] fields = getFields(criteria.column());
        List<Predicate> predicates = new ArrayList<>();
        for (String field : fields) {
            Object value = valueConverter.getValue(root.get(field), criteria.values().get(0));
            predicates.add(provider.getPredicate(cb, root.get(field), value));
        }
        return cb.or(toArray(predicates));
    }

    private <D> Predicate getMultiValueJoinPredicate(Criteria criteria, Root<T> root, CriteriaBuilder cb, MultiValuePredicateProvider<D> provider) {
        String name = criteria.column();
        Join<Object, Object> joinTable = getJoinTable(root, name);
        String[] joinFields = getFields(name);
        List<Predicate> predicates = new ArrayList<>();
        for (String field : joinFields) {
            Set<Object> valueSet = criteria.values().stream()
                    .map(value -> valueConverter.getValue(joinTable.get(field), value))
                    .collect(Collectors.toSet());
            predicates.add(provider.getPredicate(cb, joinTable.get(field), valueSet));
        }
        return cb.or(toArray(predicates));
    }

    private <D> Predicate getMultiValuePredicate(Criteria criteria, Root<T> root, CriteriaBuilder cb, MultiValuePredicateProvider<D> provider) {
        String[] fields = getFields(criteria.column());
        List<Predicate> predicates = new ArrayList<>();
        for (String field : fields) {
            Set<Object> valueSet = criteria.values().stream()
                    .map(value -> valueConverter.getValue(root.get(field), value))
                    .collect(Collectors.toSet());
            predicates.add(provider.getPredicate(cb, root.get(field), valueSet));
        }
        return cb.or(toArray(predicates));
    }

    private Predicate getContainJoinPredicate(Criteria criteria, Root<T> root, CriteriaBuilder cb, StringPredicateProvider provider) {
        String name = criteria.column();
        Join<Object, Object> joinTable = getJoinTable(root, name);
        String[] joinFields = getFields(name);
        List<Predicate> predicates = new ArrayList<>();
        for (String field : joinFields) {
            criteria.values().stream()
                    .map(value -> provider.getPredicate(cb, joinTable.get(field), value))
                    .forEach(predicates::add);
        }
        return cb.or(toArray(predicates));
    }

    private Predicate getContainPredicate(Criteria criteria, Root<T> root, CriteriaBuilder cb, StringPredicateProvider provider) {
        List<Predicate> predicates = new ArrayList<>();
        String[] fields = getFields(criteria.column());
        for (String field : fields) {
            criteria.values().stream()
                    .map(value -> provider.getPredicate(cb, root.get(field), value))
                    .forEach(predicates::add);
        }
        return cb.or(toArray(predicates));
    }

    private Predicate[] toArray(List<Predicate> predicates) {
        return predicates.toArray(new Predicate[0]);
    }

    private String getContainsEscValue(String value) {
        String newVal = value
                .replace("\\", "\\\\")
                .replace("_", "\\_")
                .replace("[", "\\[")
                .replace("]", "\\]")
                .replace("-", "\\-")
                .replace("%", "\\%");
        return "%".concat(newVal).concat("%").toLowerCase();
    }

    private void validateValue(List<Object> values) {
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