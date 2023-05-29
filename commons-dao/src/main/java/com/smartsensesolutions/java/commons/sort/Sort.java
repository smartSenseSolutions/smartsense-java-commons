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

package com.smartsensesolutions.java.commons.sort;

import com.smartsensesolutions.java.commons.FilterRequest;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Cort used in {@link FilterRequest} to provide sorting.
 */
@NoArgsConstructor
@Getter
@Setter
public class Sort {

    /**
     * Indicates the variable name that used in @{@link jakarta.persistence.Entity} class.
     */
    private String column;
    /**
     * Indicates the ASD or DESC sort on column.
     * Values used from the {@link SortType}.
     */
    private SortType sortType;

}
