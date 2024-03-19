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

package com.smartsensesolutions.commons.dao.base;

import com.smartsensesolutions.commons.dao.filter.FilterRequest;
import com.smartsensesolutions.commons.dao.filter.sort.SortType;
import com.smartsensesolutions.commons.dao.operator.CriteriaOperator;
import com.smartsensesolutions.commons.dao.specification.SpecificationUtil;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Objects;

/**
 * Each @{@link org.springframework.stereotype.Service} must be extended with BaseService.
 *
 * @param <E> - Indicates the @{@link jakarta.persistence.Entity} class.
 * @param <I> - Indicates the @{@link jakarta.persistence.Id} column from entity class.
 */
public abstract class BaseService<E extends BaseEntity, I> {

    /**
     * Method needs to Override by each service which extends BaseService. It provides Repository of entity type to perform operation
     *
     * @return BaseRepository of @{@link org.springframework.stereotype.Repository} interface.
     */
    protected abstract BaseRepository<E, I> getRepository();

    /**
     * Method needs to Override by each service which extends BaseService. It provides Filter functionality for entity class
     *
     * @return Specification of @{@link jakarta.persistence.Entity} class
     */
    protected SpecificationUtil<E> getSpecificationUtil() {
        return new SpecificationUtil<>();
    }

    /**
     * Method used for save entity.
     *
     * @param newEntity - Indicates the entity that needs to be saved.
     * @return Newly created or updated entity
     */
    public E create(E newEntity) {
        return getRepository().save(newEntity);
    }

    /**
     * Method used for save entities.
     *
     * @param iterable - Indicates the iterable entities.
     * @return Newly created or updated entities
     */
    public List<E> create(Iterable<E> iterable) {
        return getRepository().saveAll(iterable);
    }

    /**
     * Method used for fetch Entity by @{@link jakarta.persistence.Id} column.
     *
     * @param entityId - Indicates the entityId.
     * @return Entity
     */
    @Transactional(propagation = Propagation.NOT_SUPPORTED, readOnly = true)
    public E get(I entityId) {
        return getRepository().findById(entityId).orElse(null);
    }

    /**
     * Method used for fetch multiple entities by @{@link jakarta.persistence.Id} column.
     *
     * @param entityIds - Indicates the entityIds.
     * @return List of Entity
     */
    @Transactional(propagation = Propagation.NOT_SUPPORTED, readOnly = true)
    public List<E> get(Iterable<I> entityIds) {
        return getRepository().findAllById(entityIds);
    }

    /**
     * Method used for fetch all records from the entity.
     *
     * @return List of Entity
     */
    @Transactional(propagation = Propagation.NOT_SUPPORTED, readOnly = true)
    public List<E> getAll() {
        return getRepository().findAll();
    }

    /**
     * Method used for delete entity by entityId.
     *
     * @param entityId - Indicates the EntityId
     */
    public void delete(I entityId) {
        getRepository().deleteById(entityId);
    }

    /**
     * Method used for check whether given entityId exist or not.
     *
     * @param entityId - Indicates the entityId
     * @return boolean
     */
    @Transactional(readOnly = true)
    public boolean existsById(I entityId) {
        return getRepository().existsById(entityId);
    }

    /**
     * Method used for generate Page response based on the given FilterRequest.
     *
     * @param filter - Indicates the FilterRequest
     * @return Page of Entity
     */
    @Transactional(readOnly = true)
    public Page<E> filter(FilterRequest filter) {
        return filter(getSpecificationFromFilterRequest(filter), filter);
    }

    /**
     * Method used for generate Page response based on the given FilterRequest and specification.
     *
     * @param specification - Indicates the custom specification that needs to apply.
     * @param filter        - Indicates the FilterRequest used to get page and sort parameters, not the criteria
     *                      parameters
     * @return Page of Entity
     */
    @Transactional(readOnly = true)
    private Page<E> filter(Specification<E> specification, FilterRequest filter) {
        try {
            PageRequest pageRequest = getPageRequest(filter);
            if (Objects.isNull(specification)) {
                return getRepository().findAll(pageRequest);
            }
            return getRepository().findAll(specification, pageRequest);
        } catch (InvalidDataAccessApiUsageException ex) {
            throw new IllegalArgumentException("field type not support operator or value", ex);
        }
    }

    /**
     * Method used for fetch count based on the FilterRequest.
     *
     * @param filter - Indicates the FilterRequest.
     * @return Long
     */
    @Transactional(readOnly = true)
    public long count(FilterRequest filter) {
        return count(getSpecificationFromFilterRequest(filter));
    }

    /**
     * Method used for fetch count based on the specification.
     *
     * @param specification - Indicates the specification.
     * @return Long
     */
    @Transactional(readOnly = true)
    public long count(Specification<E> specification) {
        return getRepository().count(specification);
    }

    /**
     * Method used for generate specification from {@link FilterRequest}
     *
     * @param request - Indicates the FilterRequest
     * @return Specification of Entity
     */
    private Specification<E> getSpecificationFromFilterRequest(FilterRequest request) {
        if (CollectionUtils.isEmpty(request.getCriteria())) {
            return null;
        }
        if (CollectionUtils.isEmpty(request.getOrCriteria())) {
            if (CriteriaOperator.OR.equals(request.getCriteriaOperator())) {
                return getSpecificationUtil().generateOrSpecification(request.getCriteria());
            }
            return getSpecificationUtil().generateAndSpecification(request.getCriteria());
        } else {
            Specification<E> and = getSpecificationUtil().generateAndSpecification(request.getCriteria());
            Specification<E> or = getSpecificationUtil().generateOrSpecification(request.getOrCriteria());
            if (CriteriaOperator.OR.equals(request.getCriteriaOperator())) {
                return and.or(or);
            }
            return and.and(or);
        }
    }

    /**
     * Generate page request from {@link FilterRequest}.
     *
     * @param filter - Indicates the FilterRequest
     * @return PageRequest
     */
    private PageRequest getPageRequest(FilterRequest filter) {
        if (filter.getSize() <= 0) {
            filter.setSize(Integer.MAX_VALUE);
        }
        Sort sort = Sort.unsorted();
        if (!CollectionUtils.isEmpty(filter.getSort())) {
            List<Sort.Order> orders = filter.getSort().stream().map(this::toSQLSort).toList();
            sort = Sort.by(orders);
        }
        return PageRequest.of(filter.getPage(), filter.getSize(), sort);
    }

    private Sort.Order toSQLSort(com.smartsensesolutions.commons.dao.filter.sort.Sort sort) {
        if (sort.getSortType().equals(SortType.ASC)) {
            return Sort.Order.asc(sort.getColumn());
        } else {
            return Sort.Order.desc(sort.getColumn());
        }
    }
}
