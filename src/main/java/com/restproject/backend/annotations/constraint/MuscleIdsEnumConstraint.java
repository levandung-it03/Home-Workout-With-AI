package com.restproject.backend.annotations.constraint;

import com.restproject.backend.annotations.validator.LevelEnumValidator;
import com.restproject.backend.annotations.validator.MuscleIdsEnumValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Documented
@Constraint(validatedBy = { MuscleIdsEnumValidator.class })
@Target({ FIELD })
@Retention(RUNTIME)
public @interface MuscleIdsEnumConstraint {
    String message() default "Invalid muscle ids";
    Class<?>[] groups() default { };
    Class<? extends Payload>[] payload() default { };
}
