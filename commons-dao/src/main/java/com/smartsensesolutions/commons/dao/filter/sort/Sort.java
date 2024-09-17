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

package com.smartsensesolutions.commons.dao.filter.sort;

/**
 * Sort used in {@link com.smartsensesolutions.commons.dao.filter.FilterRequest} to provide sorting.
 *
 * @param column   - Indicates the variable name that used in @{@link jakarta.persistence.Entity} class.
 * @param sortType - Indicates the ASD or DESC sort on column. Values used from the {@link SortType}.
 */
public record Sort(String column, SortType sortType) {
}
