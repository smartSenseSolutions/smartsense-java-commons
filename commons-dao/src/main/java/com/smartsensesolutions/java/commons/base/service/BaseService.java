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

package com.smartsensesolutions.java.commons.base.service;

import com.smartsensesolutions.java.commons.FilterRequest;
import com.smartsensesolutions.java.commons.base.entity.BaseEntity;
import com.smartsensesolutions.java.commons.base.repository.BaseRepository;
import com.smartsensesolutions.java.commons.criteria.CriteriaOperator;
import com.smartsensesolutions.java.commons.sort.SortType;
import com.smartsensesolutions.java.commons.specification.SpecificationUtil;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

/**
 * Each @{@link org.springframework.stereotype.Service} must be extended with BaseService.
 *
 * @param <E>  - Indicates the @{@link jakarta.persistence.Entity} class.
 * @param <ID> - Indicates the @{@link jakarta.persistence.Id} column from entity class.
 */
public abstract class BaseService<E extends BaseEntity, ID> {

    /**
     * Method needs to Override by each service which extends BaseService.
     *
     * @return BaseRepository of @{@link org.springframework.stereotype.Repository} interface.
     */
    protected abstract BaseRepository<E, ID> getRepository();

    /**
     * Method needs to Override by each service which extends BaseService.
     *
     * @return Specification of @{@link jakarta.persistence.Entity} class
     */
    protected abstract SpecificationUtil<E> getSpecificationUtil();

    /**
     * Method used for save entity.
     *
     * @param newEntity - Indicates the entity that needs to be saved.
     * @return Entity
     */
    public E create(E newEntity) {
        return this.getRepository().save(newEntity);
    }

    /**
     * Method used for save entities.
     *
     * @param iterable - Indicates the iterable entities.
     * @return List of Entity
     */
    public List<E> create(Iterable<E> iterable) {
        return this.getRepository().saveAll(iterable);
    }

    /**
     * Method used for fetch Entity by @{@link jakarta.persistence.Id} column.
     *
     * @param entityId - Indicates the entityId.
     * @return Entity
     */
    @Transactional(propagation = Propagation.NOT_SUPPORTED, readOnly = true)
    public E get(ID entityId) {
        return this.getRepository().findById(entityId).orElse(null);
    }

    /**
     * Method used for fetch multiple entities by @{@link jakarta.persistence.Id} column.
     *
     * @param entityIds - Indicates the entityIds.
     * @return List of Entity
     */
    @Transactional(propagation = Propagation.NOT_SUPPORTED, readOnly = true)
    public List<E> get(Iterable<ID> entityIds) {
        return this.getRepository().findAllById(entityIds);
    }

    /**
     * Method used for fetch all records from the entity.
     *
     * @return List of Entity
     */
    @Transactional(propagation = Propagation.NOT_SUPPORTED, readOnly = true)
    public List<E> getAll() {
        return this.getRepository().findAll();
    }

    /**
     * Method used for delete entity by entityId.
     *
     * @param entityId - Indicates the EntityId
     */
    public void delete(ID entityId) {
        this.getRepository().deleteById(entityId);
    }

    /**
     * Method used for check whether given entityId exist or not.
     *
     * @param entityId - Indicates the entityId
     * @return boolean
     */
    public boolean existsById(ID entityId) {
        return this.getRepository().existsById(entityId);
    }

    /**
     * Method used for generate Page response based on the given FilterRequest.
     *
     * @param filter - Indicates the FilterRequest
     * @return Page of Entity
     */
    @Transactional(readOnly = true)
    public Page<E> filter(FilterRequest filter) {
        return this.filter(this.getSpecificationFromFilterRequest(filter), filter);
    }

    /**
     * Method used for generate Page response based on the given FilterRequest.
     *
     * @param specification - Indicates the custom specification that needs to apply.
     * @param filter        - Indicates the FilterRequest
     * @return Page of Entity
     */
    @Transactional(readOnly = true)
    public Page<E> filter(Specification<E> specification, FilterRequest filter) {
        try {
            PageRequest pageRequest = this.getPageRequest(filter);
            if (Objects.isNull(filter.getCriteria()) || filter.getCriteria().isEmpty()) {
                return this.getRepository().findAll(pageRequest);
            }
            return this.getRepository().findAll(specification, pageRequest);
        } catch (InvalidDataAccessApiUsageException ex) {
            throw new IllegalArgumentException("field type not support operator or value", ex);
        }
    }

    /**
     * Method used for generate page response based on the search and default filter request.
     *
     * @param searchRequest  - Indicates the Search Filter Request
     * @param defaultRequest - Indicates the Default Filter Request
     * @param operator       - Indicates the Criteria operator that will use between these requests.
     * @return Page of Entity
     */
    @Transactional(readOnly = true)
    public Page<E> filter(FilterRequest searchRequest, FilterRequest defaultRequest, CriteriaOperator operator) {
        Specification<E> searchSpecification = this.getSpecificationFromFilterRequest(searchRequest);
        if (Objects.isNull(searchSpecification)) {
            Specification<E> defaultSpecification = this.getSpecificationFromFilterRequest(defaultRequest);
            if (Objects.isNull(defaultSpecification)) {
                return this.getRepository().findAll(this.getPageRequest(searchRequest));
            }
            return this.filter(defaultSpecification, defaultRequest);
        }
        searchSpecification = this.appendDefaultCriteriaWithSearchCriteria(searchSpecification, defaultRequest, operator);
        return this.filter(searchSpecification, searchRequest);
    }

    /**
     * Method used for append default criteria with search criteria
     *
     * @param searchSpecification - Indicates the search specification
     * @param defaultRequest      - Indicates the default Filter request
     * @param operator            - Indicates the operator
     * @return Specification of Entity
     */
    private Specification<E> appendDefaultCriteriaWithSearchCriteria(Specification<E> searchSpecification, FilterRequest defaultRequest, CriteriaOperator operator) {
        Specification<E> defaultSpecification = this.getSpecificationFromFilterRequest(defaultRequest);
        if (Objects.nonNull(defaultSpecification) && Objects.nonNull(operator) && operator.equals(CriteriaOperator.AND)) {
            searchSpecification = searchSpecification.and(defaultSpecification);
        }
        if (Objects.nonNull(defaultSpecification) && Objects.nonNull(operator) && operator.equals(CriteriaOperator.OR)) {
            searchSpecification = searchSpecification.or(defaultSpecification);
        }
        return searchSpecification;
    }

    /**
     * Method used for fetch count based on the FilterRequest.
     *
     * @param request - Indicates the FilterRequest.
     * @return Long
     */
    @Transactional(readOnly = true)
    public long count(FilterRequest request) {
        return this.count(this.getSpecificationFromFilterRequest(request));
    }

    /**
     * Method used for fetch count based on the specification.
     *
     * @param specification - Indicates the specification.
     * @return Long
     */
    public long count(Specification<E> specification) {
        return this.getRepository().count(specification);
    }

    /**
     * Method used for fetch count based on the search and default filter request.
     *
     * @param searchRequest  - Indicates the Search Filter Request
     * @param defaultRequest - Indicates the Default Filter Request
     * @param operator       - Indicates the Criteria operator that will use between these requests.
     * @return Long
     */
    public long count(FilterRequest searchRequest, FilterRequest defaultRequest, CriteriaOperator operator) {
        Specification<E> searchSpecification = this.getSpecificationFromFilterRequest(searchRequest);
        if (Objects.isNull(searchSpecification)) {
            Specification<E> defaultSpecification = this.getSpecificationFromFilterRequest(defaultRequest);
            if (Objects.isNull(defaultSpecification)) {
                return this.getRepository().count();
            }
            return this.count(defaultSpecification);
        }
        searchSpecification = this.appendDefaultCriteriaWithSearchCriteria(searchSpecification, defaultRequest, operator);
        return this.getRepository().count(searchSpecification);
    }

    /**
     * Method used for generate specification from {@link FilterRequest}
     *
     * @param request - Indicates the FilterRequest
     * @return Specification of Entity
     */
    private Specification<E> getSpecificationFromFilterRequest(FilterRequest request) {
        if (Objects.isNull(request.getCriteria()) || request.getCriteria().isEmpty()) {
            return null;
        }
        if (Objects.isNull(request.getCriteriaOperator()) || request.getCriteriaOperator().equals(CriteriaOperator.AND)) {
            return this.getSpecificationUtil().generateSpecification(request.getCriteria());
        }
        return this.getSpecificationUtil().generateOrSpecification(request.getCriteria());
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
        if (Objects.isNull(filter.getSort())) {
            return PageRequest.of(filter.getPage(), filter.getSize());
        }
        org.springframework.data.domain.Sort sort = org.springframework.data.domain.Sort.by(filter.getSort().getSortType() == SortType.ASC ? org.springframework.data.domain.Sort.Direction.ASC : Sort.Direction.DESC, filter.getSort().getColumn().split(","));
        return PageRequest.of(filter.getPage(), filter.getSize(), sort);
    }

}
