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

package com.smartsensesolutions.java.commons.filter;

import com.smartsensesolutions.java.commons.FilterRequest;
import com.smartsensesolutions.java.commons.operator.Operator;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * FilterCriteria used in {@link FilterRequest} to provide custom search.
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class FilterCriteria {
    /**
     * Indicates the variable name that used in @{@link jakarta.persistence.Entity} class.
     */
    @NotBlank(message = "{NotNull.FilterCriteria.column}")
    private String column;
    /**
     * Indicates the value from the {@link Operator}.
     */
    @NotNull(message = "{NotNull.FilterCriteria.operator}")
    private String operator;
    /**
     * Values that needs to be matched while querying to @{@link jakarta.persistence.Entity} class.
     */
    private List<String> values;

}
