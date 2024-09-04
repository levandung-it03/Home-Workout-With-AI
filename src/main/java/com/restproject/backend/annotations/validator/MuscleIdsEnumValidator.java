package com.restproject.backend.annotations.validator;

import com.restproject.backend.annotations.constraint.MuscleIdsEnumConstraint;
import com.restproject.backend.enums.ErrorCodes;
import com.restproject.backend.enums.Muscle;
import com.restproject.backend.exceptions.ApplicationException;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

import java.util.Collection;
import java.util.Objects;

@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MuscleIdsEnumValidator implements ConstraintValidator<MuscleIdsEnumConstraint, Collection<Integer>> {

    @Override
    public void initialize(MuscleIdsEnumConstraint constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(Collection<Integer> value, ConstraintValidatorContext context) {
        if (Objects.isNull(value) || value.isEmpty())  return true;  //--Rely on @NotEmpty.
        try {
            value.forEach(Muscle::getById);
        } catch (ApplicationException e) {
            return false;
        } catch (Exception e) {
            log.info("Unaware Exception was throw by @MuscleIdsEnumValidator: " + e);
            return false;
        }
        return true;
    }
}
