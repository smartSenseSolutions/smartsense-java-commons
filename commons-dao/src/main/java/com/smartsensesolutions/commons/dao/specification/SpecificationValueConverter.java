package com.smartsensesolutions.commons.dao.specification;

import jakarta.persistence.criteria.Path;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Objects;
import java.util.UUID;

@Component
public class SpecificationValueConverter {

    public Object getValue(Path path, Object value) {
        if (Objects.equals(path.getJavaType().getName(), "java.util.Date")) {
            return new Date(Long.parseLong(value.toString()));
        } else if (Objects.equals(path.getJavaType().getName(), "java.util.UUID")) {
            return UUID.fromString(value.toString());
        } else if (path.getJavaType().isEnum()) {
            Class<? extends Enum> enumType = (Class<? extends Enum>) path.getJavaType();
            for (Enum enumConstant : enumType.getEnumConstants()) {
                if (enumConstant.toString().equals(value) || String.valueOf(enumConstant.ordinal()).equals(value)) {
                    return enumConstant;
                }
            }
            throw new IllegalArgumentException("Invalid Enum Value");
        } else {
            return value;
        }
    }
}
