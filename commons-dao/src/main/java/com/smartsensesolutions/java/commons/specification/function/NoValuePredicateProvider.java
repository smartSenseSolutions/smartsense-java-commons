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
package com.smartsensesolutions.java.commons.specification.function;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;

/**
 * No value Predicate Provider
 *
 * @param <D> Field data type
 * @author Sunil Kanzar
 * @since 20th Fab 2024
 */
@FunctionalInterface
public interface NoValuePredicateProvider<D> {
    Predicate getPredicate(CriteriaBuilder criteriaBuilder, Path<D> path);
}
