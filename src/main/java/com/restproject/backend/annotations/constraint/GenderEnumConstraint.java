package com.restproject.backend.annotations.constraint;

import com.restproject.backend.annotations.validator.GenderEnumValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Documented
@Constraint(validatedBy = { GenderEnumValidator.class })
@Target({ FIELD })
@Retention(RUNTIME)
public @interface GenderEnumConstraint {
    String message() default "Invalid gender";
    Class<?>[] groups() default { };
    Class<? extends Payload>[] payload() default { };
}
