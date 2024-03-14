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

package com.smartsensesolutions.commons.dao.operator;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

/**
 * All supported conditional operators for criteria.
 */
@AllArgsConstructor
@Getter
public enum Operator {

    CONTAIN("like"),
    CONTAIN_WITH_WILDCARD("like_with_wildcard"),
    NOT_CONTAIN("!like"),
    NOT_CONTAIN_WITH_WILDCARD("!like_with_wildcard"),
    EQUALS("="),
    NOT_EQUAL("!="),
    IN("in"),
    NOT_IN("!in"),
    GREATER_THAN(">"),
    LESSER_THAN("<"),
    GREATER_EQUALS(">="),
    LESSER_EQUALS("<="),
    TRUE("is_true"),
    FALSE("is_false"),
    NULL("is_null"),
    NOT_NULL("!is_null");

    private static final Map<String, Operator> map = new HashMap<>();
    private final String operatorValue;

    static {
        for (Operator legEnum : Operator.values()) {
            map.put(legEnum.operatorValue, legEnum);
        }
    }

    public static Operator getOperator(String operatorValue) {
        return map.get(operatorValue);
    }

}
