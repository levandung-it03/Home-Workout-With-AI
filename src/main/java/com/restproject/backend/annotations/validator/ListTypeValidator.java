package com.restproject.backend.annotations.validator;

import com.restproject.backend.annotations.constraint.ListTypeConstraint;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

import java.util.Collection;
import java.util.Objects;

@FieldDefaults(level = AccessLevel.PRIVATE)
public class ListTypeValidator implements ConstraintValidator<ListTypeConstraint, Collection<?>> {
    Class<?> type;

    @Override
    public void initialize(ListTypeConstraint constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
        this.type = constraintAnnotation.type();
    }

    @Override
    public boolean isValid(Collection<?> value, ConstraintValidatorContext context) {
        if (Objects.isNull(value))  //--Let @NotNull handle this one.
            return true;
        for (Object object: value)
            if (Objects.isNull(object) || !type.isInstance(object))
                return false;
        return true;
    }
}
