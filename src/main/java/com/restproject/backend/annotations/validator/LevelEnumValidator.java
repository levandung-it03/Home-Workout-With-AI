package com.restproject.backend.annotations.validator;

import com.restproject.backend.annotations.constraint.LevelEnumConstraint;
import com.restproject.backend.enums.Level;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

import java.util.Objects;

@FieldDefaults(level = AccessLevel.PRIVATE)
public class LevelEnumValidator implements ConstraintValidator<LevelEnumConstraint, Integer> {

    @Override
    public void initialize(LevelEnumConstraint constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(Integer value, ConstraintValidatorContext context) {
        if (Objects.isNull(value))  return true;    //--@NotNull will handle this error
        return (Level.BEGINNER.getLevel() <= value) && (value <= Level.ADVANCE.getLevel());
    }
}
