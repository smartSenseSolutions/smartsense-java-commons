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

package com.smartsensesolutions.commons.dao.base;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

/**
 * Each @{@link org.springframework.data.repository.Repository} must be extended with BaseRepository.
 *
 * @param <T>  - Indicates the @{@link jakarta.persistence.Entity} class.
 * @param <ID> - Indicates the @{@link jakarta.persistence.Id} column from entity class.
 */
@NoRepositoryBean
public interface BaseRepository<T extends BaseEntity, ID> extends JpaRepository<T, ID> {

    /**
     * Method used for fetch all elements from the entity class.
     *
     * @param specification - Indicates the JPA specification used for search on BaseEntity.
     * @param pageable      - Indicates the pageable request for JPA queries.
     * @return Page response with entity details.
     */
    Page<T> findAll(Specification<T> specification, Pageable pageable);

    /**
     * Method used for fetch count based on the JPA specification.
     *
     * @param specification - Indicates the JPA specification used for search on BaseEntity.
     * @return Indicates the elements count.
     */
    long count(Specification<T> specification);
}