package com.restproject.backend.annotations.validator;

import com.restproject.backend.annotations.constraint.DobConstraint;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.factory.annotation.Value;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

public class DobValidator implements ConstraintValidator<DobConstraint, LocalDate> {
    @Value("${services.back-end.user-info.min-age}")
    private int minAge;

    @Override
    public void initialize(DobConstraint constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(LocalDate value, ConstraintValidatorContext context) {
        if (Objects.isNull(value))  return true;
        return ChronoUnit.YEARS.between(value, LocalDate.now()) >= minAge;
    }
}
